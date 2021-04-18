package com.xjh.service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;

@Singleton
public class DeskService {
    AtomicBoolean randomUpdateStarted = new AtomicBoolean();

    public void randomUpdate() {
        if (!randomUpdateStarted.compareAndSet(false, true)) {
            return;
        }
        Random random = new Random();
        int toalSize = getAllDesks().size();
        ThreadUtils.runInDaemon(() -> {
            while (true) {
                CommonUtils.sleep(3000);
                long deskId = random.nextInt(toalSize);
                Desk desk = this.getRunningData(deskId);
                if (desk != null) {
                    desk.setVerNo(desk.getVerNo() != null ? desk.getVerNo() + 1 : 1);
                    if (EnumDesKStatus.of(desk.getStatus()) == EnumDesKStatus.USED) {
                        desk.setStatus(EnumDesKStatus.FREE.status());
                        desk.setOrderCreateTime(null);
                    } else {
                        desk.setOrderCreateTime(LocalDateTime.now());
                        desk.setStatus(EnumDesKStatus.USED.status());
                    }
                    this.saveRunningData(desk);
                } else {
                    desk = new Desk();
                    desk.setId(deskId);
                    desk.setDeskName(deskId + "");
                    desk.setStatus(EnumDesKStatus.FREE.status());
                    this.saveRunningData(desk);
                }
            }
        });
    }

    public List<Desk> getAllDesks() {
        randomUpdate();
        List<Desk> desks = new ArrayList<>();
        try {
            URL url = DeskService.class.getResource("/config/desks.json");
            String data = CommonUtils.readFile(url.getFile());
            JSONArray json = JSON.parseArray(data);
            for (int i = 0; i < json.size(); i++) {
                JSONObject v = json.getJSONObject(i);
                Desk desk = new Desk();
                desk.setId(v.getLong("id"));
                desk.setDeskName(v.getString("name"));
                desks.add(desk);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }

    public void saveRunningData(Desk desk) {
        String key = desk.getId() + "_running_data";
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
