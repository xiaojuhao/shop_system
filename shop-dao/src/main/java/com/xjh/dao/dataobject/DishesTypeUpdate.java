package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_types_update")
public class DishesTypeUpdate {
    @Id
    @Column("id")
    Integer id;
    @Column("dishesTypeId")
    Integer dishesTypeId;
    @Column("lastUpdateTime")
    Long lastUpdateTime;

}
