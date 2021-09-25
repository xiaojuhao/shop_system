package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class OrderDiscountVO {
    private float rate;
    private String discountName = "N";
    // com.xjh.common.enumeration.EnumDiscountType
    private int type;
    private int discountId;
    private String discountCode = "N";
    private int status;
    private int useTimes;
    private long startTime;
    private long endTime;
}
