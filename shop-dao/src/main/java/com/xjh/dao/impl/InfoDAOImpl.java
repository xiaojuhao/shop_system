package com.xjh.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.InfoDAO;
import com.xjh.dao.dataobject.Info;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class InfoDAOImpl implements InfoDAO {
    @Inject
    @Named("sqlite")
    HikariDataSource ds;

    @Override
    public int insert(Info info) throws SQLException {
        Entity t = Entity.create("t_desk")
                .set("id", info.getId())
                .set("info_name", info.getInfoName())
                .set("info_val", info.getInfoVal());
        return Db.use(ds).insert(t);
    }

    @Override
    public List<Info> select(Info info) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from t_info where 1 = 1 ");
        List<Entity> list = Db.use(ds).query(sql.toString());
        return CommonUtils.collect(list, this::convert);
    }

    private Info convert(Entity entity) {
        Info info = new Info();
        info.setId(entity.getLong("id"));
        info.setInfoName(entity.getStr("info_name"));
        info.setInfoVal(entity.getStr("info_val"));
        return info;
    }
}
