package com.xjh.service.domain.model;

public class OrderBillVO {
    public String orderId;
    public int customerNum;
    public String orderTime;
    public String payStatusName;
    public double orderNeedPay;
    public double totalPrice;
    public double discountAmount;
    public double discountableAmount;
    public double orderHadpaid;
    public double orderReduction;
    public double deduction;
    public double orderErase;
    public double returnAmount;
    public String payInfoRemark;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(int customerNum) {
        this.customerNum = customerNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPayStatusName() {
        return payStatusName;
    }

    public void setPayStatusName(String payStatusName) {
        this.payStatusName = payStatusName;
    }

    public double getOrderNeedPay() {
        return orderNeedPay;
    }

    public void setOrderNeedPay(double orderNeedPay) {
        this.orderNeedPay = orderNeedPay;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscountableAmount() {
        return discountableAmount;
    }

    public void setDiscountableAmount(double discountableAmount) {
        this.discountableAmount = discountableAmount;
    }

    public double getOrderHadpaid() {
        return orderHadpaid;
    }

    public void setOrderHadpaid(double orderHadpaid) {
        this.orderHadpaid = orderHadpaid;
    }

    public double getOrderReduction() {
        return orderReduction;
    }

    public void setOrderReduction(double orderReduction) {
        this.orderReduction = orderReduction;
    }

    public double getDeduction() {
        return deduction;
    }

    public void setDeduction(double deduction) {
        this.deduction = deduction;
    }

    public double getOrderErase() {
        return orderErase;
    }

    public void setOrderErase(double orderErase) {
        this.orderErase = orderErase;
    }

    public double getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(double returnAmount) {
        this.returnAmount = returnAmount;
    }

    public String getPayInfoRemark() {
        return payInfoRemark;
    }

    public void setPayInfoRemark(String payInfoRemark) {
        this.payInfoRemark = payInfoRemark;
    }
}
