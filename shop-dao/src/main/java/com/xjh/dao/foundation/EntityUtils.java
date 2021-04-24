package com.xjh.dao.foundation;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.ReflectionUtils;

import cn.hutool.db.Entity;

public class EntityUtils {

    public static void convert(Entity entity, Object target) {
        ReflectionUtils.resolvePD(target.getClass()).values().forEach(pd -> {
            try {
                Field field = pd.getField();
                String columnName = field.getName();
                Column column = field.getAnnotation(Column.class);
                if (column != null && !CommonUtils.isBlank(column.value())) {
                    columnName = column.value();
                }
                pd.writeValue(target, getValue(entity, columnName, field.getType()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static Object getValue(Entity entity, String columnName, Class<?> targetClass) {
        if (targetClass == String.class) {
            return entity.getStr(columnName);
        }
        if (targetClass == Integer.class) {
            return entity.getInt(columnName);
        }
        if (targetClass == Long.class) {
            return entity.getLong(columnName);
        }
        if (targetClass == Double.class) {
            return entity.getDouble(columnName);
        }
        if (targetClass == BigDecimal.class) {
            return entity.getBigDecimal(columnName);
        }
        if (targetClass == LocalDateTime.class) {
            Object val = entity.get(columnName);
            if (val == null) {
                return null;
            }
            if (val instanceof Long) {
                return DateBuilder.base((Long) val).dateTime();
            }
            if (val instanceof Date) {
                return DateBuilder.base((Date) val).dateTime();
            }
            if (val instanceof LocalDateTime) {
                return val;
            }
        }
        System.out.println("不支持的类型：" + columnName + ", " + targetClass);
        return null;
    }
}
