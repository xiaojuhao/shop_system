package com.xjh.service.domain;

import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.dao.mapper.SubOrderDAO;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;

@Singleton
public class SubOrderService {
    @Inject
    SubOrderDAO subOrderDAO;
    @Deprecated
    public Integer createSubOrderId() {
        while (true) {
            Integer nextId = createSubOrderId1();
            if (subOrderDAO.findBySubOrderId(nextId) == null) {
                return nextId;
            }
        }
    }
    @Deprecated
    public Integer createSubOrderId1() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        String timeStr = DateBuilder.today().format("yyyyMMddHH");
        int diffHours = (int) DateBuilder.diffHours(start, DateBuilder.base(timeStr).dateTime());
        if (diffHours <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + timeStr);
        }
        int nextId = nextId(timeStr);
        if (nextId >= 2 << 15) {
            throw new RuntimeException("循环次数已用完:" + timeStr);
        }
        // 前17位保存时间，后15位保存序列号
        int id = (diffHours << 15) | nextId;
        Logger.info("创建子订单号: " + diffHours + "," + nextId + "," + id);
        return id;
    }
    @Deprecated
    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("subOrderId:sequence:" + group);
    }

}
