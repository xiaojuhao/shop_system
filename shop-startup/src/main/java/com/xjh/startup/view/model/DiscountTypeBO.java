package com.xjh.startup.view.model;

public class DiscountTypeBO {
    private String discountName;
    private double discountRate;

    public DiscountTypeBO(String discountName, double discountRate) {
        this.discountName = discountName;
        this.discountRate = discountRate;
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

}
