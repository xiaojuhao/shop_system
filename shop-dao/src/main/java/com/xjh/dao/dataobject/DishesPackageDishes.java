package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_package_dishes")
public class DishesPackageDishes {
    Integer dishesPackageDishesId;
    Integer dishesPackageId;
    Integer dishesPackageTypeId;
    Integer dishesId;
    String dishesOptions;
    Integer dishesPriceId;
}

