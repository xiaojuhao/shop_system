package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesPackageUpdate;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DishesPackageUpdateDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;


    public List<DishesPackageUpdate> selectList(DishesPackageUpdate cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackageUpdate.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DishesPackageUpdate getByDishesPackageId(Integer dishesPackageId) {
        try {
            if (dishesPackageId == null) {
                return null;
            }
            DishesPackageUpdate cond = new DishesPackageUpdate();
            cond.setDishesPackageId(dishesPackageId);
            return selectList(cond).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public long getLastUpdateTime(Integer dishesPackageId) {
        DishesPackageUpdate u = getByDishesPackageId(dishesPackageId);
        if (u != null) {
            return u.getLastUpdateTime();
        } else {
            return 0;
        }
    }
}
