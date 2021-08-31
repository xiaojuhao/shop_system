package com.xjh.common.kvdb.impl;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;
import com.xjh.common.utils.Logger;

public class SeqKvDB extends AbstractBerkeleyKvDB<String> {
    private static class Inst {
        public static SeqKvDB inst = new SeqKvDB();
    }

    private SeqKvDB() {
        Logger.info("初始化KVDB: SeqKvDB");
    }

    @Override
    public String getDbName() {
        return "sequence";
    }

    public static SeqKvDB inst() {
        return Inst.inst;
    }
}
