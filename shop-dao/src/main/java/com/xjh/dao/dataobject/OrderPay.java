package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("order_pays")
public class OrderPay {
    @Id
    Integer orderpaysId;
    Integer orderId;
    Integer accountId;
    Double amount;
    Double actualAmount;
    Integer voucherNums;
    Integer paymentMethod;
    String cardNumber;
    Integer paymentStatus;
    String remark;
    Long createtime;

    public Integer getOrderpaysId() {
        return orderpaysId;
    }

    public void setOrderpaysId(Integer orderpaysId) {
        this.orderpaysId = orderpaysId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Integer getVoucherNums() {
        return voucherNums;
    }

    public void setVoucherNums(Integer voucherNums) {
        this.voucherNums = voucherNums;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }
}
