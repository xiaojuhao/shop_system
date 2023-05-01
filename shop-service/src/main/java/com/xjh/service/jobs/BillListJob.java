package com.xjh.service.jobs;

import com.google.inject.Singleton;
import com.xjh.common.utils.Logger;

@Singleton
public class BillListJob {

    public void startJob() {
        try {
            doJob();
        } catch (Exception ex) {
            Logger.error("BillListJob >> " + ex.getMessage());
        }
    }

    public void doJob() throws Exception {
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            System.out.println("BillListJob 。。。。。。");
        }
    }
}
