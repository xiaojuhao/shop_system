package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.dao.mapper.AdminDAO;
import com.xjh.dao.mapper.BookDAO;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.InfoDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.ReaderDAO;
import com.xjh.dao.mapper.TypeDAO;
import com.xjh.dao.mapper.impl.AdminDAOImpl;
import com.xjh.dao.mapper.impl.BookDAOImpl;
import com.xjh.dao.mapper.impl.DeskDAOImpl;
import com.xjh.dao.mapper.impl.InfoDAOImpl;
import com.xjh.dao.mapper.impl.OrderDAOImpl;
import com.xjh.dao.mapper.impl.OrderDishesDAOImpl;
import com.xjh.dao.mapper.impl.ReaderDAOImpl;
import com.xjh.dao.mapper.impl.TypeDAOImpl;


public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        long start = System.currentTimeMillis();
        bind(AdminDAO.class).to(AdminDAOImpl.class);
        bind(BookDAO.class).to(BookDAOImpl.class);
        bind(DeskDAO.class).to(DeskDAOImpl.class);
        bind(ReaderDAO.class).to(ReaderDAOImpl.class);
        bind(TypeDAO.class).to(TypeDAOImpl.class);
        bind(InfoDAO.class).to(InfoDAOImpl.class);
        bind(OrderDAO.class).to(OrderDAOImpl.class);
        bind(OrderDishesDAO.class).to(OrderDishesDAOImpl.class);
        System.out.println("DaoModule 耗时: " + (System.currentTimeMillis() - start));
    }
}
