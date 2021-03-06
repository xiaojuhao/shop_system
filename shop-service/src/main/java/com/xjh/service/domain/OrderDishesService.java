package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSONArray;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DishesAttributeHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.OrderDishesDAO;

import cn.hutool.core.codec.Base64;

@Singleton
public class OrderDishesService {
    @Inject
    OrderDishesDAO orderDishesDAO;
    @Inject
    StoreService storeService;

    public Result<Integer> updatePrimaryKey(OrderDishes update) {
        try {
            if (update == null) {
                return Result.fail("入参错误");
            }
            int i = orderDishesDAO.updateByPK(update);
            if (i == 0) {
                return Result.fail("更新失败");
            } else {
                return Result.success(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

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

    public Predicate<OrderDishes> discountableChecker() {
        Set<Integer> discountableDishesIds = storeService.getStoreDiscountableDishesIds();
        return o -> discountableDishesIds.contains(o.getDishesId()) && o.getIfDishesPackage() == 0;
    }

    public String generateAttrDigest(OrderDishes orderDishes) {
        if (CommonUtils.isBlank(orderDishes.getOrderDishesOptions())) {
            return "";
        }
        String options = Base64.decodeStr(orderDishes.getOrderDishesOptions());
        List<DishesAttributeVO> attrs = JSONArray.parseArray(options, DishesAttributeVO.class);
        return DishesAttributeHelper.generateSelectedAttrDigest(attrs);
    }
}
