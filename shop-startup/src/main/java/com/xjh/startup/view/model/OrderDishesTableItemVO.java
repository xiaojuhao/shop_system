package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.RichText;

public class OrderDishesTableItemVO {
    String orderDishesId;
    String subOrderId;
    RichText dishesName;
    RichText price;
    RichText discountPrice;
    String orderDishesNum;
    RichText saleType;

    public OrderDishesTableItemVO(String orderDishesId, String subOrderId, RichText dishesName, RichText price, RichText discountPrice, String orderDishesNum, RichText saleType) {
        this.orderDishesId = orderDishesId;
        this.subOrderId = subOrderId;
        this.dishesName = dishesName;
        this.price = price;
        this.discountPrice = discountPrice;
        this.orderDishesNum = orderDishesNum;
        this.saleType = saleType;
    }

    public String getOrderDishesId() {
        return orderDishesId;
    }

    public void setOrderDishesId(String orderDishesId) {
        this.orderDishesId = orderDishesId;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(String subOrderId) {
        this.subOrderId = subOrderId;
    }

    public RichText getDishesName() {
        return dishesName;
    }

    public void setDishesName(RichText dishesName) {
        this.dishesName = dishesName;
    }

    public RichText getPrice() {
        return price;
    }

    public void setPrice(RichText price) {
        this.price = price;
    }

    public RichText getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(RichText discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getOrderDishesNum() {
        return orderDishesNum;
    }

    public void setOrderDishesNum(String orderDishesNum) {
        this.orderDishesNum = orderDishesNum;
    }

    public RichText getSaleType() {
        return saleType;
    }

    public void setSaleType(RichText saleType) {
        this.saleType = saleType;
    }
}
