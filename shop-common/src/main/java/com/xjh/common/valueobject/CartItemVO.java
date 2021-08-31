package com.xjh.common.valueobject;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartItemVO {
    Integer dishesId;
    Integer dishesPriceId;
    Integer nums;
    Integer ifDishesPackage;
    List<JSONObject> contents = new ArrayList<>();
    List<JSONObject> packagedishes = new ArrayList<>();

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public Integer getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(Integer dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public Integer getIfDishesPackage() {
        return ifDishesPackage;
    }

    public void setIfDishesPackage(Integer ifDishesPackage) {
        this.ifDishesPackage = ifDishesPackage;
    }

    public List<JSONObject> getContents() {
        return contents;
    }

    public void setContents(List<JSONObject> contents) {
        this.contents = contents;
    }

    public List<JSONObject> getPackagedishes() {
        return packagedishes;
    }

    public void setPackagedishes(List<JSONObject> packagedishes) {
        this.packagedishes = packagedishes;
    }
}
