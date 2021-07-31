package com.xjh.service.domain.model;

public class SendOrderRequest {
    Integer orderId;
    Integer deskId;
    String deskName;

    String img;
    Integer dishesId;
    Integer dishesPackageId;
    String dishesName;
    Double dishesPrice;
    Integer ifPackage;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public Integer getDishesPackageId() {
        return dishesPackageId;
    }

    public void setDishesPackageId(Integer dishesPackageId) {
        this.dishesPackageId = dishesPackageId;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public Double getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(Double dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public Integer getIfPackage() {
        return ifPackage;
    }

    public void setIfPackage(Integer ifPackage) {
        this.ifPackage = ifPackage;
    }
}
