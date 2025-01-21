package com.axr.stockmanage.service;

import com.axr.stockmanage.model.Product;
import com.axr.stockmanage.model.dto.ProductDTO;

import java.util.List;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
public interface ProductService {

    /**
     * 添加商品
     *
     * @param dto 商品信息
     * @return product id
     */
    Integer addProduct(ProductDTO dto);

    /**
     * 更新商品信息
     *
     * @param product 商品对象
     * @return 修改信息
     */
    Integer updateProduct(Product product);

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return success
     */
    Integer deleteProduct(int id);

    /**
     * 更新商品状态
     *
     * @param id     商品ID
     * @return success
     */
    boolean updateProductStatus(int id);


    /**
     * 查询 product 列表
     *
     * @return product list
     */
    List<Product> listAll();
}
