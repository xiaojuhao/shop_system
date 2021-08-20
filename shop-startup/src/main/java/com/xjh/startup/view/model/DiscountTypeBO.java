package com.xjh.startup.view.model;

public class DiscountTypeBO {
    private String discountType;
    private String discountName;
    private double discountRate;
    private String serialNo;

    public DiscountTypeBO(String discountType, String discountName, double discountRate) {
        this.discountType = discountType;
        this.discountName = discountName;
        this.discountRate = discountRate;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
