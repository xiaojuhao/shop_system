package com.xjh.startup.view.model;

public class DishesQueryCond {
    int pageNo = 1;
    int pageSize = 40;
    String dishesName;
    Integer dishesTypeId;
    Integer ifPackage;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public Integer getDishesTypeId() {
        return dishesTypeId;
    }

    public void setDishesTypeId(Integer dishesTypeId) {
        this.dishesTypeId = dishesTypeId;
    }

    public Integer getIfPackage() {
        return ifPackage;
    }

    public void setIfPackage(Integer ifPackage) {
        this.ifPackage = ifPackage;
    }
}
