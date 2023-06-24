package com.xjh.common.valueobject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class CartItemVO {
    int cartDishesId;
    int dishesId;
    int dishesPriceId;
    int nums;
    int ifDishesPackage;
    List<DishesAttributeVO> dishesAttrs = new ArrayList<>();
    List<JSONObject> packagedishes = new ArrayList<>();
    String contents = "[]";



}
