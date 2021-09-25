package com.xjh.common.valueobject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class CartItemVO {
    Integer dishesId;
    Integer dishesPriceId;
    Integer nums;
    Integer ifDishesPackage;
    List<JSONObject> contents = new ArrayList<>();
    List<JSONObject> packagedishes = new ArrayList<>();
}
