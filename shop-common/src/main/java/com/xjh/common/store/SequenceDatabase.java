package com.xjh.common.store;

import com.sleepycat.je.DatabaseEntry;
import com.xjh.common.kvdb.Committable;
import com.xjh.common.kvdb.cart.SeqKvDB;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;

import java.nio.charset.StandardCharsets;

public class SequenceDatabase {
    static SeqKvDB seqKvDB = SeqKvDB.inst();

    public static synchronized int nextId(String group) {
        String key = "sequence_" + group;
        Committable committable = seqKvDB.beginTransaction();
        int newId;
        try {
            String value = seqKvDB.get(key, String.class);
            if (CommonUtils.isNotBlank(value)) {
                newId = CommonUtils.parseInt(value, 1);
            } else {
                newId = 1;
            }
            seqKvDB.put(key, String.valueOf(newId + 1));
        } catch (Exception ex) {
            Logger.error("获取订单ID失败:" + group + "," + ex.getMessage());
            throw new RuntimeException("获取订单ID序列失败");
        } finally {
            seqKvDB.commit(committable);
        }
        Logger.info("创建序列号:" + group + ", 返回ID:" + newId);
        return newId;
    }
}
