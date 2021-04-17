package com.xjh.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateBuilder {
    private LocalDateTime localDateTime = null;
    private static final double SECONDS_OF_DAY = 60 * 60 * 24;

    public static DateBuilder now() {
        return base(LocalDateTime.now());
    }

    public static DateBuilder today() {
        return now().setTime("00:00:00");
    }

    public static DateBuilder tomorrow() {
        return today().plusDays(1);
    }

    public static DateBuilder base(LocalDateTime dateTime) {
        DateBuilder builder = new DateBuilder();
        if (dateTime != null) {
            builder.localDateTime = dateTime;
        }
        return builder;
    }

    public static DateBuilder base(Date date) {
        DateBuilder builder = new DateBuilder();
        if (date != null) {
            builder.localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return builder;
    }

    public static long leftSeconds(LocalDateTime toDate) {
        if (toDate == null) {
            return 0;
        }
        long leftSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), toDate);
        if (leftSeconds < 0) {
            return 0;
        }
        return leftSeconds;
    }

    public static long leftDays(Date toDate) {
        double delta = Math.ceil(leftSeconds(base(toDate).localDateTime) / SECONDS_OF_DAY);
        return new Double(delta).longValue();
    }

    public static long leftDays(LocalDateTime toDate) {
        double delta = Math.ceil(leftSeconds(toDate) / SECONDS_OF_DAY);
        return new Double(delta).longValue();
    }

    private static LocalDateTime min(LocalDateTime x, LocalDateTime y) {
        if (x == null) {
            return y;
        }
        if (y == null) {
            return x;
        }
        return x.isBefore(y) ? x : y;
    }

    public static long leftNaturalDays(LocalDateTime toDate) {
        if (toDate == null) {
            return 0;
        }
        toDate = toDate.withHour(23).withMinute(59).withSecond(59);
        return leftDays(toDate);
    }


    /**
     * 设置年月日时分秒
     *
     * @param date date
     * @return builder
     */
    public static DateBuilder base(String date) {
        DateBuilder builder = new DateBuilder();
        if (date != null) {
            NumberRetriever nr = new NumberRetriever(date);
            Integer year = nr.next(4, null);
            Integer mon = nr.next(2, null);
            Integer day = nr.next(2, null);
            Integer hh = nr.next(2, 0);
            Integer mm = nr.next(2, 0);
            Integer ss = nr.next(2, 0);
            if (year != null && mon != null && day != null) {
                builder.localDateTime = LocalDateTime.now();
                builder.localDateTime = builder.localDateTime.withYear(year);
                builder.localDateTime = builder.localDateTime.withMonth(mon);
                builder.localDateTime = builder.localDateTime.withDayOfMonth(day);
                builder.localDateTime = builder.localDateTime.withHour(hh);
                builder.localDateTime = builder.localDateTime.withMinute(mm);
                builder.localDateTime = builder.localDateTime.withSecond(ss);
                builder.localDateTime = builder.localDateTime.withNano(0);
            }
        }
        return builder;
    }

    public DateBuilder plusDays(int add) {
        if (this.localDateTime != null) {
            localDateTime = localDateTime.plusDays(add);
        }
        return this;
    }

    /**
     * 周一所在的日期
     *
     * @return date
     */
    public Date mondayDate() {
        if (this.localDateTime == null) {
            return null;
        }
        LocalDateTime mondayDateTime = localDateTime.minusDays(localDateTime.getDayOfWeek().getValue() - 1);
        return Date.from(mondayDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String format(String pattern) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public DateBuilder zeroAM() {
        if (this.localDateTime != null) {
            this.localDateTime = this.localDateTime.withHour(0).withMinute(0).withSecond(0);
        }
        return this;
    }

    /**
     * 设置 时分秒
     *
     * @param time time
     * @return builder
     */
    public DateBuilder setTime(String time) {
        if (this.localDateTime == null) {
            this.localDateTime = LocalDateTime.now();
        }
        NumberRetriever nr = new NumberRetriever(time);
        Integer hh = nr.next(2, 0);
        Integer mm = nr.next(2, 0);
        Integer ss = nr.next(2, 0);
        localDateTime = localDateTime.withHour(hh);
        localDateTime = localDateTime.withMinute(mm);
        localDateTime = localDateTime.withSecond(ss);
        return this;
    }

    public boolean expired() {
        return expiredAt(LocalDateTime.now());
    }

    public boolean expiredAt(LocalDateTime time) {
        if (this.localDateTime == null || time == null) {
            return false;
        }
        return localDateTime.isBefore(time);
    }

    public boolean isAfter(LocalDateTime date) {
        if (this.localDateTime == null || date == null) {
            return false;
        }
        return localDateTime.isAfter(date);
    }

    public boolean isAfter(String date) {
        if (this.localDateTime == null || date == null) {
            return false;
        }
        return localDateTime.isAfter(base(date).dateTime());
    }

    public Date date() {
        if (this.localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime dateTime() {
        return this.localDateTime;
    }

    public static class NumberRetriever {
        String str;
        int pos = 0;

        NumberRetriever(String str) {
            this.str = str;
        }

        Integer next(int maxLen, Integer def) {
            if (str == null) {
                return def;
            }
            StringBuilder sb = new StringBuilder();
            while (sb.length() < maxLen && pos < str.length()) {
                char c = str.charAt(pos++);
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else if (sb.length() > 0) {
                    break;
                }
            }
            if (sb.length() == 0) {
                return def;
            }
            return Integer.parseInt(sb.toString());
        }
    }

}
