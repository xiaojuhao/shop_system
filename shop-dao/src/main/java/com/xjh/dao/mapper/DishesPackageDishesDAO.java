package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DishesPackageDishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> insert(DishesPackageDishes dd) {
        if (dd == null) {
            return Result.fail("入错错误");
        }
        try {
            int i = Db.use(ds).insert(EntityUtils.create(dd));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Integer> deleteByIds(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return Result.fail("入错错误");
        }
        int i = 0;
        for (Integer id : ids) {
            Result<Integer> delRs = deleteById(id);
            if (delRs.getData() != null) {
                i += delRs.getData();
            }
        }
        return Result.success(i);
    }

    public Result<Integer> deleteById(Integer id) {
        if (id == null) {
            return Result.fail("入错错误");
        }
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageDishesId(id);
        try {
            int i = Db.use(ds).del(EntityUtils.create(cond));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

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
    public List<DishesPackageDishes> getByDishesPackageId(Integer packageId) {
        if (packageId == null) {
            return new ArrayList<>();
        }
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageId(packageId);
        return selectList(cond);
    }

    public List<DishesPackageDishes> getByDishesPackageTypeId(Integer packageId, Integer dishesPackageTypeId) {
        if (dishesPackageTypeId == null) {
            return new ArrayList<>();
        }
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageId(packageId);
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
