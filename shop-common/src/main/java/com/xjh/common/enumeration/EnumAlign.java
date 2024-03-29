package com.xjh.common.enumeration;

public enum EnumAlign {
    CENTER(0),
    LEFT(2),
    RIGHT(4),
    ;
    public int type;

    EnumAlign(int type) {
        this.type = type;
    }

    public static EnumAlign of(Integer level) {
        if (level == null) {
            return LEFT;
        }
        for (EnumAlign e : EnumAlign.values()) {
            if (e.type == level) {
                return e;
            }
        }
        return LEFT;
    }
}
