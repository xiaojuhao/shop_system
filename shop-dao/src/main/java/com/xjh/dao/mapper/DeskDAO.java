package com.xjh.dao.mapper;

import com.xjh.dao.dataobject.Desk;

import java.sql.SQLException;
import java.util.List;

public interface DeskDAO {
    int insert(Desk desk) throws SQLException;

    List<Desk> select(Desk desk) throws SQLException;

    Desk getById(Long id) throws SQLException;

    int placeOrder(Desk desk) throws SQLException;

    int clearOrder(Long id) throws SQLException;
}
