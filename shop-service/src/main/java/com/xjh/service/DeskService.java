package com.xjh.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.store.DeskKvDatabase;
import com.xjh.dao.DeskDAO;
import com.xjh.dao.dataobject.Desk;

@Singleton
public class DeskService {
    @Inject
    DeskDAO deskDAO;

    public void openDesk(Long id) {
        Desk desk = this.getRunningData(id);
        if (desk == null) {
            desk = new Desk();
            desk.setId(id);
            desk.setDeskName(id.toString());
        }
        desk.setStatus(EnumDesKStatus.USED.status());
        desk.setOrderCreateTime(LocalDateTime.now());
        this.saveRunningData(desk);
    }

    public void closeDesk(Long id) {
        Desk desk = this.getRunningData(id);
        if (desk == null) {
            return;
        }
        desk.setStatus(EnumDesKStatus.FREE.status());
        desk.setOrderCreateTime(null);
        this.saveRunningData(desk);
    }

    public List<Desk> getAllDesks() {

        List<Desk> desks = new ArrayList<>();
        try {
            return deskDAO.select(new Desk());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }

    public void saveRunningData(Desk desk) {
        String key = desk.getId() + "_running_data";
        desk.setVerNo(desk.getVerNo() != null ? desk.getVerNo() + 1 : 1);
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        // DatabaseEntry theData = new DatabaseEntry(HessianUtils.serialize(desk));
        DatabaseEntry theData = new DatabaseEntry(JSON.toJSONString(desk).getBytes(StandardCharsets.UTF_8));
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = DeskKvDatabase.getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        db.put(txn, theKey, theData);
        txn.commit();
    }

    public static void main(String[] args) {
        DeskService service = new DeskService();
        for (long i = 1; i < 10000; i++) {
            Desk d = service.getRunningData(i % 10);
            if (d == null) {
                continue;
            }
            d.setVerNo(d.getVerNo() != null ? d.getVerNo() + 1 : 1);
            d.setOrderCreateTime(LocalDateTime.now());
            service.saveRunningData(d);
            System.out.println(i + " >>>> done");
        }
    }

    public Desk getRunningData(Long id) {
        String key = id + "_running_data";
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = DeskKvDatabase.getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
        Desk desk = null;
        if (status == OperationStatus.SUCCESS) {
            try {
                desk = JSON.parseObject(new String(theData.getData()), Desk.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        txn.commit();
        return desk;
    }

    public List<Desk> listAllRunningData() {
        List<Desk> desks = new ArrayList<>();
        try {
            Database db = DeskKvDatabase.getDB();
            TransactionConfig txConfig = new TransactionConfig();
            txConfig.setSerializableIsolation(true);
            Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
            CursorConfig cc = new CursorConfig();
            cc.setReadCommitted(true);
            Cursor cursor = db.openCursor(txn, cc);
            DatabaseEntry theKey = new DatabaseEntry();
            DatabaseEntry theData = new DatabaseEntry();
            while (cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                if (theData.getData() != null) {
                    desks.add(JSON.parseObject(new String(theData.getData()), Desk.class));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }
}
