package com.xjh.common.enumeration;

public enum EnumPrinterType {
    T00(-1, "未知", 32),

    T58(0, "58毫米", 32),

    T80(1, "80毫米", 48),

    ;
    public int code;
    public String name;
    public int numOfChars; // 打印纸每行字符数（按英文字符算，每个汉字算2个）

    EnumPrinterType(int code, String name, int numOfChars) {
        this.code = code;
        this.name = name;
        this.numOfChars = numOfChars;
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
