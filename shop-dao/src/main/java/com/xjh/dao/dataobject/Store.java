package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("store")
public class Store {
    @Id
    @Column("storeId")
    private Integer storeId;
    @Column("name")
    private String name;
    @Column("address")
    private String address;
    @Column("email")
    private String email;
    @Column("phone")
    private String phone;
    @Column("discount")
    private Double discount;
    @Column("status")
    private Integer status;
    @Column("stockMode")
    private Integer stockMode;
    @Column("ownerPassword")
    private String ownerPassword;
    @Column("clerkPassword")
    private String clerkPassword;
    @Column("storeDishesGroupIds")
    private String storeDishesGroupIds;
    @Column("managerDishesGroupIds")
    private String managerDishesGroupIds;
    @Column("memberDishesGroupIds")
    private String memberDishesGroupIds;
    @Column("weChatDishesGroupIds")
    private String weChatDishesGroupIds;
    @Column("ifUsing")
    private Integer ifUsing;
    @Column("activityType")
    private Integer activityType;
    @Column("fullMoney")
    private Double fullMoney;
    @Column("reduceMoney")
    private Double reduceMoney;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStockMode() {
        return stockMode;
    }

    public void setStockMode(Integer stockMode) {
        this.stockMode = stockMode;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public String getClerkPassword() {
        return clerkPassword;
    }

    public void setClerkPassword(String clerkPassword) {
        this.clerkPassword = clerkPassword;
    }

    public String getStoreDishesGroupIds() {
        return storeDishesGroupIds;
    }

    public void setStoreDishesGroupIds(String storeDishesGroupIds) {
        this.storeDishesGroupIds = storeDishesGroupIds;
    }

    public String getManagerDishesGroupIds() {
        return managerDishesGroupIds;
    }

    public void setManagerDishesGroupIds(String managerDishesGroupIds) {
        this.managerDishesGroupIds = managerDishesGroupIds;
    }

    public String getMemberDishesGroupIds() {
        return memberDishesGroupIds;
    }

    public void setMemberDishesGroupIds(String memberDishesGroupIds) {
        this.memberDishesGroupIds = memberDishesGroupIds;
    }

    public String getWeChatDishesGroupIds() {
        return weChatDishesGroupIds;
    }

    public void setWeChatDishesGroupIds(String weChatDishesGroupIds) {
        this.weChatDishesGroupIds = weChatDishesGroupIds;
    }

    public Integer getIfUsing() {
        return ifUsing;
    }

    public void setIfUsing(Integer ifUsing) {
        this.ifUsing = ifUsing;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public Double getFullMoney() {
        return fullMoney;
    }

    public void setFullMoney(Double fullMoney) {
        this.fullMoney = fullMoney;
    }

    public Double getReduceMoney() {
        return reduceMoney;
    }

    public void setReduceMoney(Double reduceMoney) {
        this.reduceMoney = reduceMoney;
    }
}
