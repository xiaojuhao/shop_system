package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("dishes_group_list")
public class DishesGroup {
    @Id
    @Column("dishesGroupId")
    Integer dishesGroupId;
    @Column("dishesGroupName")
    String dishesGroupName;
    @Column("dishesGroupContent")
    String dishesGroupContent;
    @Column("createTime")
    Long createTime;
}
