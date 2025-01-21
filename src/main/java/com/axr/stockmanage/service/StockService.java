package com.axr.stockmanage.service;

import com.axr.stockmanage.model.Stock;

public interface StockService {

    /**
     * 修改库存
     * 
     * @param productId 商品ID
     * @param quantity 增加或减少的库存数量
     */
    int updateStock(int productId, int quantity);



    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存对象
     */
    Stock getStockByProductId(int productId);
}
