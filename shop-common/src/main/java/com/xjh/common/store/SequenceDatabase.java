package com.xjh.common.store;

import com.xjh.common.kvdb.Committable;
import com.xjh.common.kvdb.impl.SeqKvDB;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;

public class SequenceDatabase {
    static SeqKvDB seqKvDB = SeqKvDB.inst();

    public static synchronized int nextId(String group) {
        Committable committable = seqKvDB.beginTransaction();
        try {
            String key = "sequence_" + group;
            int newId = CommonUtils.parseInt(seqKvDB.get(key, String.class), 1);
            seqKvDB.put(key, String.valueOf(newId + 1));
            Logger.info("创建序列号:" + group + ", 返回ID:" + newId);
            return newId;
        } catch (Exception ex) {
            Logger.error("获取订单ID失败:" + group + "," + ex.getMessage());
            throw new RuntimeException("获取订单ID序列失败");
        } finally {
            seqKvDB.commit(committable);
        }
    }
}
