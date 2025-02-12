package com.axr.stockmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.common.RedisIdWorker;
import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Order;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.entity.Stock;
import com.axr.stockmanage.model.vo.ProductVO;
import com.axr.stockmanage.service.OrderService;
import com.axr.stockmanage.service.ProductService;
import com.axr.stockmanage.service.StockService;
import com.axr.stockmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private StockService stockService;
    @Resource
    private RedisIdWorker idWorker;
    @Resource
    private OrderService orderService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;

    /**
     * 降级开关：控制是否统计商品浏览次数
     */
    @Value("${feature.toggle.viewCount:true}")
    private boolean enableViewCount;

    private final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private static final String PREFIX = "seckill:stock:";

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new OrderHandler());
    }

    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @PreDestroy
    public void destroy() {
        if (!SECKILL_ORDER_EXECUTOR.isShutdown()) {
            SECKILL_ORDER_EXECUTOR.shutdown();
        }
    }

    private class OrderHandler implements Runnable {

        private boolean running = true;
        String queueName = "stream.orders";

        @Override
        public void run() {
            while (running) {
                try {
                    // 获取队列订单信息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    // 解析信息
                    MapRecord<String, Object, Object> mapRecord = list.get(0);
                    Map<Object, Object> values = mapRecord.getValue();
                    Order order = BeanUtil.fillBeanWithMap(values, new Order(), true);

                    handleOrder(order);

                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", mapRecord.getId());

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        private void handleOrder(Order order) {
            long userId = order.getUserId();
            ReentrantLock lock = lockMap.computeIfAbsent(userId, k -> new ReentrantLock());
            lock.lock();
            try {
                int count = orderService.countByUserId(userId);
                if (count > 0) {
                    log.error("每人只能参与一次秒杀");
                }

                long productId = order.getProductId();
                StockDTO dto = StockDTO.builder()
                        .productId(productId)
                        .updateQuantity(-1)
                        .build();
                int res = stockService.updateStockWithCAS(dto);
                if (res < 1) {
                    log.error("扣减失败");
                }

                orderService.addOrder(order);
            } finally {
                lock.unlock();
                lockMap.remove(userId);
            }
        }
    }

    @Override
    @Transactional
    public Long addProduct(ProductAddDTO dto) {
        if (productMapper.findByName(dto.getName()) != null) {
            throw new BusinessException("商品已存在");
        }
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
        productMapper.addProduct(product);
        Long productId = product.getId();
        Stock stock = Stock.builder()
                .productId(productId)
                .quantity(0)
                .build();
        stockService.addStock(stock);
        return productId;
    }

    @Override
    @Transactional
    public Integer deleteProduct(long id) {
        if (productMapper.findByIdForUpdate(id) == null) {
            throw new BusinessException("该商品不存在");
        }
        int stockResult = stockService.deleteStock(id);
        int productResult = productMapper.deleteProduct(id);
        if (stockResult != 1 || productResult != 1) {
            throw new BusinessException("删除商品失败");
        }
        return productResult;
    }

    @Override
    @Transactional
    public Integer updateProduct(ProductUpdateDTO dto) {
        Product product = productMapper.findByIdForUpdate(dto.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return productMapper.updateProduct(dto);
    }

    @Override
    @Transactional
    public boolean updateProductStatus(long id) {
        Product product = productMapper.findByIdForUpdate(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        int status = product.getStatus();
        if (status != 0 && status != 1) {
            throw new BusinessException("商品状态异常");
        }
        productMapper.updateStatus(id, status ^ 1);
        return true;
    }

    @Override
    public Product findById(long id) {
        Product product = productMapper.findById(id);
        if (product != null && enableViewCount) {
            String redisKey = "product:viewCount:" + id;
            stringRedisTemplate.opsForValue().increment(redisKey, 1);
            if (stringRedisTemplate.getExpire(redisKey) == -1) {
                stringRedisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
            }
        }
        return productMapper.findById(id);
    }

    @Override
    public List<ProductVO> list(Product product) {
        return productMapper.find(product);
    }

    @Override
    public List<ProductVO> listAll() {
        return productMapper.listAll();
    }

    @Override
    @Transactional
    public boolean purchaseProduct(ProductPurchaseDTO dto) {
        if (dto.getProductId() == null || dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new BusinessException("商品信息错误");
        }
        Long productId = dto.getProductId();
        Product product = productMapper.findByIdForUpdate(productId);
        Stock stock = stockService.findByProductId(productId);

        if (product == null || product.getStatus() != 1 || stock == null) {
            throw new BusinessException("商品不存在或已下架!");
        }
        if (stock.getQuantity() < dto.getQuantity()) {
            throw new BusinessException("库存不足");
        }
        StockDTO stockDTO = StockDTO.builder()
                .productId(productId)
                .updateQuantity(dto.getQuantity())
                .build();
        int res = stockService.updateStockWithCAS(stockDTO);
        if (res < 1) {
            throw new BusinessException("扣减失败");
        }
        return true;
    }

    @Transactional
    public long secKillProduct2(Long id) {
        if (stringRedisTemplate.opsForValue().get(PREFIX + id.toString()) == null) {
            Product product = productMapper.findByIdForUpdate(id);
            Stock stock = stockService.findByProductId(id);

            if (product == null || product.getStatus() != 1 || stock == null) {
                throw new BusinessException("商品不存在或已下架");
            }
            if (stock.getQuantity() < 1) {
                throw new BusinessException("库存不足");
            }

            stringRedisTemplate.multi();
            stringRedisTemplate.opsForValue().set(PREFIX + product.getId(), stock.getQuantity().toString());
            stringRedisTemplate.exec();
        }
        if (Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(PREFIX + id.toString()))) < 1) {
            throw new BusinessException("库存不足");
        }

        long userId = userService.getUserId();
        ReentrantLock lock = lockMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();
        try {
            int count = orderService.countByUserId(userId);
            if (count > 0) {
                throw new BusinessException("每人只能参与一次秒杀");
            }

            StockDTO dto = StockDTO.builder()
                    .productId(id)
                    .updateQuantity(-1)
                    .build();
            int res = stockService.updateStockWithCAS(dto);
            if (res < 1) {
                throw new BusinessException("扣减失败");
            }
            Order order = new Order();
            long orderId = idWorker.nextId("order");
            order.setId(orderId);
            order.setUserId(userId);
            order.setProductId(id);
            order.setShopId(1L);
            order.setStatus(10);
            order.setCreateTime(LocalDateTime.now());
            orderService.addOrder(order);
            return orderId;
        } finally {
            lock.unlock();
            lockMap.remove(userId);
        }

    }

    @Override
    @Transactional
    public long secKillProduct(Long id) {
        if (stringRedisTemplate.opsForValue().get(PREFIX + id.toString()) == null) {
            Product product = productMapper.findByIdForUpdate(id);
            Stock stock = stockService.findByProductId(id);

            if (product == null || product.getStatus() != 1 || stock == null) {
                throw new BusinessException("商品不存在或已下架");
            }
            if (stock.getQuantity() < 1) {
                throw new BusinessException("库存不足");
            }

            stringRedisTemplate.opsForValue().set(PREFIX + product.getId(), stock.getQuantity().toString());
        }
        String stockStr = stringRedisTemplate.opsForValue().get(PREFIX + id.toString());
        if (stockStr == null) {
            throw new BusinessException("商品库存数据异常");
        }
        if (Integer.parseInt(stockStr) < 1) {
            throw new BusinessException("库存不足");
        }

        long orderId = idWorker.nextId("order");
        long userId = userService.getUserId();
        Long res = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                id.toString(),
                String.valueOf(userId),
                String.valueOf(orderId),
                String.valueOf(1L),
                String.valueOf(10),
                String.valueOf(LocalDateTime.now())
        );
        if (res == null) {
            throw new BusinessException("");
        }
        if (res != 0) {
            throw new BusinessException(res == 1 ? "库存不足" : "不能重复下单");
        }
        return orderId;
    }

    @Override
    public long getViewCount(long productId) {
        String redisKey = "product:viewCount:" + productId;
        String countStr = stringRedisTemplate.opsForValue().get(redisKey);
        return countStr == null ? 0L : Long.parseLong(countStr);
    }

    public boolean checkViewCount(Long productId) {
        if (!enableViewCount) {
            return true;
        }
        long count = getViewCount(productId);

        // 如果浏览人数超过阈值，则触发降级
        return count <= 10000;
    }
}
