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
import java.util.Date;
import java.util.List;

@Singleton
public class BillListSupperDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<BillListSupperDO> selectList(Date start, Date end) {
        try {
            String table = EntityUtils.tableName(BillListSupperDO.class);
            String sql = "select * from " + table
                    + " where dateTime >= " + start.getTime()
                    + "   and dateTime <= " + end.getTime();
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, BillListSupperDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<BillListSupperDO> selectList(BillListSupperDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, BillListSupperDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BillListSupperDO save(BillListSupperDO dd) throws SQLException {
        String tableName = EntityUtils.tableName(dd.getClass());
        if (dd.getDateTime() <= 0) {
            throw new RuntimeException(tableName + " dateTime值错误");
        }
        String sql = "delete from " + tableName + " where dateTime = " + dd.getDateTime();
        Db.use(ds).execute(sql);

        return insert(dd);
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
