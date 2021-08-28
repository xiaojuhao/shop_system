package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesUpdate;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesUpdateDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DishesUpdate> selectList(DishesUpdate cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesUpdate.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DishesUpdate getByDishesId(Integer dishesId) {
        try {
            if (dishesId == null) {
                return null;
            }
            DishesUpdate cond = new DishesUpdate();
            cond.setDishesId(dishesId);
            return selectList(cond).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public long getDishesUpdateLastUpdateTime(Integer dishesId) {
        DishesUpdate u = getByDishesId(dishesId);
        if (u != null) {
            return u.getLastUpdateTime();
        } else {
            return 0;
        }
    }
}
