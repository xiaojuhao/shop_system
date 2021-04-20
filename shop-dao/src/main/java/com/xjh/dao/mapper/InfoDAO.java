package com.xjh.dao.mapper;

import com.xjh.dao.dataobject.Info;

import java.sql.SQLException;
import java.util.List;

public interface InfoDAO {
    int insert(Info info) throws SQLException;

    List<Info> select(Info info) throws SQLException;
}
