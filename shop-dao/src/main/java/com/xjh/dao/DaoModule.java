package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.mapper.CartDAO;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesGroupDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.dao.mapper.DishesTypeDAO;
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
        bind(CartDAO.class);
        bind(SubOrderDAO.class);
        bind(DishesTypeDAO.class);
        bind(OrderPayDAO.class);
        bind(DishesPackageTypeDAO.class);
        bind(DishesPackageDishesDAO.class);
        bind(StoreDAO.class);
        bind(DishesGroupDAO.class);
        LogUtils.info("DaoModule 耗时: " + timeRecord.getCost());
    }
}
