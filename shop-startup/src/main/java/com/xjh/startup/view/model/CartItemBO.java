package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;

public class CartItemBO {
    Integer dishesId;
    RichText dishesName;
    String dishesPriceId;
    Money dishesPrice;
    Integer nums;
    Money totalPrice;
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

    public Money getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(Money dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public String getIfDishesPackage() {
        return ifDishesPackage;
    }

    public void setIfDishesPackage(String ifDishesPackage) {
        this.ifDishesPackage = ifDishesPackage;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }
}
