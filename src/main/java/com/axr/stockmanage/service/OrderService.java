package com.axr.stockmanage.service;

import com.axr.stockmanage.model.entity.Order;

/**
 * @author xinrui.an
 * @date 2025/02/11
 */
public interface OrderService {

    /**
     * 新建订单
     * @param order order
     */
    void addOrder(Order order);

    /**
     * 统计用户创建的秒杀订单数
     * @param userId userId
     * @return count
     */
    int countByUserId(long userId);
}
