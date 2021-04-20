package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.xjh.dao.mapper.*;
import com.xjh.dao.mapper.impl.*;


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
        System.out.println("DaoModule 耗时: " + (System.currentTimeMillis() - start));
    }
}
