package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.BillListSupperDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BillListSupperDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<BillListSupperDO> selectList(BillListSupperDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, BillListSupperDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BillListSupperDO insert(BillListSupperDO dd) throws SQLException {
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(dd));
        dd.setId(id.intValue());
        return dd;
    }

    public int insertList(List<BillListSupperDO> billLists) throws SQLException {
        for (BillListSupperDO dd : billLists) {
            insert(dd);
        }
        return 1;
    }
}
