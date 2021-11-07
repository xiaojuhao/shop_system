package com.xjh.service;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        //        bind(DeskService.class);
        //        bind(OrderService.class);
        //        bind(OrderDishesService.class);
        //        bind(CartService.class);
        //        bind(DishesService.class);
        //        bind(DishesPackageService.class);
        //        bind(AccountService.class);
        //        bind(DishesAttributeService.class);
        //        bind(PrinterService.class);
        //        bind(SubOrderService.class);
        Logger.info("ServiceModule 耗时:" + timeRecord.getCost());
    }
}
