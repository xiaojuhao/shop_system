package com.xjh.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueConvert {
    static ThreadLocal<String> DATE_PATTERN_THL = new ThreadLocal<>();
    static String DEF_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    static String DEF_DATE_PATTERN = "yyyy-MM-dd";

    static Map<CompositeKey, Converter<?, ?>> map = new HashMap<>();

    public static void withDatePattern(String pattern){
        DATE_PATTERN_THL.set(pattern);
    }

    public static void clearDatePattern(){
        DATE_PATTERN_THL.remove();
    }

    public static Object convert(Object input, Class<?> oclz) {
        if (input == null) {
            return getDefaultValue(oclz);
        }
        Class<?> iclz = input.getClass();
        if (iclz == oclz) {
            return input;
        }
        Converter converter = getConverter(iclz, oclz);
        if (converter != null) {
            return converter.convert(input);
        }
        // String 转 DTO
        if (iclz == String.class && isPOJO(oclz)) {
            if (CommonUtils.isNotBlank((String) input)) {
                return JSON.parseObject((String) input, oclz);
            }
        }
        return null;
    }

    public static Converter<?, ?> getConverter(Class<?> iclz, Class<?> oclz) {
        Converter<?, ?> converter = map.get(CompositeKey.create(iclz, oclz));
        if (converter == null && iclz != Object.class) {
            converter = getConverter(Object.class, oclz);
        }
        return converter;
    }

    public interface Converter<I, O> {
        O convert(I input);
    }

    static {
        // to string
        register(Object.class, String.class, new Converter<Object, String>() {
            public String convert(Object input) {
                return stringify(input);
            }
        });
        // string -> int
        register(Object.class, Integer.TYPE, new Converter<Object, Integer>() {
            public Integer convert(Object input) {
                return CommonUtils.parseInt(input, 0);
            }
        });
        register(Object.class, Integer.class, new Converter<Object, Integer>() {
            public Integer convert(Object input) {
                return CommonUtils.parseInt(input, null);
            }
        });
        // str -> long
        register(String.class, Long.TYPE, new Converter<String, Long>() {
            public Long convert(String input) {
                if (CommonUtils.isPureDigit(input)) {
                    return Long.parseLong(input);
                }
                return 0L;
            }
        });
        register(String.class, Long.class, new Converter<String, Long>() {
            public Long convert(String input) {
                if (CommonUtils.isPureDigit(input)) {
                    return Long.parseLong(input);
                }
                return null;
            }
        });

        register(BigInteger.class, Long.class, new Converter<BigInteger, Long>() {
            public Long convert(BigInteger input) {
                if(input == null){
                    return null;
                }
                return input.longValue();
            }
        });

        register(BigDecimal.class, Long.class, new Converter<BigDecimal, Long>() {
            public Long convert(BigDecimal input) {
                if(input == null){
                    return null;
                }
                return input.longValue();
            }
        });

        register(BigInteger.class, Integer.class, new Converter<BigInteger, Integer>() {
            public Integer convert(BigInteger input) {
                if(input == null){
                    return null;
                }
                return input.intValue();
            }
        });

        register(BigDecimal.class, Integer.class, new Converter<BigDecimal, Integer>() {
            public Integer convert(BigDecimal input) {
                if(input == null){
                    return null;
                }
                return input.intValue();
            }
        });

        // to BigDecimal
        register(Object.class, BigDecimal.class, new Converter<Object, BigDecimal>() {
            public BigDecimal convert(Object input) {
                return new BigDecimal(input.toString());
            }
        });
        // to bool
        register(Object.class, Boolean.TYPE, new Converter<Object, Boolean>() {
            public Boolean convert(Object input) {
                return objToBool(input, false);
            }
        });
        register(Object.class, Boolean.class, new Converter<Object, Boolean>() {
            public Boolean convert(Object input) {
                return objToBool(input, null);
            }
        });
        // str -> date
        register(Object.class, Date.class, new Converter<Object, Date>() {
            public Date convert(Object input) {
                return DateParser.parseDate(input);
            }
        });
    }

    static Object getDefaultValue(Class<?> clz) {
        if (clz == Integer.TYPE || clz == Long.TYPE) {
            return 0;
        }
        if (clz == Float.TYPE || clz == Double.TYPE) {
            return 0.0;
        }
        if (clz == Byte.TYPE || clz == Character.TYPE) {
            return '0';
        }
        if (clz == Boolean.TYPE) {
            return false;
        }
        if (clz == Short.TYPE) {
            return 0;
        }
        return null;
    }

    static Boolean objToBool(Object obj, Boolean def) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        // 数字类
        if (obj instanceof Integer) {
            return (Integer) obj > 0;
        }
        if (obj instanceof Long) {
            return (Long) obj > 0;
        }
        // 字符串
        if (obj instanceof String) {
            String str = (String) obj;
            if (CommonUtils.eq("true", str) || CommonUtils.eq("1", str) || CommonUtils.eq("y", str)) {
                return true;
            }
            if (CommonUtils.eq("false", str) || CommonUtils.eq("0", str) || CommonUtils.eq("n", str)) {
                return false;
            }
        }
        return def;
    }

    static <T1, T2> void register(Class<T1> a, Class<T2> b, Converter<T1, T2> converter) {
        map.put(CompositeKey.create(a, b), converter);
    }

    static Map<Class<?>, Boolean> POJO_CACHE = new ConcurrentHashMap<>();

    static boolean isPOJO(Class<?> clz) {
        if (POJO_CACHE.containsKey(clz)) {
            return POJO_CACHE.get(clz);
        }
        if (clz == null || clz.getPackage() == null) {
            return false;
        }
        boolean rs = clz.getPackage().getName().startsWith("com.cpic");
        POJO_CACHE.put(clz, rs);
        return rs;
    }

    public static String stringify(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Number || obj instanceof String) {
            return obj.toString();
        }
        if (obj instanceof Date) {
            String datePatten = DEF_DATETIME_PATTERN;
            if(CommonUtils.isNotBlank(DATE_PATTERN_THL.get())){
                datePatten = DATE_PATTERN_THL.get();
            }
            return new SimpleDateFormat(datePatten).format((Date) obj);
        }
        if (isPOJO(obj.getClass())) {
            return JSONObject.toJSONString(obj);
        }
        return obj.toString();
    }
}
