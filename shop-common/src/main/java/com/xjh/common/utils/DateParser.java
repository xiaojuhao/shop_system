package com.xjh.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {

    public static String DEF_DT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(Object date) {
        if (date instanceof Date) {
            return (Date) date;
        }
        if (date instanceof String) {
            String strDate = (String)date;
            if(CommonUtils.isBlank(strDate)){
                return null;
            }
            boolean isPureDigit = CommonUtils.isPureDigit(strDate);
            // 毫秒数
            if(isPureDigit && strDate.length() >= 13) {
                Date d = new Date();
                d.setTime(Long.parseLong(strDate));
                return d;
            }
            return toDateSpec((String) date).date();
        }
        return null;
    }
    public static DateSpec toDateSpec(String date) {
        DateSpec spec = new DateSpec();
        if (date != null) {
            // Mar 6, 2022 12:00:00 AM, Mar 6, 2022 12:00:00 PM
            if(date.contains(" AM") || date.contains(" PM")) {
                try{
                    Date d = new Date(date);
                    date = new SimpleDateFormat(DEF_DT_PATTERN).format(d);
                }catch (Exception ex){

                }
            }
            NumberRetriever nr = new NumberRetriever(date);
            spec.year = nr.next(4, null);
            spec.mon = nr.next(2, null);
            spec.day = nr.next(2, null);
            spec.hh = nr.next(2, 0);
            spec.mm = nr.next(2, 0);
            spec.ss = nr.next(2, 0);
        }
        return spec;
    }

    public static DateSpec toDateSpec(Date date) {
        if(date == null){
            return new DateSpec();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return toDateSpec(c);
    }

    public static DateSpec toDateSpec(Calendar c) {
        DateSpec spec = new DateSpec();
        spec.year = c.get(Calendar.YEAR);
        spec.mon = c.get(Calendar.MONTH) + 1;
        spec.day = c.get(Calendar.DATE);
        spec.hh = c.get(Calendar.HOUR_OF_DAY);
        spec.mm = c.get(Calendar.MINUTE);
        spec.ss = c.get(Calendar.SECOND);
        return spec;
    }
    public static class DateSpec {
        Integer year = null;
        Integer mon = null;
        Integer day = null;
        Integer hh = null;
        Integer mm = null;
        Integer ss = null;

        public Date date() {
            if (year == null || mon == null) {
                return null;
            }
//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.YEAR, year);
//            c.set(Calendar.MONTH, mon - 1);
//            c.set(Calendar.DATE, day == null ? 1 : day);
//            c.set(Calendar.HOUR, hh == null ? 0 : hh);
//            c.set(Calendar.MINUTE, mm == null ? 0 : mm);
//            c.set(Calendar.SECOND, ss == null ? 0 : ss);
//            return c.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DEF_DT_PATTERN);
            day = day == null ? 1 : day;
            hh = hh == null ? 0 : hh;
            mm = mm == null ? 0 : mm;
            ss = ss == null ? 0 : ss;
            String dateStr = String.format("%s-%s-%s %s:%s:%s", year, mon, day, hh, mm, ss);
            try {
                return sdf.parse(dateStr);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        public String format(String format){
            if(this.date() == null){
                return "";
            }
            return new SimpleDateFormat(format).format(this.date());
        }

        public DateSpec zeroAM(){
            this.hh = 0;
            this.mm = 0;
            this.ss = 0;
            return this;
        }
        public DateSpec endOfDay(){
            this.hh = 23;
            this.mm = 59;
            this.ss = 59;
            return this;
        }
        public DateSpec hours(int hours){
            this.hh = hours;
            return this;
        }
        private Calendar calendar(){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, mon - 1);
            c.set(Calendar.DATE, day == null ? 1 : day);
            c.set(Calendar.HOUR_OF_DAY, hh == null ? 0 : hh);
            c.set(Calendar.MINUTE, mm == null ? 0 : mm);
            c.set(Calendar.SECOND, ss == null ? 0 : ss);
            return c;
        }
        public DateSpec plusDays(int i){
            Calendar c = calendar();
            c.add(Calendar.DATE, i);
            return toDateSpec(c);
        }

        public DateSpec plusMinutes(int i){
            Calendar c = calendar();
            c.add(Calendar.MINUTE, i);
            return toDateSpec(c);
        }

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
