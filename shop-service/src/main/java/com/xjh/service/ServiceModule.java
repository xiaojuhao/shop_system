package com.xjh.service;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.service.domain.AccountService;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(DeskService.class);
        bind(OrderService.class);
        bind(OrderDishesService.class);
        bind(CartService.class);
        bind(DishesService.class);
        bind(DishesPackageService.class);
        bind(AccountService.class);
        Logger.info("ServiceModule 耗时:" + timeRecord.getCost());
    }
}
