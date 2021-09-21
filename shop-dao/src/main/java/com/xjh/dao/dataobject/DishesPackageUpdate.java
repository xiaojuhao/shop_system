package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_package_update")
public class DishesPackageUpdate {
    @Id
    @Column("Id")
    Integer id;

    @Column("dishesPackageId")
    Integer dishesPackageId;

    @Column("lastUpdateTime")
    Long lastUpdateTime;
}
