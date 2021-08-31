package com.xjh.common.kvdb;

public interface KvDB<T> {
    Committable beginTransaction();

    void commit(Committable committable);

    void put(String key, T val);

    void remove(String key);

     T get(String key, Class<T> clz);

}
