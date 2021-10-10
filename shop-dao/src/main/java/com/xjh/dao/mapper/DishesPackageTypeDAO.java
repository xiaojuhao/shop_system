package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
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

    public Result<Integer> insert(DishesPackageType item) {
        if (item == null) {
            return Result.fail("入参错误");
        }
        try {
            int i = Db.use(ds).insert(EntityUtils.create(item));
            if (i > 0) {
                return Result.success(i);
            } else {
                return Result.fail("更新失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Integer> updateById(DishesPackageType item) {
        if (item == null) {
            return Result.fail("入参错误");
        }
        if (item.getDishesPackageTypeId() == null) {
            return Result.fail("ID必输");
        }
        try {
            int i = Db.use(ds).update(EntityUtils.create(item),
                    EntityUtils.idCond(item));
            if (i > 0) {
                return Result.success(i);
            } else {
                return Result.fail("更新失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Integer> deleteById(Integer id) {
        if (id == null) {
            return Result.fail("入参错误");
        }
        try {
            DishesPackageType e = new DishesPackageType();
            e.setDishesPackageTypeId(id);
            int i = Db.use(ds).del(EntityUtils.create(e));
            if (i > 0) {
                return Result.success(i);
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public DishesPackageType getById(Integer dishesPackageTypeId) {
        if (dishesPackageTypeId == null) {
            return null;
        }
        try {
            DishesPackageType cond = new DishesPackageType();
            cond.setDishesPackageTypeId(dishesPackageTypeId);
            return selectList(cond).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<DishesPackageType> selectList(DishesPackageType cond) {
        if (cond == null) {
            return new ArrayList<>();
        }
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackageType.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

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
            return new ArrayList<>();
        }
    }
}
