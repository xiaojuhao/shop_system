package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.BillListNightDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BillListNightDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<BillListNightDO> selectList(BillListNightDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, BillListNightDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BillListNightDO insert(BillListNightDO dd) throws SQLException {
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(dd));
        dd.setId(id.intValue());
        return dd;
    }

    public int insertList(List<BillListNightDO> list) throws SQLException {
        for (BillListNightDO dd : list) {
            insert(dd);
        }
        return 1;
    }
}
