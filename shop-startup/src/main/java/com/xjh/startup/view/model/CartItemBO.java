package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.RichText;

public class CartItemBO {
    Integer dishesId;
    RichText dishesName;
    String dishesPriceId;
    String dishesPrice;
    String nums;
    String totalPrice;
    String ifDishesPackage;

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public RichText getDishesName() {
        return dishesName;
    }

    public void setDishesName(RichText dishesName) {
        this.dishesName = dishesName;
    }

    public String getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(String dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }

    public String getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(String dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getIfDishesPackage() {
        return ifDishesPackage;
    }

    public void setIfDishesPackage(String ifDishesPackage) {
        this.ifDishesPackage = ifDishesPackage;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
