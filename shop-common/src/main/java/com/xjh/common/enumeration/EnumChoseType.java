package com.xjh.common.enumeration;

public enum EnumChoseType {
    ALL(1, "全部必选"),
    EXACT(0, "精确选择"),
    MAX(2, "最多可选");
    public int code;
    public String name;

    EnumChoseType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumChoseType of(Integer code) {
        if (code == null) {
            return ALL;
        }
        for (EnumChoseType t : EnumChoseType.values()) {
            if (t.code == code) {
                return t;
            }
        }
        return ALL;
    }
}
