package com.xjh.dao.foundation;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xjh.common.anno.NotColumn;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.ReflectionUtils;

import cn.hutool.db.Entity;

public class EntityUtils {
    public static Entity pureCreate(Class<?> clz) {
        Table table = clz.getAnnotation(Table.class);
        String tableName = table.value();
        return Entity.create(tableName);
    }

    public static Entity create(Object dd) {
        Class<?> clz = dd.getClass();
        Table table = clz.getAnnotation(Table.class);
        String tableName = table.value();
        Entity entity = Entity.create(tableName);
        ReflectionUtils.resolvePD(dd.getClass()).values().forEach(pd -> {
            try {
                Object value = pd.readValue(dd);
                if (notNone(value)) {
                    String colname = getColumnName(pd.getField());
                    if (CommonUtils.isNotBlank(colname)) {
                        entity.set(colname, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return entity;
    }

    public static Entity idCond(Object dd) throws SQLException {
        Class<?> clz = dd.getClass();
        Table table = clz.getAnnotation(Table.class);
        String tableName = table.value();
        Entity entity = Entity.create(tableName);
        AtomicBoolean hasId = new AtomicBoolean(false);
        ReflectionUtils.resolvePD(dd.getClass()).values().forEach(pd -> {
            try {
                if (pd.getField().getAnnotation(Id.class) != null) {
                    String colname = getColumnName(pd.getField());
                    if (CommonUtils.isNotBlank(colname)) {
                        hasId.set(true);
                        entity.set(colname, pd.readValue(dd));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (!hasId.get()) {
            throw new SQLException("not found id property");
        }
        return entity;
    }

    public static <T> List<T> convertList(List<Entity> entitys, Class<T> clz) {
        List<T> list = new ArrayList<>();
        try {
            for (Entity e : entitys) {
                T t = convert(e, clz);
                if (t != null) {
                    list.add(t);
                }
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T convert(Entity entity, Class<T> clz) {
        try {
            T obj = clz.newInstance();
            convert(entity, obj);
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void convert(Entity entity, Object target) {
        ReflectionUtils.resolvePD(target.getClass()).values().forEach(pd -> {
            try {
                String colname = getColumnName(pd.getField());
                if (CommonUtils.isNotBlank(colname)) {
                    pd.writeValue(target, getValue(entity, colname, pd.getField().getType()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static String getColumnName(Field field) {
        String columnName = field.getName();
        NotColumn notCol = field.getAnnotation(NotColumn.class);
        if (notCol != null) {
            return null;
        }
        Column column = field.getAnnotation(Column.class);
        if (column != null && !CommonUtils.isBlank(column.value())) {
            columnName = column.value();
        }
        return columnName;
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
        Logger.info("不支持的类型：" + columnName + ", " + targetClass);
        return null;
    }

    private static boolean notNone(Object val) {
        if (val == null) {
            return false;
        }
        return !(val instanceof String) || !((String) val).trim().isEmpty();
    }
}
