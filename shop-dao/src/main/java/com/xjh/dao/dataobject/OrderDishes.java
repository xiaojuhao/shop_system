package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Table;

@Table("order_dishes_list")
public class OrderDishes {
    Long orderDishesId;
    String orderId;
    String subOrderId;
    Integer dishesTypeId;
    Integer ifDishesPackage;
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

    public Long getOrderDishesId() {
        return orderDishesId;
    }

    public void setOrderDishesId(Long orderDishesId) {
        this.orderDishesId = orderDishesId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(String subOrderId) {
        this.subOrderId = subOrderId;
    }

    public Integer getDishesTypeId() {
        return dishesTypeId;
    }

    public void setDishesTypeId(Integer dishesTypeId) {
        this.dishesTypeId = dishesTypeId;
    }

    public Integer getIfDishesPackage() {
        return ifDishesPackage;
    }

    public void setIfDishesPackage(Integer ifDishesPackage) {
        this.ifDishesPackage = ifDishesPackage;
    }

    public Double getOrderDishesPrice() {
        return orderDishesPrice;
    }

    public void setOrderDishesPrice(Double orderDishesPrice) {
        this.orderDishesPrice = orderDishesPrice;
    }

    public Double getOrderDishesDiscountPrice() {
        return orderDishesDiscountPrice;
    }

    public void setOrderDishesDiscountPrice(Double orderDishesDiscountPrice) {
        this.orderDishesDiscountPrice = orderDishesDiscountPrice;
    }

    public Integer getOrderDishesNums() {
        return orderDishesNums;
    }

    public void setOrderDishesNums(Integer orderDishesNums) {
        this.orderDishesNums = orderDishesNums;
    }

    public Integer getOrderDishesStatus() {
        return orderDishesStatus;
    }

    public void setOrderDishesStatus(Integer orderDishesStatus) {
        this.orderDishesStatus = orderDishesStatus;
    }

    public Integer getOrderDishesSaletype() {
        return orderDishesSaletype;
    }

    public void setOrderDishesSaletype(Integer orderDishesSaletype) {
        this.orderDishesSaletype = orderDishesSaletype;
    }

    public String getOrderDishesOptions() {
        return orderDishesOptions;
    }

    public void setOrderDishesOptions(String orderDishesOptions) {
        this.orderDishesOptions = orderDishesOptions;
    }

    public Integer getOrderDishesIfrefund() {
        return orderDishesIfrefund;
    }

    public void setOrderDishesIfrefund(Integer orderDishesIfrefund) {
        this.orderDishesIfrefund = orderDishesIfrefund;
    }

    public Integer getOrderDishesIfchange() {
        return orderDishesIfchange;
    }

    public void setOrderDishesIfchange(Integer orderDishesIfchange) {
        this.orderDishesIfchange = orderDishesIfchange;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public String getOrderDishesDiscountInfo() {
        return orderDishesDiscountInfo;
    }

    public void setOrderDishesDiscountInfo(String orderDishesDiscountInfo) {
        this.orderDishesDiscountInfo = orderDishesDiscountInfo;
    }

    public Integer getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(Integer dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }
}
