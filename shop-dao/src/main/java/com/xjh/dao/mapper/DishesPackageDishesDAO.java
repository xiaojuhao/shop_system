package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesPackageDishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> deleteByDishesPackageTypeId(Integer dishesPackageTypeId) {
        if (dishesPackageTypeId == null) {
            return Result.fail("入错错误");
        }
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageTypeId(dishesPackageTypeId);
        try {
            int i = Db.use(ds).del(EntityUtils.create(cond));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public List<DishesPackageDishes> getByDishesPackageTypeId(Integer dishesPackageTypeId) {
        if (dishesPackageTypeId == null) {
            return new ArrayList<>();
        }
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageTypeId(dishesPackageTypeId);
        return selectList(cond);
    }

    public List<DishesPackageDishes> selectList(DishesPackageDishes cond) {
        if (cond == null) {
            return new ArrayList<>();
        }
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackageDishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
