package com.xjh.service.domain.model;

import java.util.List;

public class StoreVO {
    private int storeId;
    private String name;
    private String address;
    private String email;
    private String phone;
    private double discount;
    private int status;
    private int stockMode;
    private String ownerPassword;
    private String clerkPassword;
    private List<DishesGroupVO> storeDishesGroups;
    private List<DishesGroupVO> managerDishesGroups;
    private List<DishesGroupVO> memberDishesGroups;
    private List<DishesGroupVO> weChatDishesGroups;
    private int ifUsing;
    private int activityType;
    private double fullMoney;
    private double reduceMoney;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStockMode() {
        return stockMode;
    }

    public void setStockMode(int stockMode) {
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

    public List<DishesGroupVO> getStoreDishesGroups() {
        return storeDishesGroups;
    }

    public void setStoreDishesGroups(List<DishesGroupVO> storeDishesGroups) {
        this.storeDishesGroups = storeDishesGroups;
    }

    public List<DishesGroupVO> getManagerDishesGroups() {
        return managerDishesGroups;
    }

    public void setManagerDishesGroups(List<DishesGroupVO> managerDishesGroups) {
        this.managerDishesGroups = managerDishesGroups;
    }

    public List<DishesGroupVO> getMemberDishesGroups() {
        return memberDishesGroups;
    }

    public void setMemberDishesGroups(List<DishesGroupVO> memberDishesGroups) {
        this.memberDishesGroups = memberDishesGroups;
    }

    public List<DishesGroupVO> getWeChatDishesGroups() {
        return weChatDishesGroups;
    }

    public void setWeChatDishesGroups(List<DishesGroupVO> weChatDishesGroups) {
        this.weChatDishesGroups = weChatDishesGroups;
    }

    public int getIfUsing() {
        return ifUsing;
    }

    public void setIfUsing(int ifUsing) {
        this.ifUsing = ifUsing;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public double getFullMoney() {
        return fullMoney;
    }

    public void setFullMoney(double fullMoney) {
        this.fullMoney = fullMoney;
    }

    public double getReduceMoney() {
        return reduceMoney;
    }

    public void setReduceMoney(double reduceMoney) {
        this.reduceMoney = reduceMoney;
    }
}
