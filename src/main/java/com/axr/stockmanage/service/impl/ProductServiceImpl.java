package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.entity.Stock;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.vo.ProductVO;
import com.axr.stockmanage.service.ProductService;
import com.axr.stockmanage.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    public Integer addProduct(ProductAddDTO dto) {
        if (productMapper.findByName(dto.getName()) != null) {
            throw new BusinessException("商品已存在");
        }
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
        productMapper.addProduct(product);
        Integer productId = product.getId();
        Stock stock = Stock.builder()
                .productId(productId)
                .quantity(0)
                .build();
        stockMapper.add(stock);
        return productId;
    }

    @Override
    @Transactional
    public Integer deleteProduct(int id) {
        if (productMapper.findById(id) == null) {
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
        if (productMapper.findById(dto.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        return productMapper.updateProduct(dto);
    }

    @Override
    @Transactional
    public boolean updateProductStatus(int id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        int status = product.getStatus();
        productMapper.updateStatus(id, status ^ 1);
        return true;
    }

    @Override
    public List<ProductVO> list(Product product) {
        return productMapper.find(product);
    }

    @Override
    @Transactional
    public ProductVO purchaseProduct(ProductPurchaseDTO dto) {
        if (dto.getProductId() == null || dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new BusinessException("商品信息错误");
        }
        Integer productId = dto.getProductId();
        stockService.updateStock(StockDTO.builder()
                .productId(productId)
                .updateQuantity(dto.getQuantity() * -1)
                .build());
        List<ProductVO> products = productMapper.find(Product.builder().id(productId).build());
        if (products.isEmpty()) {
            throw new BusinessException("商品已售罄");
        }
        return products.get(0);
    }
}
