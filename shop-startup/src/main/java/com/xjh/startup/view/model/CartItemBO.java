package com.xjh.startup.view.model;

public class CartItemBO {
    String dishesId;
    String dishesName;
    String dishesPriceId;
    String dishesPrice;
    String nums;
    String ifDishesPackage;

    public String getDishesId() {
        return dishesId;
    }

    public void setDishesId(String dishesId) {
        this.dishesId = dishesId;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
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
}
