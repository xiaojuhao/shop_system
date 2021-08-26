package com.xjh.common.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

@SuppressWarnings({"unused"})
public class CommonUtils {
    public static <V, R> List<R> collect(List<V> list, Function<V, R> mapper) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        List<R> retList = new ArrayList<>();
        for (V v : list) {
            R r = mapper.apply(v);
            if (r != null) {
                retList.add(r);
            }
        }
        return retList;
    }

    public static <V> List<V> filter(List<V> list, Predicate<V> test) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        return list.stream().filter(Objects::nonNull).filter(test).collect(Collectors.toList());
    }

    public static <T> void addList(List<T> list, List<T> src) {
        if (list != null) {
            forEach(src, it -> {
                if (it != null) {
                    list.add(it);
                }
            });
        }
    }

    public static <T> T firstOf(Collection<T> coll) {
        if (coll == null || coll.size() == 0) {
            return null;
        } else {
            return coll.stream().findFirst().orElse(null);
        }
    }

    public static void safeRun(Runnable run) {
        try {
            if (run == null) {
                return;
            }
            run.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void safeRun(Collection<Runnable> runs) {
        forEach(runs, CommonUtils::safeRun);
    }

    public static <T> T orElse(T v, T def) {
        return v != null ? v : def;
    }

    public static String formatMoney(Number value) {
        if (value == null) {
            return "0.00";
        }
        return new DecimalFormat("0.00").format(value);
    }

    public static String formatSeconds(long seconds) {
        if (seconds <= 60) {
            return padding(seconds) + "秒";
        }
        if (seconds <= 3600) {
            return padding((seconds / 60))
                    + "分" + padding((seconds % 60)) + "秒";
        }
        if (seconds <= 3600 * 24) {
            return padding((seconds / 3600)) + "时"
                    + padding(((seconds % 3600) / 60)) + "分"
                    + padding((seconds % 60)) + "秒";
        }
        return padding((seconds / (3600 * 24))) + "天" +
                padding((seconds % (3600 * 24) / 3600)) + "时"
                + padding(((seconds % 3600) / 60)) + "分"
                + padding((seconds % 60)) + "秒";
    }

    private static String padding(long time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    public static boolean ne(Object a, Object b) {
        return !eq(a, b);
    }

    public static boolean eq(Object a, Object b) {
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof LocalDateTime && b instanceof LocalDateTime) {
            return ((LocalDateTime) a).isEqual((LocalDateTime) b);
        }
        return a.equals(b);
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String reflectString(Object data) {
        try {
            if (data == null) {
                return "null";
            }
            if (data instanceof String || data instanceof Number) {
                return data.toString();
            }
            return JSON.toJSONString(data);
        } catch (Exception ex) {
            Logger.error("序列化对象失败:" + ex.getMessage());
            return "error:" + ex.getMessage();
        }
    }

    public static String readFile(String file) throws Exception {
        InputStreamReader ipr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(ipr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        br.close();
        ipr.close();
        return sb.toString();
    }

    public static Integer toInt(Object value, Integer def) {
        try {
            if (value == null) {
                return def;
            }
            return parseInt(value.toString(), def);
        } catch (Exception ex) {
            return def;
        }
    }

    public static Integer parseInt(String str, Integer def) {
        try {
            if (str == null) {
                return def;
            }
            return new Double(Double.parseDouble(str)).intValue();
        } catch (Exception ex) {
            return def;
        }
    }

    public static boolean isDigit(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static Boolean parseBoolean(Object val, Boolean def) {
        try {
            if (val == null) {
                return def;
            }
            if (val instanceof Boolean) {
                return (Boolean) val;
            }
            return Boolean.parseBoolean(val.toString());
        } catch (Exception ex) {
            return def;
        }
    }

    public static Short parseShort(Object val, Short def) {
        try {
            if (val == null) {
                return def;
            }
            if (val instanceof Short) {
                return (Short) val;
            }
            return Short.parseShort(val.toString());
        } catch (Exception ex) {
            return def;
        }
    }

    public static Double parseDouble(Object val, Double def) {
        try {
            if (val == null) {
                return def;
            }
            if (val instanceof Double) {
                return (Double) val;
            }
            if (val.toString().length() == 0) {
                return def;
            }
            return Double.parseDouble(val.toString());
        } catch (Exception ex) {
            return def;
        }
    }

    public static Double parseMoney(String money, Double def) {
        if (isBlank(money)) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < money.length(); i++) {
            char c = money.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                sb.append(c);
            }
        }
        Double val = parseDouble(sb, def);
        if (val != null) {
            DecimalFormat decimalFormat = new DecimalFormat("##.00");
            String formattedVal = decimalFormat.format(val);
            return parseDouble(formattedVal, def);
        }
        return def;
    }

    public static Long parseLong(Object str, Long def) {
        try {
            if (str == null) {
                return def;
            }
            if (str instanceof Long) {
                return (Long) str;
            }
            return new Double(Double.parseDouble(str.toString())).longValue();
        } catch (Exception ex) {
            return def;
        }
    }

    /**
     * 数组自然排序
     *
     * @param list list
     * @param <T>  type
     * @return result
     */
    public static <T extends Comparable<T>> boolean inNaturalOrder(List<T> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        if (list.size() == 1) {
            return true;
        }
        T prev = list.get(0);
        if (prev == null) {
            return false;
        }
        for (int i = 1; i < list.size(); i++) {
            T curr = list.get(i);
            if (curr == null) {
                return false;
            }
            if (prev.compareTo(curr) > 0) {
                return false;
            }
            prev = curr;
        }
        return true;
    }

    static Random RANDOM_RANDOMCHAR = new Random();

    public static Character randomChar() {
        return randomChar("0123456789abcdefghijklmopqrstuvwxyz0123456789ABCDEFGHIJKLMOPQRSTUVWXYZ0123456789");
    }

    public static Character randomChar(String source) {
        int random = RANDOM_RANDOMCHAR.nextInt(source.length());
        return source.charAt(random);
    }

    public static String randomStr(Random rnd, int length, String source) {
        if (length <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = rnd.nextInt(source.length());
            builder.append(source.charAt(random));
        }
        return builder.toString();
    }

    public static String randomStr(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(randomChar());
        }
        return builder.toString();
    }

    static Random RANDOM_NUMBER = new Random();

    public static int randomNumber(int lower, int upper) {
        if (lower > upper) {
            return randomNumber(upper, lower);
        }
        if (lower == upper) {
            return lower;
        }
        int randInt = RANDOM_NUMBER.nextInt(upper - lower + 1);
        return lower + randInt;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> boolean inNaturalOrder(T... ts) {
        if (ts == null) {
            return false;
        }
        List<T> list = new ArrayList<>(Arrays.asList(ts));
        return inNaturalOrder(list);
    }

    static final Random RANDOMIZE_LONG = new Random();

    public static Long randomizeLong(Long val) {
        if (val == null) {
            return null;
        }
        if (val <= 0) {
            val = -val;
        }
        long result = 0;
        while (val > 0) {
            boolean b = LONG_RANDOMIZE_RANDOM.nextBoolean();
            result = (((result << 4) | (val & 0b1111)) << 1) | (b ? 1 : 0);
            val = val >>> 4;
        }
        return result;
    }

    static final Random LONG_RANDOMIZE_RANDOM = new Random();
    static final String LONG_RANDOMIZE_LETTERS = "123456789abcdefghijkmnpqrstuvwxyz";


    public static String randomizeToString(Long val) {
        if (val == null) {
            return "";
        }
        if (val <= 0) {
            val = -val;
        }
        StringBuilder sb = new StringBuilder();
        while (val > 0) {
            int x = (int) (val & 0b1111);
            val = val >> 4;
            boolean b = LONG_RANDOMIZE_RANDOM.nextBoolean();
            x = (x << 1) | (b ? 1 : 0);
            char c = LONG_RANDOMIZE_LETTERS.charAt(x);
            if (b) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Long restoreRandomizedLong(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        long val = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            int index = LONG_RANDOMIZE_LETTERS.indexOf(Character.toLowerCase(c));
            val = val << 4 | (index >> 1);
        }
        return val;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String maskPhone(String phoneNo) {
        if (isBlank(phoneNo)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phoneNo.length(); i++) {
            if (i == 3 || i == 4 || i == 5 || i == 6) {
                sb.append("*");
            } else {
                sb.append(phoneNo.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String weekId(Date date) {
        if (date == null) {
            return null;
        }
        Date mondayDate = DateBuilder.base(date).mondayDate();
        return "W" + DateBuilder.base(mondayDate).format("yyMMdd");
    }

    public static String weekId(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        Date mondayDate = DateBuilder.base(dateTime).mondayDate();
        return "W" + DateBuilder.base(mondayDate).format("yyMMdd");
    }

    public static List<Integer> randomSplitNumber(Integer totalNum, int size) {
        if (totalNum == null) {
            return Collections.emptyList();
        }
        // 不足均分
        if (totalNum < size) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < totalNum; i++) {
                list.add(1);
            }
            for (int i = totalNum; i < size; i++) {
                list.add(0);
            }
            return list;
        }
        if (size <= 1) {
            return Collections.singletonList(totalNum);
        }
        if (size == 2) {
            return RandomBonusUtils.createBonusList(totalNum, size, totalNum / 4, totalNum, 1);
        }
        if (size == 3) {
            return RandomBonusUtils.createBonusList(totalNum, size, 1, totalNum / 2 * 3, 0.3);
        }
        return RandomBonusUtils.createBonusList(totalNum, size, 1, totalNum / size * 3, 1.0 / size);
    }

    public static <T> List<T> tailList(List<T> srcList, int tailNum) {
        if (tailNum <= 0) {
            return srcList;
        }
        if (srcList == null) {
            return new ArrayList<>();
        }

        if (srcList.size() <= tailNum) {
            return srcList;
        }
        List<T> list = new ArrayList<>();
        for (int i = srcList.size() - tailNum; i < srcList.size(); i++) {
            list.add(srcList.get(i));
        }
        return list;
    }

    public static Set<String> splitAsSet(String str, String delimiter) {
        Set<String> set = new HashSet<>();
        if (isBlank(str)) {
            return set;
        }
        if (isBlank(delimiter)) {
            delimiter = ",";
        }
        String[] arrays = str.split(delimiter);
        for (String s : arrays) {
            if (isNotBlank(s)) {
                set.add(s);
            }
        }
        return set;
    }

    public static <K, V> Map<K, V> listToMap(Collection<V> coll, Function<V, K> fun) {
        Map<K, V> map = new HashMap<>();
        if (coll != null) {
            for (V v : coll) {
                if (v != null) {
                    K k = fun.apply(v);
                    if (k != null) {
                        map.put(k, v);
                    }
                }
            }
        }
        return map;
    }

    public static <T> void forEach(Collection<T> coll, Consumer<T> consumer) {
        if (coll == null) {
            return;
        }
        for (T t : coll) {
            if (t != null) {
                consumer.accept(t);
            }
        }
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static String stringify(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Date) {
            return DateBuilder.base((Date) value).format("yyyy-MM-dd HH:mm:ss");
        }
        if (value instanceof LocalDateTime) {
            return DateBuilder.base((LocalDateTime) value).format("yyyy-MM-dd HH:mm:ss");
        }
        if (value instanceof Number) {
            return value.toString();
        }
        return JSON.toJSONString(value);
    }

    public static String stringJoin(Collection<String> coll, String delimiter) {
        if (isEmpty(coll)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : coll) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static List<String> readLines(InputStream stream) throws Exception {
        if (stream == null) {
            return new ArrayList<>();
        }
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    public static String readAsString(InputStream stream) throws Exception {
        return String.join(" ", readLines(stream));
    }

    public static void setField(Object bean, Class<?> beanClass, String fieldName, Object fieldVal)
            throws IllegalAccessException {
        if (bean == null || fieldVal == null) {
            return;
        }
        Field[] fields = beanClass.getDeclaredFields();
        for (Field f : fields) {
            String beanFieldName = f.getName();
            if (beanFieldName.equalsIgnoreCase(fieldName)) {
                if (f.getType().isAssignableFrom(fieldVal.getClass())) {
                    f.setAccessible(true);
                    f.set(bean, fieldVal);
                }
            }
        }
    }

    public static void setFieldQuietly(Object bean, Class<?> beanClass, String fieldName, Object fieldVal) {
        try {
            setField(bean, beanClass, fieldName, fieldVal);
        } catch (Exception ex) {
            // do nothing
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPhone(String phone) {
        if (length(phone) != 11) {
            return false;
        }
        return phone.startsWith("1");
    }

    public static int length(String str) {
        if (str == null) {
            return 0;
        }
        return str.length();
    }

    static Random confoundNumberSeed = new Random();
    static Long MAX_CONFOUND_VAL = -1L >>> 14;

    /**
     * 混淆数字(无法反解)
     *
     * @param val val
     * @return result
     */
    public static Long confoundLong(Long val) {
        if (val == null) {
            val = System.nanoTime();
        }
        if (val <= 0) {
            val = -val;
        }
        if (val > MAX_CONFOUND_VAL) {
            throw new RuntimeException("value值太大,无法插入随机数");
        }
        // 每4bit插入一个随机bit, 保证long不能大于
        long rs = 0;
        while (val > 0) {
            int x = (int) (val & 0b1111);
            val = val >> 4;
            boolean b = confoundNumberSeed.nextBoolean();
            x = (x << 1) | (b ? 1 : 0);
            if (b) {
                rs = rs << 4 | x | 1;
            } else {
                rs = rs << 4 | x;
            }
        }
        return rs;
    }

    public static String md5hex(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(byteHEX(b));
            }
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String byteHEX(byte ib) {
        char[] Digit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[]{Digit[ib >>> 4 & 15], Digit[ib & 15]};
        return new String(ob);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static void emptyAction() {
    }
}
