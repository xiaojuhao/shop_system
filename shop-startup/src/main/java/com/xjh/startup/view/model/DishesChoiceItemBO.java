package com.xjh.startup.view.model;

public class DishesChoiceItemBO {
    String img;
    Integer dishesId;
    Integer dishesPackageId;
    String dishesName;
    Double dishesPrice;
    Integer ifPackage;

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