package com.axr.stockmanage.service;

import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Stock;

public interface StockService {

    /**
     * 修改库存
     * 
     * @param dto 库存信息
     * @return success
     */
    int updateStock(StockDTO dto);



    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存对象
     */
    Stock getStockByProductId(int productId);
}
