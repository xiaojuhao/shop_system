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
        BillListDO cond = new BillListDO();
        cond.setDateTime(maxDateTime);
        return CommonUtils.firstOf(selectList(cond));
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

    public BillListDO insert(BillListDO dd) throws SQLException {
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(dd));
        dd.setId(id.intValue());
        return dd;
    }

    public int insertList(List<BillListDO> list) throws SQLException {
        for (BillListDO dd : list) {
            insert(dd);
        }
        return 1;
    }
}
