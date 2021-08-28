package com.xjh.common.enumeration;

public enum EnumDeskType {
    XJH(1, "小句号日式料理"),

    BORROW(2, "借用"),

    PRESERV(3, "预约");
    public int code;
    public String name;

    EnumDeskType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumDeskType of(Integer code) {
        if (code == null) {
            return XJH;
        }
        for (EnumDeskType e : EnumDeskType.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return XJH;
    }

}
