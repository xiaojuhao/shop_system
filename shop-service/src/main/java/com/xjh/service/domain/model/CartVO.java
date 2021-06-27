package com.xjh.service.domain.model;

import java.util.List;

public class CartVO {
    private int dishesId;
    private int nums;
    private List<DishesAttributeVO> selectDishesAttributes;
    private int ifDishesPackage;
    private int dishesPriceId;

    public int getDishesId() {
        return dishesId;
    }

    public void setDishesId(int dishesId) {
        this.dishesId = dishesId;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public List<DishesAttributeVO> getSelectDishesAttributes() {
        return selectDishesAttributes;
    }

    public void setSelectDishesAttributes(List<DishesAttributeVO> selectDishesAttributes) {
        this.selectDishesAttributes = selectDishesAttributes;
    }

    public int getIfDishesPackage() {
        return ifDishesPackage;
    }

    public void setIfDishesPackage(int ifDishesPackage) {
        this.ifDishesPackage = ifDishesPackage;
    }

    public int getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(int dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }
}
