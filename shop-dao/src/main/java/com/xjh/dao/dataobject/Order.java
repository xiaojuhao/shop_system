package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

@Table("order_list")
public class Order {
    @Column("orderId")
    Integer orderId;
    @Column("orderStatus")
    Integer orderStatus;
    @Column("deskId")
    Integer deskId;
    @Column("accountId")
    Long accountId;
    @Column("memberId")
    Long memberId;
    @Column("orderCustomerNums")
    Integer orderCustomerNums;
    @Column("orderCustomerTelphone")
    String orderCustomerTelphone;
    @Column("orderRecommender")
    String orderRecommender;
    @Column("orderHadpaid")
    Double orderHadpaid;
    @Column("orderReduction")
    Double orderReduction;
    @Column("orderErase")
    Double orderErase;
    @Column("orderRefund")
    Double orderRefund;
    @Column("createtime")
    Long createTime;
    @Column("status")
    Integer status;
    @Column("orderDiscountInfo")
    String orderDiscountInfo;
    @Column("fullReduceDishesPrice")
    Double fullReduceDishesPrice;
    @Column("discount_reason")
    String discountReason;
    @Column("return_cash_reason")
    String returnCashReason;
    @Column("orderType")
    Integer orderType;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getOrderCustomerNums() {
        return orderCustomerNums;
    }

    public void setOrderCustomerNums(Integer orderCustomerNums) {
        this.orderCustomerNums = orderCustomerNums;
    }

    public String getOrderCustomerTelphone() {
        return orderCustomerTelphone;
    }

    public void setOrderCustomerTelphone(String orderCustomerTelphone) {
        this.orderCustomerTelphone = orderCustomerTelphone;
    }

    public String getOrderRecommender() {
        return orderRecommender;
    }

    public void setOrderRecommender(String orderRecommender) {
        this.orderRecommender = orderRecommender;
    }

    public Double getOrderHadpaid() {
        return orderHadpaid;
    }

    public void setOrderHadpaid(Double orderHadpaid) {
        this.orderHadpaid = orderHadpaid;
    }

    public Double getOrderReduction() {
        return orderReduction;
    }

    public void setOrderReduction(Double orderReduction) {
        this.orderReduction = orderReduction;
    }

    public Double getOrderErase() {
        return orderErase;
    }

    public void setOrderErase(Double orderErase) {
        this.orderErase = orderErase;
    }

    public Double getOrderRefund() {
        return orderRefund;
    }

    public void setOrderRefund(Double orderRefund) {
        this.orderRefund = orderRefund;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderDiscountInfo() {
        return orderDiscountInfo;
    }

    public void setOrderDiscountInfo(String orderDiscountInfo) {
        this.orderDiscountInfo = orderDiscountInfo;
    }

    public Double getFullReduceDishesPrice() {
        return fullReduceDishesPrice;
    }

    public void setFullReduceDishesPrice(Double fullReduceDishesPrice) {
        this.fullReduceDishesPrice = fullReduceDishesPrice;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public String getReturnCashReason() {
        return returnCashReason;
    }

    public void setReturnCashReason(String returnCashReason) {
        this.returnCashReason = returnCashReason;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
