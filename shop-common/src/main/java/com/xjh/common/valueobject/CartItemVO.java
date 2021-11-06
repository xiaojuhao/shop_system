package com.xjh.common.valueobject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class CartItemVO {
    Integer cartDishesId;
    Integer dishesId;
    Integer dishesPriceId;
    Integer nums;
    Integer ifDishesPackage;
    List<DishesAttributeVO> dishesAttrs = new ArrayList<>();
    List<JSONObject> packagedishes = new ArrayList<>();
}
