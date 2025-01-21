package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.Stock;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Mapper
public interface StockMapper {

    /**
     * 根据商品ID获取库存信息
     *
     * @param productId 商品ID
     * @return Stock 库存对象，包含库存数量和更新时间
     */
    Stock findByProductId(int productId);

    /**
     * 修改库存
     *
     * @param productId 商品ID
     * @param quantity 修改的库存数量
     * @return int 返回更新的记录数
     */
    int updateStock(int productId, int quantity);

    /**
     * 添加库存信息
     *
     * @param stock stock
     */
    void add(Stock stock);

    /**
     * 根据 product id 删除库存信息
     *
     * @param productId product id
     * @return r
     */
    int deleteByProductId(int productId);
}