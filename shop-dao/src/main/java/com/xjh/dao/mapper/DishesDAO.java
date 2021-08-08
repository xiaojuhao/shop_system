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

    public List<Dishes> getByIds(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        try {
            Entity cond = EntityUtils.pureCreate(Dishes.class);
            cond.set("dishesId", ids.toArray());
            List<Entity> list = Db.use(ds).findAll(cond);
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
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
            StringBuilder where = new StringBuilder();
            if (cond.getDishesId() != null) {
                where.append(" and dishesId = ").append(cond.getDishesId());
            }
            if (CommonUtils.isNotBlank(cond.getDishesName())) {
                where.append(" and dishesName like '%").append(cond.getDishesName()).append("%'");
            }
            if (cond.getDishesTypeId() != null) {
                where.append(" and dishesTypeId = ").append(cond.getDishesTypeId());
            }
            if (cond.getDishesStatus() != null) {
                where.append(" and dishesStatus = ").append(cond.getDishesStatus());
            }
            String sql = "select * from dishes_list where 1=1 " + where
                    + " limit " + (pageNo - 1) * pageSize + "," + pageSize;

            System.out.println(sql);
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
