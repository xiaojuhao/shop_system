package com.xjh.service.domain.model;

import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import lombok.Data;

@Data
public class PaymentResult {
    Integer orderId;
    double payAmount;
    double actualAmount;
    Integer voucherNum = 0;
    String payCertNo;
    String payRemark;
    String cardNumber;
    EnumPayMethod payMethod;
    // 0--取消  1--确认
    EnumPayAction payAction;

    String errorMsg;
}
