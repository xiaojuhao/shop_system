package com.xjh.service.jobs;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.Executors;

@Singleton
public class SchedJobService {
    @Inject
    BillListJob billListJob;

    public void startAllJobs() {
        Executors.newSingleThreadExecutor().submit(() -> {
            billListJob.startJob();
        });
    }
}
