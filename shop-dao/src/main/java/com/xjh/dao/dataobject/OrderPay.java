package com.xjh.dao.dataobject;

import com.xjh.common.enumeration.EnumPayStatus;
import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

import java.util.function.Predicate;

@Data
@Table("order_pays")
public class OrderPay {
    @Id
    @Column
    Integer orderpaysId;
    @Column
    Integer orderId;
    @Column
    Integer accountId;
    @Column
    Double amount;
    @Column
    Double actualAmount;
    @Column
    Integer voucherNums;
    @Column
    Integer paymentMethod;
    @Column
    String cardNumber;
    @Column
    Integer paymentStatus;
    @Column
    String remark;
    @Column
    Long createtime;

    public static Predicate<OrderPay> isPaid(){
        return orderPay -> EnumPayStatus.of(orderPay.paymentStatus) == EnumPayStatus.PAID;
    }
}
