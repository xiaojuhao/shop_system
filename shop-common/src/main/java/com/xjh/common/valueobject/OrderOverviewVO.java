package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class OrderOverviewVO {
    public String deskName;
    public Integer deskId;
    public String orderId;
    public int customerNum;
    public String orderTime;
    public String payStatusName;
    public double orderNeedPay;
    public double totalPrice;
    public String discountName;
    public double discountAmount;
    public double discountableAmount;
    public double orderHadpaid;
    public double orderRefund;
    public double orderReduction;
    public double deduction;
    public double orderErase;
    public double returnDishesPrice;
    public double returnedCash;
    public String payInfoRemark;
}
