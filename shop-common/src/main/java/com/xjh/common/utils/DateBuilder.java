package com.xjh.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateBuilder {
    public final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private LocalDateTime localDateTime = null;
    private static final double SECONDS_OF_HOUR = 3600;
    private static final double SECONDS_OF_DAY = SECONDS_OF_HOUR * 24;

    public static DateBuilder now() {
        return base(LocalDateTime.now());
    }

    public static DateBuilder today() {
        return now().setTime("00:00:00");
    }

    public static DateBuilder tomorrow() {
        return today().plusDays(1);
    }

    public static DateBuilder yestoday() {
        return today().plusDays(-1);
    }

    public static DateBuilder base(Long mills) {
        DateBuilder builder = new DateBuilder();
        if (mills != null && mills > 0) {
            builder.localDateTime = base(new Date(mills)).dateTime();
        }
        return builder;
    }

    public static DateBuilder base22(Date date) {
        DateBuilder builder = new DateBuilder();
        if (date != null) {
            builder.localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return builder;
    }

    public static DateBuilder base(LocalDate localDate) {
        DateBuilder builder = new DateBuilder();
        if (localDate != null) {
            builder.localDateTime = localDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
        }
        return builder;
    }

    public static Date toDate(LocalDate localDate) {
        return base(localDate).date();
    }

    public static LocalDate toLocalDate(Date date) {
        return base(date).dateTime().toLocalDate();
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
        return intervalSeconds(LocalDateTime.now(), toDate);
    }

    public static long intervalSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        long leftSeconds = ChronoUnit.SECONDS.between(start, end);
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

    public static long daythOfYear(LocalDateTime date) {
        LocalDateTime start = base(base(date).format("yyyy") + "0101").localDateTime;
        return diffDays(start, date);
    }

    public static long diffDays(LocalDateTime start, LocalDateTime end) {
        long seconds = intervalSeconds(start, end);
        double delta = Math.ceil(seconds / SECONDS_OF_DAY);
        return new Double(delta).longValue();
    }

    public static long diffHours(LocalDateTime start, LocalDateTime end) {
        long seconds = intervalSeconds(start, end);
        double delta = Math.ceil(seconds / SECONDS_OF_HOUR);
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

    public String timeStr() {
        return format(DATETIME_PATTERN);
    }

    public DateBuilder zeroAM() {
        if (this.localDateTime != null) {
            this.localDateTime = this.localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
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

    public long mills() {
        return base(this.localDateTime).date().getTime();
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

}
