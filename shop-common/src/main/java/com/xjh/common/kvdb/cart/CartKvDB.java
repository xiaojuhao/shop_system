package com.xjh.common.kvdb.cart;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;

import javax.inject.Singleton;

@Singleton
public class CartKvDB extends AbstractBerkeleyKvDB {
    public String getDbName() {
        return "cart";
    }
}
