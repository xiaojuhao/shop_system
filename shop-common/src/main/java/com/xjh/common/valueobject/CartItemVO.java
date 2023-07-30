package com.xjh.common.valueobject;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartItemVO {
    int cartDishesId;
    int dishesId;
    int dishesPriceId;
    int nums;
    int ifDishesPackage;
    List<DishesAttributeVO> dishesAttrs = new ArrayList<>();
    List<JSONObject> packagedishes = new ArrayList<>();
    List<JSONObject> contents = new ArrayList<>();



}
