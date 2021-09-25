package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
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
}
