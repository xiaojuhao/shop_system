package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.OrderDishesDAO;

@Singleton
public class OrderDishesService {
    @Inject
    OrderDishesDAO orderDishesDAO;

    public List<OrderDishes> selectOrderDishes(String orderId) {
        if (CommonUtils.isBlank(orderId)) {
            return new ArrayList<>();
        }
        OrderDishes c = new OrderDishes();
        c.setOrderId(orderId);
        return orderDishesDAO.select(c);
    }
}
