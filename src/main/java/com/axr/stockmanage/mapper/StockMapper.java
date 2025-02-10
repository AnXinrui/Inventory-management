package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.dto.StockDTO;
import com.axr.stockmanage.model.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * @return Stock 库存对象
     */
    Stock findByProductId(long productId);

    /**
     * 加入悲观锁查询
     * @param productId 商品ID
     * @return 库存信息
     */
    Stock findByProductIdForUpdate(@Param("productId") long productId);

    /**
     * 修改库存
     *
     * @param dto 库存修改信息
     * @return int 返回更新的记录数
     */
    int updateStock(StockDTO dto);

    /**
     * 新增库存信息
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
    int deleteByProductId(long productId);

}