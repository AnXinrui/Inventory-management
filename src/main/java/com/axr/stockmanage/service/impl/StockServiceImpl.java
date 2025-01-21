package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.Stock;
import com.axr.stockmanage.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class StockServiceImpl implements StockService {

    @Resource
    private StockMapper stockMapper;

    /**
     * 增加库存
     *
     * @param productId 商品ID
     * @param quantity  增加的库存数量
     */
    @Transactional
    @Override
    public int updateStock(int productId, int quantity) {
        Stock stock = stockMapper.findByProductId(productId);
        if (stock == null) {
            throw new BusinessException("商品库存不存在");
        }
        if (stock.getQuantity() + quantity < 0) {
            throw new BusinessException("库存不足");
        }
        return stockMapper.updateStock(productId, quantity);
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
