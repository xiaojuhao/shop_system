package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.OrderDishesDAO;

@Singleton
public class OrderDishesService {
    @Inject
    OrderDishesDAO orderDishesDAO;

    public List<OrderDishes> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        OrderDishes c = new OrderDishes();
        c.setOrderId(orderId);
        return orderDishesDAO.select(c);
    }

    public List<OrderDishes> selectByIdList(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return orderDishesDAO.selectByIds(ids);
    }

    public int returnOrderDishes(OrderDishes orderDishes) {
        try {
            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(orderDishes.getOrderDishesId());
            update.setOrderDishesSaletype(EnumOrderSaleType.RETURN.type);
            update.setOrderDishesIfrefund(1);
            return orderDishesDAO.updateByPK(update);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
