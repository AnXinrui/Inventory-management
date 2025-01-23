package com.axr.stockmanage.service;

import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.vo.ProductVO;

import java.util.List;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
public interface ProductService {

    /**
     * 新增商品
     *
     * @param dto 商品信息
     * @return product id
     */
    Integer addProduct(ProductAddDTO dto);

    /**
     * 更新商品信息
     *
     * @param dto 商品修改信息
     * @return 修改信息
     */
    Integer updateProduct(ProductUpdateDTO dto);

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
     * @param id 商品ID
     * @return success
     */
    boolean updateProductStatus(int id);


    /**
     * 查询 product
     *
     * @param  product 信息
     * @return 满足条件的 product 列表
     */
    List<ProductVO> list(Product product);

    /**
     * 购买 product
     *
     * @param dto 购买 product 信息
     * @return 最新 product 信息
     */
    ProductVO purchaseProduct(ProductPurchaseDTO dto);
}
