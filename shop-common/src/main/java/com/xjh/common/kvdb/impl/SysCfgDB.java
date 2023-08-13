package com.xjh.common.kvdb.impl;

import com.xjh.common.kvdb.AbstractBerkeleyKvDB;
import com.xjh.common.kvdb.Committable;
import com.xjh.common.utils.Logger;
import com.xjh.common.valueobject.CartVO;

import java.util.Properties;

public class SysCfgDB extends AbstractBerkeleyKvDB<String> {
    private static class Inst {
        public static SysCfgDB inst = new SysCfgDB();
    }

    public static SysCfgDB inst() {
        return Inst.inst;
    }

    private SysCfgDB() {
        Logger.info("初始化KVDB: SysCfgDB");
    }

    public void putInTransaction(String key, String val) {
        SysCfgDB db = inst();
        Committable committable = null;
        try {
            committable = db.beginTransaction();
            db.put(key, val);
        } finally {
            db.commit(committable);
        }
    }

    public void removeInTransaction(String key) {
        SysCfgDB db = inst();
        Committable committable = null;
        try {
            committable = db.beginTransaction();
            db.remove(key);
        } finally {
            db.commit(committable);
        }
    }

    public String getDbName() {
        return "syscfg";
    }
}
