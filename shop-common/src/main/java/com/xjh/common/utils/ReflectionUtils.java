package com.xjh.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {
    public static List<PropertyDescriptor> getAnnotatedPDs(Class<?> clazz,
                                                           Class<? extends Annotation> annotationClass) {
        List<PropertyDescriptor> pds = new ArrayList<>();
        Map<String, PropertyDescriptor> map = resolvePD(clazz);
        map.forEach((fieldName, pd) -> {
            if (pd.field.isAnnotationPresent(annotationClass)) {
                pds.add(pd);
            }
        });
        return pds;
    }

    static Map<Class<?>, Map<String, PropertyDescriptor>> pdCache = new ConcurrentHashMap<>();

    public static Map<String, PropertyDescriptor> resolvePD(Class<?> clazz) {
        if (pdCache.containsKey(clazz)) {
            return pdCache.get(clazz);
        }
        Map<String, PropertyDescriptor> pdMap = new HashMap<>();
        for(PropertyDescriptor pd : resolvePDList(clazz)){
            pdMap.put(pd.name, pd);
        }
        pdCache.put(clazz, pdMap);
        return pdMap;
    }

    public static List<PropertyDescriptor> resolvePDList(Class<?> clazz) {
        List<PropertyDescriptor> pds = new ArrayList<>();
        Map<String, Method> methodMap = new HashMap<>();
        List<Field> fieldMap = new ArrayList<>();
        resolveMethods(clazz, methodMap);
        resolveFields(clazz, fieldMap);
        Set<String> added = new HashSet<>();
        fieldMap.forEach(f -> {
            String name = f.getName();
            if(added.contains(name)){
                return;
            }
            added.add(name);
            Method read = methodMap.get("get" + capitalize(name));
            Method write = methodMap.get("set" + capitalize(name));
            if (read != null && write != null) {
                PropertyDescriptor pd = new PropertyDescriptor();
                pd.field = f;
                pd.read = read;
                pd.write = write;
                pd.name = name;
                pds.add(pd);
            }
        });
        return pds;
    }

    public static void resolveMethods(Class<?> clazz, Map<String, Method> methodMap) {
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (filter(m)) {
                methodMap.putIfAbsent(m.getName(), m);
            }
        }
        methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (filter(m)) {
                methodMap.putIfAbsent(m.getName(), m);
            }
        }
        if (clazz.getSuperclass() != null) {
            resolveMethods(clazz.getSuperclass(), methodMap);
        }
        for (Class<?> intf : clazz.getInterfaces()) {
            resolveMethods(intf, methodMap);
        }
    }

    private static boolean filter(Method m) {
        return !Modifier.isFinal(m.getModifiers())
                && !Modifier.isNative(m.getModifiers())
                && !m.getName().equals("finalize")
                && !m.getName().equals("equals")
                && !m.getName().equals("toString")
                && !(m.getName().contains("$"));
    }

    private static void resolveFields(Class<?> clazz, List<Field> fieldList) {
        Field[] fields = clazz.getFields();
        fieldList.addAll(Arrays.asList(fields));
        fields = clazz.getDeclaredFields();
        fieldList.addAll(Arrays.asList(fields));
        if (clazz.getSuperclass() != null) {
            resolveFields(clazz.getSuperclass(), fieldList);
        }
        for (Class<?> intf : clazz.getInterfaces()) {
            resolveFields(intf, fieldList);
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static class PropertyDescriptor {
        String name;
        Field field;
        Method read;
        Method write;

        public Object readValue(Object bean) throws RuntimeException {
            if (bean != null && read != null) {
                try {
                    return read.invoke(bean);
                } catch (Exception ex) {
                    return new RuntimeException(ex);
                }
            } else {
                return null;
            }
        }

        public void writeValue(Object bean, Object val) throws RuntimeException {
            if (bean != null && write != null) {
                try {
                    write.invoke(bean, val);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public Field getField() {
            return field;
        }

        public Method getRead() {
            return read;
        }

        public Method getWrite() {
            return write;
        }
    }
}
