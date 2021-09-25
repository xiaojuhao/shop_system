package com.xjh.service.domain.model;

import com.xjh.common.enumeration.EnumPayMethod;

import lombok.Data;

@Data
public class PaymentResult {
    Integer orderId;
    double payAmount;
    String payCertNo;
    String payRemark;
    String cardNumber;
    EnumPayMethod payMethod;
    // 0--取消  1--确认
    int payAction;
}
