package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesAttribute;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class DishesAttributeDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DishesAttribute> selectList(DishesAttribute cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesAttribute.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DishesAttribute selectById(Integer dishesAttributeId) {
        try {
            if (dishesAttributeId == null) {
                return null;
            }
            DishesAttribute cond = new DishesAttribute();
            cond.setDishesAttributeId(dishesAttributeId);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return Objects.requireNonNull(EntityUtils.convertList(list, DishesAttribute.class)).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Result<Integer> updateById(DishesAttribute attr) {
        try {
            if (attr.getDishesAttributeId() == null) {
                return Result.fail("主键ID为空");
            }
            int i = Db.use(ds).update(
                    EntityUtils.create(attr),
                    EntityUtils.idCond(attr)
            );
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新异常:" + ex.getMessage());
        }
    }

    public Result<Integer> insert(DishesAttribute dd) {
        try {
            int i = Db.use(ds).insert(EntityUtils.create(dd));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存数据错误");
        }
    }

    public Result<Integer> deleteById(DishesAttribute dd) {
        try {
            int i = Db.use(ds).del(EntityUtils.idCond(dd));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存数据错误");
        }
    }


}
