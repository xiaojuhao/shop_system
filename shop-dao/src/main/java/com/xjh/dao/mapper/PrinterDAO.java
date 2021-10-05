package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.Printer;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class PrinterDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int updateById(Printer printer) {
        try {
            return Db.use(ds).update(EntityUtils.create(printer),
                    EntityUtils.idCond(printer));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int insert(Printer printer) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(printer));
    }

    public List<Printer> selectList(Printer cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Printer.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
