package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.DishesGroup;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesGroupDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public DishesGroup selectByDishesGroupId(Integer dishesGroupId) throws SQLException {
        DishesGroup cond = new DishesGroup();
        cond.setDishesGroupId(dishesGroupId);
        List<DishesGroup> groups = selectList(cond);
        if (CommonUtils.isEmpty(groups)) {
            return null;
        }
        return groups.get(0);
    }

    public List<DishesGroup> selectList(DishesGroup cond) throws SQLException {
        List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
        return EntityUtils.convertList(list, DishesGroup.class);
    }

}
