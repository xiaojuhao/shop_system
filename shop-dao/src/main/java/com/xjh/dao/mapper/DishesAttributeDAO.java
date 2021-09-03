package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesAttribute;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

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


}
