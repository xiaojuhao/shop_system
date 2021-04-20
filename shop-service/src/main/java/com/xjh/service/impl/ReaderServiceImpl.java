package com.xjh.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.dataobject.Reader;
import com.xjh.dao.mapper.ReaderDAO;
import com.xjh.service.ReaderService;

@Singleton
public class ReaderServiceImpl implements ReaderService {
    @Inject
    private ReaderDAO readerDAO;

    @Override
    public List<Reader> getAllReaders() {
        List<Reader> readerList = new ArrayList<>();
        try {
            readerList = readerDAO.selectReaders();
        } catch (SQLException e) {
            System.err.println("查询所有读者信息出现异常");
        }
        return readerList;
    }

    @Override
    public void deleteReader(long id) {
        try {
            readerDAO.deleteById(id);
        } catch (SQLException e) {
            System.err.println("删除读者信息出现异常");
        }
    }

    @Override
    public Long addReader(Reader reader) {
        long result = 0;
        try {
            result = readerDAO.insertReader(reader);
        } catch (SQLException e) {
            System.err.println("新增读者信息出现异常");
        }
        return result;
    }

    @Override
    public int countByRole(String role) {
        int result = 0;
        try {
            result = readerDAO.countByRole(role);
        } catch (SQLException e) {
            System.err.println("根据角色统计读者信息出现异常");
        }
        return result;
    }

    @Override
    public int countByDepartment(String department) {
        int result = 0;
        try {
            result = readerDAO.countByDepartment(department);
        } catch (SQLException e) {
            System.err.println("根据部门统计读者信息出现异常");
        }
        return result;
    }
}
