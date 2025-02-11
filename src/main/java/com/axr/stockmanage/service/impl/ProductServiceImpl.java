package com.axr.stockmanage.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
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

    private final UserService userService = new UserService();

    private final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

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
            throw new BusinessException("商品不存在或已下架");
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

    @Override
    @Transactional
    public long secKillProduct(Long id) {
        Product product = productMapper.findByIdForUpdate(id);
        Stock stock = stockService.findByProductId(id);

        if (product == null || product.getStatus() != 1 || stock == null) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (stock.getQuantity() < 1) {
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
}
