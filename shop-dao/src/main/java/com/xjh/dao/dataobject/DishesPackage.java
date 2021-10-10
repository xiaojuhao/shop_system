package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_package_list_new")
public class DishesPackage {
    @Id
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

    @Column("sortby")
    Integer sortby;
}
