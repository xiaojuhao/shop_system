package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("order_list")
public class Order {
    @Id
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
}
