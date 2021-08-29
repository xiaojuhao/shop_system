package com.xjh.common.enumeration;

public enum EnumDeskPhysicalStatus {
    // 1--空闲 2--使用中 3--已预约 4--已付款
    DISABLE(0, "禁用"),

    NORMAL(1, "正常"),

    ;
    public int code;
    public String name;

    EnumDeskPhysicalStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumDeskPhysicalStatus of(Integer code) {
        if (code == null) {
            return NORMAL;
        }
        for (EnumDeskPhysicalStatus e : EnumDeskPhysicalStatus.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return NORMAL;
    }
}
