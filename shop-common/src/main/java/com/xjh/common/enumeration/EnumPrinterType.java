package com.xjh.common.enumeration;

public enum EnumPrinterType {
    T00(-1, "未知"),

    T58(0, "58毫米"),

    T80(1, "80毫米"),

    ;
    public int code;
    public String name;

    EnumPrinterType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumPrinterType of(Integer code) {
        if (code == null) {
            return T00;
        }
        for (EnumPrinterType e : EnumPrinterType.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return T00;
    }
}
