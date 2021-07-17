package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesPackageTypeDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DishesPackageType> getByDishesPackageId(Integer dishesPackageId) {
        if (dishesPackageId == null) {
            return new ArrayList<>();
        }
        try {
            DishesPackageType cond = new DishesPackageType();
            cond.setDishesPackageId(dishesPackageId);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackageType.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
