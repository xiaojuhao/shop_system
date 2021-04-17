package com.xjh.service;

import com.google.inject.AbstractModule;
import com.xjh.service.impl.AdminServiceImpl;
import com.xjh.service.impl.AnalysisServiceImpl;
import com.xjh.service.impl.BookServiceImpl;
import com.xjh.service.impl.ReaderServiceImpl;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AdminService.class).to(AdminServiceImpl.class);
        bind(AnalysisService.class).to(AnalysisServiceImpl.class);
        bind(BookService.class).to(BookServiceImpl.class);
        bind(ReaderService.class).to(ReaderServiceImpl.class);
        bind(DeskService.class);
    }
}
