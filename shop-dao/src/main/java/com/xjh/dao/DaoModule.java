package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.mapper.AccountDAO;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.DishesAttributeDAO;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesGroupDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.dao.mapper.DishesPackageUpdateDAO;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.dao.mapper.DishesTypeDAO;
import com.xjh.dao.mapper.DishesTypeUpdateDAO;
import com.xjh.dao.mapper.DishesUpdateDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.dao.mapper.StoreDAO;
import com.xjh.dao.mapper.SubOrderDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(DeskDAO.class);
        bind(OrderDAO.class);
        bind(OrderDishesDAO.class);
        bind(DishesPackageDAO.class);
        bind(DishesDAO.class);
        bind(SubOrderDAO.class);
        bind(DishesTypeDAO.class);
        bind(OrderPayDAO.class);
        bind(DishesPackageTypeDAO.class);
        bind(DishesPackageDishesDAO.class);
        bind(StoreDAO.class);
        bind(DishesGroupDAO.class);
        bind(AccountDAO.class);
        bind(DishesPriceDAO.class);
        bind(DishesAttributeDAO.class);
        bind(DishesTypeUpdateDAO.class);
        bind(DishesUpdateDAO.class);
        bind(DishesPackageUpdateDAO.class);
        Logger.info("DaoModule 耗时: " + timeRecord.getCost());
    }
}
