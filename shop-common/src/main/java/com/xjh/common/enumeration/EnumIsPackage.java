package com.xjh.common.enumeration;

public enum EnumIsPackage {
    YES(1, "是套餐"),

    NO(0, "不是套餐");

    public final int code;

    EnumIsPackage(int code, String remark) {
        this.code = code;
    }
}
