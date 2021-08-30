package com.xjh.common.kvdb.cart;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;

import javax.inject.Singleton;

@Singleton
public class CartKvDB extends AbstractBerkeleyKvDB {
    private static class Inst {
        public static CartKvDB inst = new CartKvDB();
    }

    public static CartKvDB inst() {
        return Inst.inst;
    }

    private CartKvDB() {
    }

    public String getDbName() {
        return "cart";
    }
}
