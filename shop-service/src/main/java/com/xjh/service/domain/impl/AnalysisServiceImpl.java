package com.xjh.service.domain.impl;

import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.mapper.AdminDAO;
import com.xjh.dao.mapper.BookDAO;
import com.xjh.dao.mapper.ReaderDAO;
import com.xjh.dao.mapper.TypeDAO;
import com.xjh.service.domain.AnalysisService;

@Singleton
public class AnalysisServiceImpl implements AnalysisService {
    @Inject
    TypeDAO typeDAO;
    @Inject
    BookDAO bookDAO;
    @Inject
    ReaderDAO readerDAO;
    @Inject
    AdminDAO adminDAO;

    @Override
    public int getTypesCount() {
        int n = 0;
        try {
            n = typeDAO.countTypes();
        } catch (SQLException e) {
            System.err.println("统计类别总数出现异常");
        }
        return n;
    }

    @Override
    public int getBooksCount() {
        int n = 0;
        try {
            n = bookDAO.countBooks();
        } catch (SQLException e) {
            System.err.println("统计图书总数出现异常");
        }
        return n;
    }

    @Override
    public int getReadersCount() {
        int n = 0;
        try {
            n = readerDAO.countReaders();
        } catch (SQLException e) {
            System.err.println("统计读者总数出现异常");
        }
        return n;
    }

    @Override
    public int getAdminsCount() {
        int n = 0;
        try {
            n = adminDAO.countAdmins();
        } catch (SQLException e) {
            System.err.println("统计管理员总数出现异常");
        }
        return n;
    }
}
