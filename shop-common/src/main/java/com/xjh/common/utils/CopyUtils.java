package com.xjh.common.utils;

import java.util.Map;

import com.xjh.common.utils.ReflectionUtils.PropertyDescriptor;

public class CopyUtils {
    public static <T> T cloneObj(T obj) {
        Map<String, PropertyDescriptor> pds = ReflectionUtils.resolvePD(obj.getClass());
        try {
            T _new = (T) obj.getClass().newInstance();
            pds.forEach((field, pd) -> {
                try {
                    pd.writeValue(_new, pd.readValue(obj));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return _new;
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", "克隆对象出现异常" + obj.getClass());
            return null;
        }

    }
}
