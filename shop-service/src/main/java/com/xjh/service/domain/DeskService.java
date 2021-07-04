package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.InfoDAO;
import com.xjh.service.domain.model.CreateOrderParam;
import com.xjh.service.domain.model.OpenDeskParam;

@Singleton
public class DeskService {
    @Inject
    OrderService orderService;
    @Inject
    DeskDAO deskDAO;
    @Inject
    InfoDAO infoDAO;

    public Desk getById(Integer id) {
        try {
            return deskDAO.getById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void openDesk(OpenDeskParam param) {
        try {
            int deskId = param.getDeskId();
            Desk desk = deskDAO.getById(deskId);
            if (desk == null) {
                AlertBuilder.ERROR("开桌失败", "桌号不存在:" + deskId);
                return;
            }
            // 开桌
            Integer orderId = orderService.createNewOrderId();
            desk.setOrderId(orderId);
            deskDAO.useDesk(desk);
            // 下单
            CreateOrderParam createOrderParam = new CreateOrderParam();
            createOrderParam.setOrderId(orderId);
            createOrderParam.setDeskId(deskId);
            createOrderParam.setCustomerNum(param.getCustomerNum());
            Order order = orderService.createOrder(createOrderParam);
            LogUtils.info("下单成功: " + JSON.toJSONString(order));
        } catch (Exception ex) {
            LogUtils.info("开桌失败" + ex.getMessage());
            AlertBuilder.ERROR("下单失败", "开桌失败:" + ex.getMessage());
        }
    }

    public void closeDesk(Integer deskId) {
        try {
            deskDAO.freeDesk(deskId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Desk> getAllDesks() {

        List<Desk> desks = new ArrayList<>();
        try {
            //            infoDAO.select(new Info()).forEach(info -> {
            //                LogUtils.info("读取INFO信息:" + JSON.toJSONString(info));
            //            });
            desks.addAll(deskDAO.select(new Desk()));
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
