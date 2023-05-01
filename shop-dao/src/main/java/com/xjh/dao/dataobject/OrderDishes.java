package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("order_dishes_list")
public class OrderDishes {
    @Id
    @Column
    Long orderDishesId;
    @Column
    Integer orderId;
    @Column
    Integer subOrderId;
    @Column
    Integer dishesTypeId;
    @Column
    Integer ifDishesPackage;
    @Column
    Integer dishesId;
    @Column
    Double orderDishesPrice;
    @Column
    Double orderDishesDiscountPrice;
    @Column
    Integer orderDishesNums;
    @Column
    Integer orderDishesStatus;
    @Column
    Integer orderDishesSaletype;
    @Column
    String orderDishesOptions;
    @Column
    Integer orderDishesIfrefund;
    @Column
    Integer orderDishesIfchange;
    @Column
    Long createtime;
    @Column
    String orderDishesDiscountInfo;
    @Column
    Integer dishesPriceId;
}
