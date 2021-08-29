package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.valueobject.PageCond;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesPackageDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public DishesPackage getById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            DishesPackage cond = new DishesPackage();
            cond.setDishesPackageId(id);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackage.class).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<DishesPackage> selectList(DishesPackage cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPackage.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<DishesPackage> pageQuery(DishesPackage cond, PageCond page) {
        try {
            int pageNo = CommonUtils.orElse(page.getPageNo(), 1);
            int pageSize = CommonUtils.orElse(page.getPageSize(), 20);
            StringBuilder where = new StringBuilder();
            if (CommonUtils.isNotBlank(cond.getDishesPackageName())) {
                where.append(" and dishesPackageName like '%" + cond.getDishesPackageName() + "%'");
            }
            if (cond.getDishesPackageId() != null) {
                where.append(" and dishesPackageId = " + cond.getDishesPackageId());
            }
            String sql = "select * from dishes_package_list_new where 1=1 " + where
                    + " limit " + (pageNo - 1) + "," + pageSize;
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, DishesPackage.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
