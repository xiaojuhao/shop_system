package com.xjh.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.xjh.common.anno.UploadField;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowHeader<T> {
    public List<String> headerNames = new ArrayList<>();
    Map<String, Item> nameMap = new HashMap<>();
    Map<Integer, Item> indexMap = new HashMap<>();

    Map<String, ReflectionUtils.PropertyDescriptor> namePd = new HashMap<>();

    Class<T> dataClz;

    public T newData(){
        try {
            return dataClz.newInstance();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public RowHeader(Class<T> clz) {
        dataClz = clz;
        initHeader();
    }

    public void initHeader(){
        List<ReflectionUtils.PropertyDescriptor> pds = ReflectionUtils.resolvePDList(this.dataClz);
        for (ReflectionUtils.PropertyDescriptor pd : pds) {
            UploadField anno = pd.field.getAnnotation(UploadField.class);
            if (anno != null) {
                String[] names = anno.value();
                for (String name : names) {
                    if (namePd.containsKey(name)) {
                        throw new RuntimeException("Class对象表格名称重复:" + name);
                    }
                    namePd.put(name, pd);
                }
            }
        }
    }

    public void addItem(int index, String name) {
        Item item = new Item();
        item.index = index;
        item.name = name;
        if (nameMap.containsKey(name)) {
            throw new RuntimeException("Excel表格字段名称重复:" + name);
        }
        nameMap.put(name, item);
        indexMap.put(index, item);
        headerNames.add(name);
    }

    public ReflectionUtils.PropertyDescriptor getIndexPd(int index) {
        return getPd(getItem(index));
    }

    public Item getItem(int index) {
        return indexMap.get(index);
    }

    public String getIndexName(int index){
        Item item = getItem(index);
        return item != null ? item.name : "";
    }

    public int nameIndex(String name){
        Item item = nameMap.get(name);
        return item != null ? item.index : -1;
    }

    public ReflectionUtils.PropertyDescriptor getPd(Item item) {
        if (item == null) {
            return null;
        }
        return namePd.get(item.getName());
    }

    @Data
    public static class Item {
        String name;
        int index;
    }
}
