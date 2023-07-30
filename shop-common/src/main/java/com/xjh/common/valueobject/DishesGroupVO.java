package com.xjh.common.valueobject;

import lombok.Data;

import java.util.List;

@Data
public class DishesGroupVO {
    Integer dishesGroupId;
    String dishesGroupName;
    List<Integer> groupIds;
    Long createTime;
}
