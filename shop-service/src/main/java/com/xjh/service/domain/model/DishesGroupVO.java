package com.xjh.service.domain.model;

import java.util.List;

public class DishesGroupVO {
    Integer dishesGroupId;
    String dishesGroupName;
    List<Integer> groupIds;
    Long createTime;

    public Integer getDishesGroupId() {
        return dishesGroupId;
    }

    public void setDishesGroupId(Integer dishesGroupId) {
        this.dishesGroupId = dishesGroupId;
    }

    public String getDishesGroupName() {
        return dishesGroupName;
    }

    public void setDishesGroupName(String dishesGroupName) {
        this.dishesGroupName = dishesGroupName;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
