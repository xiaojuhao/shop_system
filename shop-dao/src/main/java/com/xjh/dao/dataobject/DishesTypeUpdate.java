package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("dishes_types_update")
public class DishesTypeUpdate {
    @Id
    @Column("id")
    Integer id;
    @Column("dishesTypeId")
    Integer dishesTypeId;
    @Column("lastUpdateTime")
    Long lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDishesTypeId() {
        return dishesTypeId;
    }

    public void setDishesTypeId(Integer dishesTypeId) {
        this.dishesTypeId = dishesTypeId;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
