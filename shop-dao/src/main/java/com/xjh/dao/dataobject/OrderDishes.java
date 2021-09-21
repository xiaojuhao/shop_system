package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("order_dishes_list")
public class OrderDishes {
    @Id
    Long orderDishesId;
    Integer orderId;
    Integer subOrderId;
    Integer dishesTypeId;
    Integer ifDishesPackage;
    Integer dishesId;
    Double orderDishesPrice;
    Double orderDishesDiscountPrice;
    Integer orderDishesNums;
    Integer orderDishesStatus;
    Integer orderDishesSaletype;
    String orderDishesOptions;
    Integer orderDishesIfrefund;
    Integer orderDishesIfchange;
    Long createtime;
    String orderDishesDiscountInfo;
    Integer dishesPriceId;
}
