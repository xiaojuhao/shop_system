package com.xjh.dao.mapper;

import com.xjh.dao.dataobject.Admin;

import java.sql.SQLException;
import java.util.List;


/**
 * 管理员DAO接口
 */
public interface AdminDAO {
    /**
     * 查询所有管理员
     *
     * @return List<Admin>
     * @throws SQLException
     */
    List<Admin> selectAdmins() throws SQLException;

    /**
     * 根据账号查询管理员
     *
     * @param account
     * @return Entity
     * @throws SQLException
     */
    Admin getAdminByAccount(String account) throws SQLException;

    /**
     * 统计管理员总数
     *
     * @return
     * @throws SQLException
     */
    int countAdmins() throws SQLException;

    int updateAdmin(Admin admin) throws SQLException;
}
