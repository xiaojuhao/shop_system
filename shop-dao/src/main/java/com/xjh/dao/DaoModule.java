package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        //        bind(DeskDAO.class);
        //        bind(OrderDAO.class);
        //        bind(OrderDishesDAO.class);
        //        bind(DishesPackageDAO.class);
        //        bind(DishesDAO.class);
        //        bind(SubOrderDAO.class);
        //        bind(DishesTypeDAO.class);
        //        bind(OrderPayDAO.class);
        //        bind(DishesPackageTypeDAO.class);
        //        bind(DishesPackageDishesDAO.class);
        //        bind(StoreDAO.class);
        //        bind(DishesGroupDAO.class);
        //        bind(AccountDAO.class);
        //        bind(DishesPriceDAO.class);
        //        bind(DishesAttributeDAO.class);
        //        bind(DishesTypeUpdateDAO.class);
        //        bind(DishesUpdateDAO.class);
        //        bind(DishesPackageUpdateDAO.class);
        //        bind(PrinterDAO.class);
        //        bind(PrinterTaskDAO.class);

        //        Set<Class<?>> classes = ClasspathPackageScanner.getClasses("com.xjh.dao.mapper");
        //        classes.forEach(c -> {
        //            if (c.getName().endsWith("DAO")) {
        //                bind(c);
        //                Logger.info("::: 绑定DAO >> " + c.getName());
        //            }
        //        });
        Logger.info("DaoModule 耗时: " + timeRecord.getCost());
    }
}
