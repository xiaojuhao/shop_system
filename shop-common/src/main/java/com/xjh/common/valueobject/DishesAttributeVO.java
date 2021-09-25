package com.xjh.common.valueobject;

import java.util.List;

import lombok.Data;

@Data
public class DishesAttributeVO {
    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    Boolean isValueRadio;
    Boolean isSync;
    Long createTime;
    List<DishesAttributeValueVO> selectedAttributeValues;
    List<DishesAttributeValueVO> allAttributeValues;

}
