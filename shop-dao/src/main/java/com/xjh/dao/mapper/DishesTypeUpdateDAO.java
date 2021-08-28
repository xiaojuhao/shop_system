package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesTypeUpdate;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesTypeUpdateDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DishesTypeUpdate> selectList(DishesTypeUpdate cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesTypeUpdate.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DishesTypeUpdate queryByTypeId(Integer dishesTypeId) {
        DishesTypeUpdate cond = new DishesTypeUpdate();
        cond.setDishesTypeId(dishesTypeId);
        return selectList(cond).stream().findFirst().orElse(null);
    }

    public long getDishesTypeLastUpdateTime(Integer dishesTypeId) {
        DishesTypeUpdate d = queryByTypeId(dishesTypeId);
        if (d != null) {
            return d.getLastUpdateTime();
        } else {
            return 0;
        }
    }
}
