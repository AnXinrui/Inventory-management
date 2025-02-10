package com.axr.stockmanage.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.entity.Stock;
import com.axr.stockmanage.model.vo.ProductVO;
import com.axr.stockmanage.service.ProductService;
import com.axr.stockmanage.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private StockMapper stockMapper;

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
        stockMapper.add(stock);
        return productId;
    }

    @Override
    @Transactional
    public Integer deleteProduct(long id) {
        if (productMapper.findByIdForUpdate(id) == null) {
            throw new BusinessException("该商品不存在");
        }
        int stockResult = stockMapper.deleteByProductId(id);
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
    @Cached(name = "productCache:", key = "'productId:' + #id", expire = 30, timeUnit = TimeUnit.SECONDS)
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
    public ProductVO purchaseProduct(ProductPurchaseDTO dto) {
        if (dto.getProductId() == null || dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new BusinessException("商品信息错误");
        }
        Long productId = dto.getProductId();
        Product product = productMapper.findByIdForUpdate(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        stockService.updateStock(StockDTO.builder()
                .productId(productId)
                .updateQuantity(dto.getQuantity() * -1)
                .build());
        List<ProductVO> products = productMapper.find(Product.builder().id(productId).build());
        if (products.isEmpty()) {
            throw new BusinessException("系统错误");
        }
        return products.get(0);
    }
}
