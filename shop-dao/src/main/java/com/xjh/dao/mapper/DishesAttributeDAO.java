package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
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


}
