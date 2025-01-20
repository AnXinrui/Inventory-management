package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.Stock;
import com.axr.stockmanage.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    /**
     * 增加库存
     * 
     * @param productId 商品ID
     * @param quantity 增加的库存数量
     */
    @Transactional
    @Override
    public void increaseStock(int productId, int quantity) {
        stockMapper.increaseStock(productId, quantity);
    }

    /**
     * 减少库存
     * 
     * @param productId 商品ID
     * @param quantity 减少的库存数量
     */
    @Transactional
    @Override
    public void decreaseStock(int productId, int quantity) {
        stockMapper.decreaseStock(productId, quantity);
    }

    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存对象
     */
    @Transactional(readOnly = true)
    @Override
    public Stock getStockByProductId(int productId) {
        return stockMapper.findByProductId(productId);
    }
}
