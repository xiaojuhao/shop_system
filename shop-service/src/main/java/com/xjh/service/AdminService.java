package com.xjh.service;

import java.util.List;

import com.xjh.dao.dataobject.Admin;

public interface AdminService {
    /**
     * 查询所有管理员
     *
     * @return List<Admin>
     */
    List<Admin> getAllAdmins();


    /**
     * 管理员登录
     *
     * @param account
     * @param password
     * @return boolean
     */
    boolean login(String account, String password);

    /**
     * 根据账号查询管理员信息
     *
     * @param account
     * @return amdin
     */
    Admin getAdminByAccount(String account);

    void updateAdmin(Admin admin);
}
