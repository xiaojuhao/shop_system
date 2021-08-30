package com.xjh.common.kvdb;

public interface KvDB {
    Committable beginTransaction();

    void commit(Committable committable);

    void put(String key, Object val);

    void remove(String key);

    <T> T get(String key, Class<T> clz);

}
