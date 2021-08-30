package com.xjh.common.kvdb;

public interface KvDB {
    void beginTransaction();

    void commit();

    void put(String key, Object val);

    void remove(String key);

    <T> T get(String key, Class<T> clz);

}
