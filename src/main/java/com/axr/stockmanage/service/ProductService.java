package com.axr.stockmanage.service;

import com.axr.stockmanage.model.Product;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
public interface ProductService {

    /**
     * 添加商品
     *
     * @param product 商品对象
     */
    void addProduct(Product product);

    /**
     * 更新商品信息
     *
     * @param product 商品对象
     */
    void updateProduct(Product product);

    /**
     * 删除商品
     *
     * @param id 商品ID
     */
    void deleteProduct(int id);

    /**
     * 更新商品状态
     *
     * @param id     商品ID
     * @param status 商品状态（0表示下架，1表示上架）
     */
    void updateProductStatus(int id, String status);
}
