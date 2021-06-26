package com.xjh.common.enumeration;

public enum EnumOrderType {
    NORMAL(1, "单点"),

    BUFFET(2, "自助");
    public int type;
    public String remark;

    EnumOrderType(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }
}
