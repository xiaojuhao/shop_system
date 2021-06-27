package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.reqmodel.PageCond;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;

@Singleton
public class DishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Dishes getById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            Dishes cond = new Dishes();
            cond.setDishesId(id);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Dishes.class).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Dishes> selectList(Dishes cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Dishes> pageQuery(Dishes cond, PageCond page) {
        try {
            int pageNo = CommonUtils.orElse(page.getPageNo(), 1);
            int pageSize = CommonUtils.orElse(page.getPageSize(), 20);
            Page pageCond = new Page(pageNo, pageSize);
            List<Entity> list = Db.use(ds).pageForEntityList(EntityUtils.create(cond), pageCond);
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
