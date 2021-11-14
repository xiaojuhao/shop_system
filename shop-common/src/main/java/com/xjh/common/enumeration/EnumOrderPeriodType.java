package com.xjh.common.enumeration;

import java.time.LocalDateTime;

import com.xjh.common.utils.DateBuilder;

public enum EnumOrderPeriodType {
    NOON(1, "午市"),

    NIGHT(2, "晚市"),

    SUPER(3, "夜宵");

    int type;
    String remark;

    EnumOrderPeriodType(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public static EnumOrderPeriodType check(Long millis) {
        if (millis == null) {
            return NOON;
        }
        LocalDateTime dateTime = DateBuilder.base(millis).dateTime();
        if (dateTime.getHour() >= 9 && dateTime.getHour() < 16) {
            return NOON;
        }
        if (dateTime.getHour() >= 16 && dateTime.getHour() <= 22) {
            return NIGHT;
        }
        return SUPER;

    }
}
