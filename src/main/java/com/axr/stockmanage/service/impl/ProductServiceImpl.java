package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.Product;
import com.axr.stockmanage.model.Stock;
import com.axr.stockmanage.model.dto.ProductDTO;
import com.axr.stockmanage.service.ProductService;
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
    private StockMapper stockMapper;

    /**
     * 添加商品
     *
     * @param dto 商品信息
     * @return product id
     */
    @Override
    @Transactional
    public Integer addProduct(ProductDTO dto) {
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
                .quantity(dto.getQuantity())
                .build();
        stockMapper.add(stock);
        return productId;
    }

    /**
     * 更新商品信息
     *
     * @param product 商品对象
     */
    @Override
    public Integer updateProduct(Product product) {
        return productMapper.updateProduct(product);
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     */
    @Override
    @Transactional
    public Integer deleteProduct(int id) {
        if (productMapper.findById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        int stockResult = stockMapper.deleteByProductId(id);
        int productResult = productMapper.deleteProduct(id);
        if (stockResult != 1 || productResult != 1) {
            throw new BusinessException("删除商品失败");
        }
        return productResult;
    }

    /**
     * 更新商品状态
     *
     * @param id 商品ID
     */
    @Transactional
    @Override
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
    public List<Product> listAll() {
        return productMapper.findAll();
    }
}
