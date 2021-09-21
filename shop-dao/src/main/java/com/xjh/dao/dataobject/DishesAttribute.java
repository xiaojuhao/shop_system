package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishesattribute_list")
public class DishesAttribute {
    @Id
    @Column("dishesAttributeId")
    Integer dishesAttributeId;

    @Column("dishesAttributeName")
    String dishesAttributeName;

    @Column("dishesAttributeMarkInfo")
    String dishesAttributeMarkInfo;

    @Column("isValueRadio")
    Integer isValueRadio;

    @Column("isSync")
    Integer isSync;

    @Column("dishesAttributeObj")
    String dishesAttributeObj;

    @Column("creatTime")
    Long creatTime;
}
