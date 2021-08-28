package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

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

    public Integer getIfRefund() {
        return ifRefund;
    }

    public void setIfRefund(Integer ifRefund) {
        this.ifRefund = ifRefund;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public Integer getSortby() {
        return sortby;
    }

    public void setSortby(Integer sortby) {
        this.sortby = sortby;
    }

    public Integer getHiddenH5() {
        return hiddenH5;
    }

    public void setHiddenH5(Integer hiddenH5) {
        this.hiddenH5 = hiddenH5;
    }

    public Integer getHiddenFlat() {
        return hiddenFlat;
    }

    public void setHiddenFlat(Integer hiddenFlat) {
        this.hiddenFlat = hiddenFlat;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }
}
