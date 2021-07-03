package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("dishes_types")
public class DishesType {
    @Id
    Integer typeId;
    String typeName;
    Integer typeStatus;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(Integer typeStatus) {
        this.typeStatus = typeStatus;
    }
}
