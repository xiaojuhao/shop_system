package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.RichText;
import lombok.Data;

@Data
public class OrderDishesTableItemBO {
    String orderDishesId;
    String subOrderId;
    RichText dishesName;
    RichText price;
    RichText discountPrice;
    String orderDishesNum;
    RichText saleType;

    public OrderDishesTableItemBO(String orderDishesId, String subOrderId, RichText dishesName, RichText price, RichText discountPrice, String orderDishesNum, RichText saleType) {
        this.orderDishesId = orderDishesId;
        this.subOrderId = subOrderId;
        this.dishesName = dishesName;
        this.price = price;
        this.discountPrice = discountPrice;
        this.orderDishesNum = orderDishesNum;
        this.saleType = saleType;
    }
}
