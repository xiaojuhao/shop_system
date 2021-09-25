package com.xjh.common.valueobject;

import java.util.List;

import lombok.Data;

@Data
public class DishesGroupVO {
    Integer dishesGroupId;
    String dishesGroupName;
    List<Integer> groupIds;
    Long createTime;
}
