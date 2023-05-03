package com.xjh.service.jobs;

import com.google.inject.Singleton;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.DateRange;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.BillListDO;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.*;
import com.xjh.service.domain.BillListService;

import javax.inject.Inject;
import java.util.Date;

@Singleton
public class BillListJob {
    @Inject
    BillListDAO billListDAO;
    @Inject
    BillListNoonDAO billListNoonDAO;
    @Inject
    BillListNightDAO billListNightDAO;
    @Inject
    BillListSupperDAO billListSupperDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    BillListService billListService;


    public void startJob() {
        try {
            doJob();
        } catch (Exception ex) {
            Logger.error("BillListJob >> " + ex.getMessage());
        }
    }

    public void doJob() throws Exception {
        Date start = DateBuilder.today().date();
        BillListDO newest = billListDAO.newestDO();
        if (newest != null) {
            start = new Date(newest.getDateTime());
        } else {
            Order order = orderDAO.firstOrderOf(null);
            if (order != null) {
                start = new Date(order.getCreateTime());
            }
        }
        start = DateBuilder.base(start).zeroAM().date();
        DateRange dateRange = DateRange.of(start, DateBuilder.yestoday().date());
        System.out.println("统计区间: " + dateRange);
        while (dateRange.hasNext()) {
            billListService.doStatistics(dateRange.nextDay(), billListService::saveBill);
        }
    }


}
