package com.xjh.service.vo;

import lombok.Data;

@Data
public class ManyCoupon {
    // 代金券
    String manyCouponId;
    String name;
    int couponType;
    int condition;
    Double amount;
    Double rate;
    String serialNumber;
    int status;
    Long createTime;
    Long startTime;
    Long endTime;
}
