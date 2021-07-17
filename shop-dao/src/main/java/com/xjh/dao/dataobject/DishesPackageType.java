package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

@Table("dishes_package_type")
public class DishesPackageType {
    @Column("dishesPackageTypeId")
    Integer dishesPackageTypeId;

    @Column("dishesPackageId")
    Integer dishesPackageId;

    @Column("dishesPackageTypeName")
    String dishesPackageTypeName;

    @Column("ifRequired")
    Integer ifRequired;

    @Column("chooseNums")
    Integer chooseNums;

    public Integer getDishesPackageTypeId() {
        return dishesPackageTypeId;
    }

    public void setDishesPackageTypeId(Integer dishesPackageTypeId) {
        this.dishesPackageTypeId = dishesPackageTypeId;
    }

    public Integer getDishesPackageId() {
        return dishesPackageId;
    }

    public void setDishesPackageId(Integer dishesPackageId) {
        this.dishesPackageId = dishesPackageId;
    }

    public String getDishesPackageTypeName() {
        return dishesPackageTypeName;
    }

    public void setDishesPackageTypeName(String dishesPackageTypeName) {
        this.dishesPackageTypeName = dishesPackageTypeName;
    }

    public Integer getIfRequired() {
        return ifRequired;
    }

    public void setIfRequired(Integer ifRequired) {
        this.ifRequired = ifRequired;
    }

    public Integer getChooseNums() {
        return chooseNums;
    }

    public void setChooseNums(Integer chooseNums) {
        this.chooseNums = chooseNums;
    }
}
