package com.axr.stockmanage.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.entity.Product;
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
    Long addProduct(ProductAddDTO dto);

    /**
     * 更新商品信息
     *
     * @param dto 商品修改信息
     * @return 修改信息
     */
    @CacheInvalidate(name = "productCache:", key = "'productId:' + #dto.id")
    Integer updateProduct(ProductUpdateDTO dto);

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return success
     */
    @CacheInvalidate(name = "productCache:", key = "'productId:' + #id")
    Integer deleteProduct(long id);

    /**
     * 更新商品状态
     *
     * @param id 商品ID
     * @return success
     */
    @CacheInvalidate(name = "productCache:", key = "'productId:' + #id")
    boolean updateProductStatus(long id);

    /**
     * 根据 id 获取商品
     *
     * @param id id
     * @return product
     */
    @Cached(cacheType = CacheType.BOTH, name = "productCache:", key = "'productId:' + #id")
    Product findById(long id);

    /**
     * 查询 product
     *
     * @param  product 信息
     * @return 满足条件的 product 列表
     */
    List<ProductVO> list(Product product);

    /**
     * 查询所有 product
     *
     * @return productVo list
     */
    List<ProductVO> listAll();

    /**
     * 购买 product
     *
     * @param dto 购买 product 信息
     * @return bool
     */
    @CacheInvalidate(name = "productCache:", key = "'productId:' + #dto.productId")
    boolean purchaseProduct(ProductPurchaseDTO dto);

    /**
     * 商品秒杀
     *
     * @param id productId
     * @return orderId
     */
    @CacheInvalidate(name = "productCache:", key = "'productId:' + #id")
    long secKillProduct(Long id);

    /**
     * 获取指定商品的浏览次数
     *
     * @param productId productId
     * @return viewCount
     */
    long getViewCount(long productId);

    /**
     * 判断是否需要降级
     *
     * @param productId product
     * @return boolean
     */
    boolean checkViewCount(Long productId);
}
