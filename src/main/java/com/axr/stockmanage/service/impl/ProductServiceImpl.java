package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.model.Product;
import com.axr.stockmanage.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加商品
     *
     * @param product 商品对象
     */
    @Transactional
    @Override
    public void addProduct(Product product) {
        productMapper.addProduct(product);
    }

    /**
     * 更新商品信息
     *
     * @param product 商品对象
     */
    @Transactional
    @Override
    public void updateProduct(Product product) {
        productMapper.updateProduct(product);
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     */
    @Transactional
    @Override
    public void deleteProduct(int id) {
        productMapper.deleteProduct(id);
    }

    /**
     * 更新商品状态
     *
     * @param id     商品ID
     * @param status 商品状态（0表示下架，1表示上架）
     */
    @Transactional
    @Override
    public void updateProductStatus(int id, String status) {
        productMapper.updateStatus(id, status);
    }
}
