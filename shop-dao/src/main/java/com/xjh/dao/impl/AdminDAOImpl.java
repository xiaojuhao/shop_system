package com.xjh.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.xjh.dao.AdminDAO;
import com.xjh.dao.dataobject.Admin;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;


public class AdminDAOImpl implements AdminDAO {
    @Inject
    HikariDataSource ds;

    @Override
    public List<Admin> selectAdmins() throws SQLException {
        List<Entity> entityList = Db.use(ds).query("SELECT * FROM t_admin ");
        List<Admin> adminList = new ArrayList<>();
        for (Entity entity : entityList) {
            adminList.add(convertAdmin(entity));
        }
        return adminList;
    }

    @Override
    public Admin getAdminByAccount(String account) throws SQLException {
        Entity entity = Db.use(ds).queryOne(
                "SELECT * FROM t_admin WHERE account = ? ", account);
        return convertAdmin(entity);
    }

    @Override
    public int countAdmins() throws SQLException {
        return Db.use(ds).queryNumber("SELECT COUNT(*) FROM t_admin ").intValue();
    }

    @Override
    public int updateAdmin(Admin admin) throws SQLException {
        return Db.use(ds).update(
                Entity.create().set("password", admin.getPassword()),
                Entity.create("t_admin").set("id", admin.getId())
        );
    }

    /**
     * 封装一个将Entity转换为Admin的方法
     *
     * @param entity
     * @return
     */
    private Admin convertAdmin(Entity entity) {
        if (entity == null) {
            return null;
        }
        Admin admin = new Admin();
        admin.setId(entity.getLong("id"));
        admin.setAccount(entity.getStr("account"));
        admin.setPassword(entity.getStr("password"));
        admin.setName(entity.getStr("name"));
        admin.setAvatar(entity.getStr("avatar"));
        return admin;
    }
}
