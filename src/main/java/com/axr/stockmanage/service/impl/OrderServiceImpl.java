package com.axr.stockmanage.service.impl;

import com.axr.stockmanage.mapper.OrderMapper;
import com.axr.stockmanage.model.entity.Order;
import com.axr.stockmanage.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xinrui.an
 * @date 2025/02/11
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public void addOrder(Order order) {
        orderMapper.addOrder(order);
    }

    @Override
    public int countByUserId(long userId) {
        return orderMapper.countByUserId(userId);
    }
}
