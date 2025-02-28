package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.mapper.StockMapper;
import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Stock;
import com.axr.stockmanage.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class StockServiceImpl implements StockService {

    @Resource
    private StockMapper stockMapper;

    @Override
    public void addStock(Stock stock) {
        stockMapper.add(stock);
    }

    @Override
    public int deleteStock(long id) {
        return stockMapper.deleteByProductId(id);
    }

    @Override
    public Stock findByProductId(long id) {
        return stockMapper.findByProductId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 1000)
    public int updateStock(StockDTO dto) throws BusinessException {
        // 使用 FOR UPDATE 查询库存，并加锁
        Stock stock = stockMapper.findByProductIdForUpdate(dto.getProductId());
        if (stock == null) {
            throw new BusinessException("商品库存不存在");
        }
        if (stock.getQuantity() + dto.getUpdateQuantity() < 0) {
            throw new BusinessException("库存不足");
        }

        int updated = stockMapper.updateStock(dto);
        if (updated != 1) {
            throw new BusinessException("库存更新失败");
        }
        return updated;
    }

    @Override
    public int updateStockWithCAS(StockDTO stockDTO) {
        return stockMapper.updateStockWithCAS(stockDTO);
    }


    @Override
    public Stock getStockByProductId(long productId) {
        return stockMapper.findByProductId(productId);
    }
}
