package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class OrderDiscountVO {
    private float rate;
    private String discountName = "无";
    // com.xjh.common.enumeration.EnumDiscountType
    private int type;
    private int discountId;
    private String discountCode = "无";
    private int status;
    private int useTimes;
    private long startTime;
    private long endTime;
}
