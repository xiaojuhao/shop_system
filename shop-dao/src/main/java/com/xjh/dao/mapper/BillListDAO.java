package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.BillListDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class BillListDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public BillListDO newestDO() throws SQLException {
        String sql = "select max(dateTime) as maxDateTime from bill_list";
        List<Entity> list = Db.use(ds).query(sql);
        if (CommonUtils.isEmpty(list)) {
            return null;
        }
        Long maxDateTime = list.get(0).getLong("maxDateTime");
        if(maxDateTime == null){
            return null;
        }
        BillListDO cond = new BillListDO();
        cond.setDateTime(maxDateTime);
        return CommonUtils.firstOf(selectList(cond));
    }

    public List<BillListDO> selectList(Date start, Date end) {
        try {
            String table = EntityUtils.tableName(BillListDO.class);
            String sql = "select * from " + table
                    + " where dateTime >= " + start.getTime()
                    + "   and dateTime <= " + end.getTime();
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, BillListDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<BillListDO> selectList(BillListDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, BillListDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BillListDO save(BillListDO dd) throws SQLException {
        String tableName = EntityUtils.tableName(dd.getClass());
        if (dd.getDateTime() <= 0) {
            throw new RuntimeException(tableName + " dateTime值错误");
        }
        String sql = "delete from " + tableName + " where dateTime = " + dd.getDateTime();
        Db.use(ds).execute(sql);

        return insert(dd);
    }

    public BillListDO insert(BillListDO dd) throws SQLException {
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(dd));
        dd.setId(id.intValue());
        return dd;
    }
}
