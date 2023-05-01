package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("dishes_package_dishes")
public class DishesPackageDishes {
    @Id
    @Column
    Integer dishesPackageDishesId;
    @Column
    Integer dishesPackageId;
    @Column
    Integer dishesPackageTypeId;
    @Column
    Integer dishesId;
    @Column
    String dishesOptions;
    @Column
    Integer dishesPriceId;
}

