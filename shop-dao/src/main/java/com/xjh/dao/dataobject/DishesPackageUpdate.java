package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("dishes_package_update")
public class DishesPackageUpdate {
    @Id
    @Column("Id")
    Integer id;

    @Column("dishesPackageId")
    Integer dishesPackageId;

    @Column("lastUpdateTime")
    Long lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDishesPackageId() {
        return dishesPackageId;
    }

    public void setDishesPackageId(Integer dishesPackageId) {
        this.dishesPackageId = dishesPackageId;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
