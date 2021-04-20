package com.xjh.service;

import com.google.inject.AbstractModule;
import com.xjh.service.domain.*;
import com.xjh.service.domain.impl.AdminServiceImpl;
import com.xjh.service.domain.impl.AnalysisServiceImpl;
import com.xjh.service.domain.impl.BookServiceImpl;
import com.xjh.service.domain.impl.ReaderServiceImpl;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        long start = System.currentTimeMillis();
        bind(AdminService.class).to(AdminServiceImpl.class);
        bind(AnalysisService.class).to(AnalysisServiceImpl.class);
        bind(BookService.class).to(BookServiceImpl.class);
        bind(ReaderService.class).to(ReaderServiceImpl.class);
        bind(DeskService.class);
        System.out.println("ServiceModule 耗时:" + (System.currentTimeMillis() - start));
    }
}
