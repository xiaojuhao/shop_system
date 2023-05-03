package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.BillListNoonDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class BillListNoonDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;


    public List<BillListNoonDO> selectList(Date start, Date end) {
        try {
            String table = EntityUtils.tableName(BillListNoonDO.class);
            String sql = "select * from " + table
                    + " where dateTime >= " + start.getTime()
                    + "   and dateTime <= " + end.getTime();
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, BillListNoonDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<BillListNoonDO> selectList(BillListNoonDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, BillListNoonDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BillListNoonDO save(BillListNoonDO dd) throws SQLException {
        String tableName = EntityUtils.tableName(dd.getClass());
        if (dd.getDateTime() <= 0) {
            throw new RuntimeException(tableName + " dateTime值错误");
        }
        String sql = "delete from " + tableName + " where dateTime = " + dd.getDateTime();
        Db.use(ds).execute(sql);

        return insert(dd);
    }

    public BillListNoonDO insert(BillListNoonDO dd) throws SQLException {
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(dd));
        dd.setId(id.intValue());
        return dd;
    }

    public int insertList(List<BillListNoonDO> billLists) throws SQLException {
        for (BillListNoonDO dd : billLists) {
            insert(dd);
        }
        return 1;
    }
}
