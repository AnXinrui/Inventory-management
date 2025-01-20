package com.axr.stockmanage.service;

import com.axr.stockmanage.model.Stock;

public interface StockService {

    /**
     * 增加库存
     * 
     * @param productId 商品ID
     * @param quantity 增加的库存数量
     */
    void increaseStock(int productId, int quantity);

    /**
     * 减少库存
     * 
     * @param productId 商品ID
     * @param quantity 减少的库存数量
     */
    void decreaseStock(int productId, int quantity);

    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存对象
     */
    Stock getStockByProductId(int productId);
}
