package com.xjh.service.store;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;
import com.xjh.common.kvdb.Committable;
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

    public void putInTransaction(String key, Cart val) {
        CartDB db = inst();
        Committable committable = null;
        try {
            committable = db.beginTransaction();
            db.put(key, val);
        } finally {
            db.commit(committable);
        }
    }

    public void removeInTransaction(String key) {
        CartDB db = inst();
        Committable committable = null;
        try {
            committable = db.beginTransaction();
            db.remove(key);
        } finally {
            db.commit(committable);
        }
    }

    public String getDbName() {
        return "cart";
    }
}
