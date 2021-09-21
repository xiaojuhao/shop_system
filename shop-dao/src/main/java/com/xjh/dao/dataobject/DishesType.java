package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_types")
public class DishesType {
    @Id
    @Column("typeId")
    Integer typeId;
    @Column("typeName")
    String typeName;
    @Column("typeStatus")
    Integer typeStatus;
    @Column("ifRefund")
    Integer ifRefund;

    @Column("validTime")
    String validTime;

    @Column("sortby")
    Integer sortby;

    @Column("hidden_h5")
    Integer hiddenH5;

    @Column("hidden_flat")
    Integer hiddenFlat;

    @Column("creatTime")
    Long creatTime;
}
