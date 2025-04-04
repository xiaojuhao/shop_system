package com.xjh.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.ReflectionUtils.PropertyDescriptor;

import java.util.Map;

public class CopyUtils {

    public static <T, R> R convert(T obj, Class<R> clz) {
        try {
            R r = clz.newInstance();
            copy(obj, r);
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T deepClone(T v) {
        JSONObject j = JSONBuilder.toJSON(v);
        return (T) j.toJavaObject(v.getClass());
    }

    public static <T> T cloneObj(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            T _new = (T) obj.getClass().newInstance();
            copy(obj, _new);
            return _new;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Result<Object> copy(Object from, Object to) {
        if (from == null || to == null) {
            return Result.fail("拷贝错误");
        }

        Map<String, PropertyDescriptor> fromPds = ReflectionUtils.resolvePD(from.getClass());
        Map<String, PropertyDescriptor> toPds = ReflectionUtils.resolvePD(to.getClass());
        try {
            fromPds.forEach((field, fromPd) -> {
                try {
                    PropertyDescriptor toPd = toPds.get(field);
                    if (toPd != null) {
                        Class<?> toType = toPd.getRead().getReturnType();
                        Class<?> fromType = fromPd.getRead().getReturnType();
                        if (toType.isPrimitive() || toType.isAssignableFrom(fromType)) {
                            Object v = fromPd.readValue(from);
                            if (v != null || !toType.isPrimitive()) {
                                toPd.writeValue(to, v);
                            }
                        } else {
                            Logger.info("类型不匹配:"
                                    + to.getClass() + "(" + toPd.getRead().getReturnType() + ") >> "
                                    + from.getClass() + "(" + fromPd.getRead().getReturnType() + ")");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return Result.success(to);
        } catch (Exception ex) {
            return Result.fail("拷贝异常:" + ex.getMessage());
        }
    }
}
