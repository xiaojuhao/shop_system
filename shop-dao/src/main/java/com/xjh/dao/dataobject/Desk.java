package com.xjh.dao.dataobject;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.xjh.dao.foundation.Column;

public class Desk implements Serializable {
    private static final long serialVersionUID = -2784348482896858640L;
    @Column("deskId")
    Long id;
    @Column("deskName")
    String deskName;
    @Column("useStatus")
    Integer status;
    @Column("maxPersonNum")
    Integer maxPerson;
    Integer deskType;
    @Column("orderId")
    String orderId;
    @Column("createTime")
    LocalDateTime orderCreateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(LocalDateTime orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

}
