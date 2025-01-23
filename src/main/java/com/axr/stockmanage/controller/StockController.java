package com.axr.stockmanage.controller;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.common.Result;
import com.axr.stockmanage.model.entity.Stock;
import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.service.StockService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xinrui.an
 * @date 2025/01/21
 */
@RestController
@RequestMapping("stock")
public class StockController {

    @Resource
    private StockService stockService;

    @PostMapping("update")
    public Result<Integer> updateStock(@RequestBody StockDTO dto) {
        if (dto.getProductId() == null || dto.getProductId() < 1) {
            throw new BusinessException("信息错误");
        }
        return Result.success(stockService.updateStock(dto));
    }

    @GetMapping("search")
    public Result<Stock> searchStock(Integer productId) {
        return Result.success(stockService.getStockByProductId(productId));
    }
}
