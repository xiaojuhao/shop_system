package com.xjh.common.enumeration;

public enum EnumPrinterStatus {
    CLOSED(0, "关闭"),

    OPENED(1, "正常"),

    ;
    public int code;
    public String name;

    EnumPrinterStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumPrinterStatus of(Integer code) {
        if (code == null) {
            return CLOSED;
        }
        for (EnumPrinterStatus s : EnumPrinterStatus.values()) {
            if (s.code == code) {
                return s;
            }
        }
        return CLOSED;
    }
}
