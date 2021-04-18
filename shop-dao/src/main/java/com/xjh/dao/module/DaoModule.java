package com.xjh.dao.module;

import com.google.inject.AbstractModule;
import com.xjh.dao.AdminDAO;
import com.xjh.dao.BookDAO;
import com.xjh.dao.DeskDAO;
import com.xjh.dao.ReaderDAO;
import com.xjh.dao.TypeDAO;
import com.xjh.dao.impl.AdminDAOImpl;
import com.xjh.dao.impl.BookDAOImpl;
import com.xjh.dao.impl.DeskDAOImpl;
import com.xjh.dao.impl.ReaderDAOImpl;
import com.xjh.dao.impl.TypeDAOImpl;


public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        long start = System.currentTimeMillis();
        bind(AdminDAO.class).to(AdminDAOImpl.class);
        bind(BookDAO.class).to(BookDAOImpl.class);
        bind(DeskDAO.class).to(DeskDAOImpl.class);
        bind(ReaderDAO.class).to(ReaderDAOImpl.class);
        bind(TypeDAO.class).to(TypeDAOImpl.class);
        System.out.println("DaoModule 耗时: " + (System.currentTimeMillis() - start));
    }
}
