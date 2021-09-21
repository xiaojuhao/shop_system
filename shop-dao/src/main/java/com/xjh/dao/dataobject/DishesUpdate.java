package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_update")
public class DishesUpdate {
    @Id
    @Column("id")
    Integer id;
    @Column("dishesId")
    Integer dishesId;
    @Column("lastUpdateTime")
    Long lastUpdateTime;
}
