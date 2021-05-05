package com.xjh.service;

import com.google.inject.AbstractModule;
import com.xjh.service.domain.AdminService;
import com.xjh.service.domain.AnalysisService;
import com.xjh.service.domain.BookService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.ReaderService;
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
        bind(OrderService.class);
        bind(OrderDishesService.class);
        System.out.println("ServiceModule 耗时:" + (System.currentTimeMillis() - start));
    }
}
