package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_package_type")
public class DishesPackageType {
    @Id
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
}
