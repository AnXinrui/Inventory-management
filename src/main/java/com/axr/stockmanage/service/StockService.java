package com.axr.stockmanage.service;

import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Stock;

public interface StockService {

    /**
     * 新增库存信息
     *
     * @param stock stock
     */
    void addStock(Stock stock);

    /**
     * 删除库存信息
     *
     * @param id productId
     * @return res
     */
    int deleteStock(long id);

    /**
     * 通过 id 获取 stock
     *
     * @param id productId
     * @return stock
     */
    Stock findByProductId(long id);

    /**
     * 修改库存
     * 
     * @param dto 库存信息
     * @return success
     */
    int updateStock(StockDTO dto);

    /**
     * 修改库存 (CAS)
     *
     * @param stockDTO stockdto
     * @return res
     */
    int updateStockWithCAS(StockDTO stockDTO);

    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存对象
     */
    Stock getStockByProductId(long productId);
}
