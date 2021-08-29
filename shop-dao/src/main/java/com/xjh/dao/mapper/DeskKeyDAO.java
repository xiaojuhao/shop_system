package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DeskKey;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DeskKeyDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DeskKey> selectList(DeskKey cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DeskKey.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DeskKey getByDeskId(Integer deskId) {
        if (deskId == null) {
            return null;
        }
        DeskKey cond = new DeskKey();
        cond.setDeskId(deskId);
        return selectList(cond).stream().findFirst().orElse(null);
    }
}
