package com.xjh.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.DeskDAO;
import com.xjh.dao.InfoDAO;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Info;

@Singleton
public class DeskService {
    @Inject
    DeskDAO deskDAO;
    @Inject
    InfoDAO infoDAO;

    public Desk getById(Long id) {
        try {
            return deskDAO.getById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void openDesk(Long id) {
        try {
            Desk desk = deskDAO.getById(id);
            if (desk == null) {
                return;
            }
            desk.setOrderId("ORD" + System.currentTimeMillis());
            desk.setOrderCreateTime(LocalDateTime.now());
            deskDAO.placeOrder(desk);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeDesk(Long id) {
        try {
            deskDAO.clearOrder(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Desk> getAllDesks() {

        List<Desk> desks = new ArrayList<>();
        try {
            infoDAO.select(new Info()).forEach(info -> {
                System.out.println("读取INFO信息:" + JSON.toJSONString(info));
            });
            return deskDAO.select(new Desk());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }

    //    public void saveRunningData(Desk desk) {
    //        String key = desk.getId() + "_running_data";
    //        desk.setVerNo(desk.getVerNo() != null ? desk.getVerNo() + 1 : 1);
    //        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
    //        // DatabaseEntry theData = new DatabaseEntry(HessianUtils.serialize(desk));
    //        DatabaseEntry theData = new DatabaseEntry(JSON.toJSONString(desk).getBytes(StandardCharsets.UTF_8));
    //        TransactionConfig txConfig = new TransactionConfig();
    //        txConfig.setSerializableIsolation(true);
    //        Database db = DeskKvDatabase.getDB();
    //        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
    //        db.put(txn, theKey, theData);
    //        txn.commit();
    //    }

    //    public static void main(String[] args) {
    //        DeskService service = new DeskService();
    //        for (long i = 1; i < 10000; i++) {
    //            Desk d = service.getRunningData(i % 10);
    //            if (d == null) {
    //                continue;
    //            }
    //            d.setVerNo(d.getVerNo() != null ? d.getVerNo() + 1 : 1);
    //            d.setOrderCreateTime(LocalDateTime.now());
    //            service.saveRunningData(d);
    //            System.out.println(i + " >>>> done");
    //        }
    //    }

    //    public Desk getRunningData(Long id) {
    //        String key = id + "_running_data";
    //        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
    //        DatabaseEntry theData = new DatabaseEntry();
    //        TransactionConfig txConfig = new TransactionConfig();
    //        txConfig.setSerializableIsolation(true);
    //        Database db = DeskKvDatabase.getDB();
    //        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
    //        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
    //        Desk desk = null;
    //        if (status == OperationStatus.SUCCESS) {
    //            try {
    //                desk = JSON.parseObject(new String(theData.getData()), Desk.class);
    //            } catch (Exception ex) {
    //                ex.printStackTrace();
    //            }
    //        }
    //        txn.commit();
    //        return desk;
    //    }

    //    public List<Desk> listAllRunningData() {
    //        List<Desk> desks = new ArrayList<>();
    //        try {
    //            Database db = DeskKvDatabase.getDB();
    //            TransactionConfig txConfig = new TransactionConfig();
    //            txConfig.setSerializableIsolation(true);
    //            Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
    //            CursorConfig cc = new CursorConfig();
    //            cc.setReadCommitted(true);
    //            Cursor cursor = db.openCursor(txn, cc);
    //            DatabaseEntry theKey = new DatabaseEntry();
    //            DatabaseEntry theData = new DatabaseEntry();
    //            while (cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
    //                if (theData.getData() != null) {
    //                    desks.add(JSON.parseObject(new String(theData.getData()), Desk.class));
    //                }
    //            }
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //        }
    //        return desks;
    //    }
}
