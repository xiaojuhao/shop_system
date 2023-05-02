package com.xjh.common.utils;

import java.util.Date;

public class DateRange {
    Date from;
    Date end;
    Date index;

    public static DateRange of(String from, String end) {
        return of(DateBuilder.base(from).date(), DateBuilder.base(end).date());
    }

    public static DateRange of(Date from, Date end) {
        DateRange dr = new DateRange();
        dr.from = from;
        dr.end = end;
        dr.index = from;
        return dr;
    }

    public boolean hasNext() {
        long t1 = this.index.getTime();
        long t2 = this.end.getTime();
        return t1 <= t2;
    }

    public Date nextDay() {
        if (hasNext()) {
            Date r = new Date(this.index.getTime());
            this.index.setTime(this.index.getTime() + 24 * 3600 * 1000);
            return r;
        } else {
            return null;
        }
    }

    public String toString() {
        return "DateRange(" + DateBuilder.base(from).format("yyyy-MM-dd") + "," +
                DateBuilder.base(end).format("yyyy-MM-dd") + ")";
    }

    public static void main(String[] args) {
        DateRange dr = DateRange.of("2023-04-01", "2023-04-01");
        while (dr.hasNext()) {
            System.out.println(DateBuilder.base(dr.nextDay()).format("yyyy-MM-dd"));
        }
    }
}
