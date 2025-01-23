package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;

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
     * 根据商品名称查询商品
     *
     * @param name product name
     * @return product
     */
    Product findByName(String name);

    /**
     * 查询所有商品
     *
     * @param product 商品信息
     * @return 商品列表
     */
    List<ProductVO> find(Product product);

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
     * @param dto 商品信息
     * @return 受影响的行数
     */
    int updateProduct(ProductUpdateDTO dto);

    /**
     * 更新商品状态
     *
     * @param id     商品ID
     * @param status 商品状态（0表示下架，1表示上架）
     * @return 受影响的行数
     */
    int updateStatus(int id, int status);

}
