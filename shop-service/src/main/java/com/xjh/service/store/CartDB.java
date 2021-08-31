package com.xjh.service.store;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;
import com.xjh.dao.dataobject.Cart;

public class CartDB extends AbstractBerkeleyKvDB<Cart> {
    private static class Inst {
        public static CartDB inst = new CartDB();
    }

    public static CartDB inst() {
        return Inst.inst;
    }

    private CartDB() {
    }

    public String getDbName() {
        return "cart";
    }
}
