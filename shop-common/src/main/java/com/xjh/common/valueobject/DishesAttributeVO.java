package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class DishesAttributeVO {
    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    Boolean isValueRadio;
    Boolean isSync;
    Long createTime;
}
