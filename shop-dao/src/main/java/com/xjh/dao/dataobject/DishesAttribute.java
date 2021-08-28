package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

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

    public Integer getDishesAttributeId() {
        return dishesAttributeId;
    }

    public void setDishesAttributeId(Integer dishesAttributeId) {
        this.dishesAttributeId = dishesAttributeId;
    }

    public String getDishesAttributeName() {
        return dishesAttributeName;
    }

    public void setDishesAttributeName(String dishesAttributeName) {
        this.dishesAttributeName = dishesAttributeName;
    }

    public String getDishesAttributeMarkInfo() {
        return dishesAttributeMarkInfo;
    }

    public void setDishesAttributeMarkInfo(String dishesAttributeMarkInfo) {
        this.dishesAttributeMarkInfo = dishesAttributeMarkInfo;
    }

    public Integer getIsValueRadio() {
        return isValueRadio;
    }

    public void setIsValueRadio(Integer isValueRadio) {
        this.isValueRadio = isValueRadio;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }

    public String getDishesAttributeObj() {
        return dishesAttributeObj;
    }

    public void setDishesAttributeObj(String dishesAttributeObj) {
        this.dishesAttributeObj = dishesAttributeObj;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }
}
