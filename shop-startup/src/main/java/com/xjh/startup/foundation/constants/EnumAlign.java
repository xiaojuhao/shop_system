package com.xjh.startup.foundation.constants;

public enum EnumAlign {
    CENTER(0),
    LEFT(2),
    RIGHT(4),
    ;
    public int level;

    EnumAlign(int level) {

    }

    public static EnumAlign of(Integer level) {
        if (level == null) {
            return LEFT;
        }
        for (EnumAlign e : EnumAlign.values()) {
            if (e.level == level) {
                return e;
            }
        }
        return LEFT;
    }
}
