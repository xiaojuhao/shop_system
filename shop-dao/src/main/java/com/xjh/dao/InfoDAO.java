package com.xjh.dao;

import java.sql.SQLException;
import java.util.List;

import com.xjh.dao.dataobject.Info;

public interface InfoDAO {
    int insert(Info info) throws SQLException;

    List<Info> select(Info info) throws SQLException;
}
