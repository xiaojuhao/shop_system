package com.xjh.common.valueobject;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DishesValidTime {
    Integer day;
    String start;
    String end;

    public String asStr() {
        return day + "_" + start + "_" + end;
    }

    public int getStartHour() {
        if (start == null || start.indexOf(":") <= 0) {
            return 0;
        }
        return Integer.parseInt(start.split(":")[0]);
    }

    public int getStartMinute() {
        if (start == null || start.indexOf(":") <= 0) {
            return 0;
        }
        return Integer.parseInt(start.split(":")[1]);
    }

    public int getEndHour() {
        if (end == null || end.indexOf(":") <= 0) {
            return 0;
        }
        return Integer.parseInt(end.split(":")[0]);
    }

    public int getEndMinute() {
        if (end == null || end.indexOf(":") <= 0) {
            return 0;
        }
        return Integer.parseInt(end.split(":")[1]);
    }

    public boolean isValid() {
        return day != null && start != null && end != null;
    }

    public static DishesValidTime from(String str) {
        String[] arr = str.split("_");
        if (arr.length != 3) {
            return null;
        }
        DishesValidTime t = new DishesValidTime();
        t.day = Integer.parseInt(arr[0]);
        t.start = arr[1];
        t.end = arr[2];
        return t;
    }

    public boolean testValid(LocalDateTime time) {
        if (!isValid()) {
            return false;
        }
        if (time.getDayOfWeek().getValue() != day) {
            return false;
        }
        int intStart = getStartHour() * 60 + getStartMinute();
        int intEnd = getEndHour() * 60 + getEndMinute();
        int intTime = time.getHour() * 60 + time.getMinute();
        return intStart <= intTime && intTime <= intEnd;
    }
}
