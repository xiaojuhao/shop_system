package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.mapper.AdminDAO;
import com.xjh.dao.mapper.BookDAO;
import com.xjh.dao.mapper.CartDAO;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesTypeDAO;
import com.xjh.dao.mapper.InfoDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.dao.mapper.ReaderDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.dao.mapper.TypeDAO;
import com.xjh.dao.mapper.impl.AdminDAOImpl;
import com.xjh.dao.mapper.impl.BookDAOImpl;
import com.xjh.dao.mapper.impl.InfoDAOImpl;
import com.xjh.dao.mapper.impl.ReaderDAOImpl;
import com.xjh.dao.mapper.impl.TypeDAOImpl;


public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(AdminDAO.class).to(AdminDAOImpl.class);
        bind(BookDAO.class).to(BookDAOImpl.class);
        bind(ReaderDAO.class).to(ReaderDAOImpl.class);
        bind(TypeDAO.class).to(TypeDAOImpl.class);
        bind(InfoDAO.class).to(InfoDAOImpl.class);
        bind(DeskDAO.class);
        bind(OrderDAO.class);
        bind(OrderDishesDAO.class);
        bind(DishesPackageDAO.class);
        bind(DishesDAO.class);
        bind(CartDAO.class);
        bind(SubOrderDAO.class);
        bind(DishesTypeDAO.class);
        bind(OrderPayDAO.class);
        LogUtils.info("DaoModule 耗时: " + timeRecord.getCost());
    }
}
