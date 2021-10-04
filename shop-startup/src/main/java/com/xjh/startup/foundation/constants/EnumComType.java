package com.xjh.startup.foundation.constants;

public enum EnumComType {
    TEXT(1, "文本"),

    LINE(2, "分隔符"),

    TABLE(3, "表格"),

    QRCODE(4, "二维码"),

    QRCODE2(5, "2个二维码"),

    ;

    public int type;

    public String remark;

    EnumComType(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public static EnumComType of(Integer type) {
        if (type == null) {
            return TEXT;
        }
        for (EnumComType e : EnumComType.values()) {
            if (e.type == type) {
                return e;
            }
        }
        return TEXT;
    }
}
