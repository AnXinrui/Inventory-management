package com.axr.stockmanage.mapper;

import com.axr.stockmanage.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xinrui.an
 * @date 2025/02/11
 */
@Mapper
public interface OrderMapper {

    /**
     * 添加订单
     * @param order order
     * @return orderId
     */
    long addOrder(Order order);

    /**
     * 统计用户创建的秒杀订单数
     * @param userId userId
     * @return count
     */
    int countByUserId(long userId);
}
