package com.xjh.dao.dataobject;

import java.io.Serializable;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

@Table("desks")
public class Desk implements Serializable {
    private static final long serialVersionUID = -2784348482896858640L;
    @Column("deskId")
    Integer deskId;
    @Column("deskName")
    String deskName;
    @Column("useStatus")
    Integer status;
    @Column("maxPersonNum")
    Integer maxPerson;
    Integer deskType;
    @Column("orderId")
    Integer orderId;
    @Column("belongDeskType")
    Integer belongDeskType;
    @Column("createTime")
    Long orderCreateTime;

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public String getDeskName() {
        return deskName;
    }

    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(Integer maxPerson) {
        this.maxPerson = maxPerson;
    }

    public Integer getDeskType() {
        return deskType;
    }

    public void setDeskType(Integer deskType) {
        this.deskType = deskType;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Long getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Long orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public Integer getBelongDeskType() {
        return belongDeskType;
    }

    public void setBelongDeskType(Integer belongDeskType) {
        this.belongDeskType = belongDeskType;
    }
}
