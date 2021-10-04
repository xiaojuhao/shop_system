package com.xjh.startup.foundation.utils;

public enum AlignEnum {
    CENTER(0),
    LEFT(2),
    RIGHT(4),
    ;
    int level;

    AlignEnum(int level) {

    }

    public static AlignEnum of(Integer level) {
        if (level == null) {
            return LEFT;
        }
        for (AlignEnum e : AlignEnum.values()) {
            if (e.level == level) {
                return e;
            }
        }
        return LEFT;
    }
}
