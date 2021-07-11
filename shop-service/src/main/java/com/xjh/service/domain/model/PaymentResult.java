package com.xjh.service.domain.model;

import com.xjh.common.enumeration.EnumPayMethod;

public class PaymentResult {
    Integer orderId;
    double payAmount;
    String payCertNo;
    String payRemark;
    String cardNumber;
    EnumPayMethod payMethod;
    // 0--取消  1--确认
    int payAction;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayCertNo() {
        return payCertNo;
    }

    public void setPayCertNo(String payCertNo) {
        this.payCertNo = payCertNo;
    }

    public String getPayRemark() {
        return payRemark;
    }

    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }

    public int getPayAction() {
        return payAction;
    }

    public void setPayAction(int payAction) {
        this.payAction = payAction;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public EnumPayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(EnumPayMethod payMethod) {
        this.payMethod = payMethod;
    }
}
