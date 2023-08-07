package com.xjh.common.model;

import com.xjh.common.enumeration.EnumDiscountType;
import lombok.Data;

@Data
public class DiscountApplyReq {
    private String managerPwd;
    private EnumDiscountType type;
    private String discountName;
    private double discountRate;
    private String discountCode;
}
