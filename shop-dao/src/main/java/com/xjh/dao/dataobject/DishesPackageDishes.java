package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Table;

@Table("dishes_package_dishes")
public class DishesPackageDishes {
    Integer dishesPackageDishesId;
    Integer dishesPackageId;
    Integer dishesPackageTypeId;
    Integer dishesId;
    String dishesOptions;
    Integer dishesPriceId;

    public Integer getDishesPackageDishesId() {
        return dishesPackageDishesId;
    }

    public void setDishesPackageDishesId(Integer dishesPackageDishesId) {
        this.dishesPackageDishesId = dishesPackageDishesId;
    }

    public Integer getDishesPackageId() {
        return dishesPackageId;
    }

    public void setDishesPackageId(Integer dishesPackageId) {
        this.dishesPackageId = dishesPackageId;
    }

    public Integer getDishesPackageTypeId() {
        return dishesPackageTypeId;
    }

    public void setDishesPackageTypeId(Integer dishesPackageTypeId) {
        this.dishesPackageTypeId = dishesPackageTypeId;
    }

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public String getDishesOptions() {
        return dishesOptions;
    }

    public void setDishesOptions(String dishesOptions) {
        this.dishesOptions = dishesOptions;
    }

    public Integer getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(Integer dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }
}

