package com.xjh.common.kvdb.cart;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;

public class SeqKvDB extends AbstractBerkeleyKvDB {
    @Override
    public String getDbName() {
        return "sequence";
    }
}
