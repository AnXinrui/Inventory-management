package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.Stock;
import org.apache.ibatis.annotations.Mapper;

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
     * 增加库存
     *
     * @param productId 商品ID
     * @param quantity 增加的库存数量
     * @return int 返回更新的记录数
     */
    int increaseStock(int productId, int quantity);

    /**
     * 减少库存
     *
     * @param productId 商品ID
     * @param quantity 减少的库存数量
     * @return int 返回更新的记录数
     */
    int decreaseStock(int productId, int quantity);
}