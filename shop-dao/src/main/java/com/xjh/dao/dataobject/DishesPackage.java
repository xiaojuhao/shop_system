package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

@Table("dishes_package_list_new")
public class DishesPackage {
    @Column("dishesPackageId")
    Integer dishesPackageId;

    @Column("dishesPackageType")
    Integer dishesPackageType;

    @Column("dishesPackageName")
    String dishesPackageName;

    @Column("dishesPackagePrice")
    Double dishesPackagePrice;

    @Column("dishesPackageStatus")
    Integer dishesPackageStatus;

    @Column("dishesPackageImg")
    String dishesPackageImg;

    @Column("dishesPackageDishes")
    String dishesPackageDishes;

    @Column("creatTime")
    Long creatTime;

    public Integer getDishesPackageId() {
        return dishesPackageId;
    }

    public void setDishesPackageId(Integer dishesPackageId) {
        this.dishesPackageId = dishesPackageId;
    }

    public Integer getDishesPackageType() {
        return dishesPackageType;
    }

    public void setDishesPackageType(Integer dishesPackageType) {
        this.dishesPackageType = dishesPackageType;
    }

    public String getDishesPackageName() {
        return dishesPackageName;
    }

    public void setDishesPackageName(String dishesPackageName) {
        this.dishesPackageName = dishesPackageName;
    }

    public Double getDishesPackagePrice() {
        return dishesPackagePrice;
    }

    public void setDishesPackagePrice(Double dishesPackagePrice) {
        this.dishesPackagePrice = dishesPackagePrice;
    }

    public Integer getDishesPackageStatus() {
        return dishesPackageStatus;
    }

    public void setDishesPackageStatus(Integer dishesPackageStatus) {
        this.dishesPackageStatus = dishesPackageStatus;
    }

    public String getDishesPackageImg() {
        return dishesPackageImg;
    }

    public void setDishesPackageImg(String dishesPackageImg) {
        this.dishesPackageImg = dishesPackageImg;
    }

    public String getDishesPackageDishes() {
        return dishesPackageDishes;
    }

    public void setDishesPackageDishes(String dishesPackageDishes) {
        this.dishesPackageDishes = dishesPackageDishes;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }
}
