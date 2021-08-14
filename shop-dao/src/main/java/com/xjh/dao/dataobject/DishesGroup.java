package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("dishes_group_list")
public class DishesGroup {
    @Id
    @Column("dishesGroupId")
    Integer dishesGroupId;
    @Column("dishesGroupName")
    String dishesGroupName;
    @Column("dishesGroupContent")
    String dishesGroupContent;
    @Column("createTime")
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

    public String getDishesGroupContent() {
        return dishesGroupContent;
    }

    public void setDishesGroupContent(String dishesGroupContent) {
        this.dishesGroupContent = dishesGroupContent;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
