package com.xjh.common.model;

import lombok.Data;

@Data
public class DiscountTypeBO {
    private String discountName;
    private double discountRate;

    public DiscountTypeBO(String discountName, double discountRate) {
        this.discountName = discountName;
        this.discountRate = discountRate;
    }
}
