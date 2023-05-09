package com.xjh.common.enumeration;

public enum EnumIsPackage {

    NO(0, "不是套餐"),

    YES(1, "是套餐"),

    YES_NEW(2, "是新套餐"),
    ;

    public final int code;

    EnumIsPackage(int code, String remark) {
        this.code = code;
    }
}
