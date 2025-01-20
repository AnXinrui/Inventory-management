package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Mapper
public interface ProductMapper {

    /**
     * 添加商品
     *
     * @param product 商品对象
     * @return 商品ID
     */
    int addProduct(Product product);

    /**
     * 根据商品ID查询商品
     *
     * @param id 商品ID
     * @return 商品对象
     */
    Product findById(int id);

    /**
     * 查询所有商品
     *
     * @return 商品列表
     */
    List<Product> findAll();

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 受影响的行数
     */
    int deleteProduct(int id);

    /**
     * 更新商品信息
     *
     * @param product 商品对象
     * @return 受影响的行数
     */
    int updateProduct(Product product);

    /**
     * 更新商品状态
     *
     * @param id     商品ID
     * @param status 商品状态（0表示下架，1表示上架）
     * @return 受影响的行数
     */
    int updateStatus(int id, String status);

}
