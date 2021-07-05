package com.xjh.common.enumeration;

public enum EnumPayStatus {
    UNPAID(0, "未支付"),
    PAID(1, "已支付");
    public int code;
    public String name;

    EnumPayStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
