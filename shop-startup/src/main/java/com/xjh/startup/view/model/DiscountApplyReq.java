package com.xjh.startup.view.model;

import com.xjh.common.enumeration.EnumDiscountType;

public class DiscountApplyReq {
    private String managerPwd;
    private EnumDiscountType type;
    private String discountName;
    private double discountRate;
    private String discountCode;

    public String getManagerPwd() {
        return managerPwd;
    }

    public void setManagerPwd(String managerPwd) {
        this.managerPwd = managerPwd;
    }

    public EnumDiscountType getType() {
        return type;
    }

    public void setType(EnumDiscountType type) {
        this.type = type;
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

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
}
