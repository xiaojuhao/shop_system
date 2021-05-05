package com.xjh.dao.mapper;

import java.util.List;

import com.xjh.dao.dataobject.OrderDishes;

public interface OrderDishesDAO {
    List<OrderDishes> select(OrderDishes example);
}
