package com.xjh.common.valueobject;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DishesAttributeVO {
    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    Boolean isValueRadio = false;
    Boolean isSync = false;
    Long createTime;
    List<DishesAttributeValueVO> selectedAttributeValues = new ArrayList<>();
    List<DishesAttributeValueVO> allAttributeValues = new ArrayList<>();

}
