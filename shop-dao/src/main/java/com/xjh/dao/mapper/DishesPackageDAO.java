package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.DishesPackageQuery;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.util.PageUtil.totalPage;

@Singleton
public class DishesPackageDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public DishesPackage getByDishesPackageId(Integer id) {
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

    public PageResult<DishesPackage> pageQuery(DishesPackageQuery cond) {
        int pageNo = OrElse.orGet(cond.getPageNo(), 1);
        int pageSize = OrElse.orGet(cond.getPageSize(), 20);
        PageResult<DishesPackage> result = new PageResult<>(pageNo, pageSize, 0);
        try {
            StringBuilder where = new StringBuilder();
            if (CommonUtils.isNotBlank(cond.getName())) {
                where.append(" and dishesPackageName like '%" + cond.getName() + "%'");
            }
            if (cond.getPackageId() != null) {
                where.append(" and dishesPackageId = " + cond.getPackageId());
            }
            if (cond.getStatus() != null) {
                where.append(" and dishesPackageStatus = " + cond.getStatus());
            }
            int total = Db.use(ds).queryNumber("select * from dishes_package_list_new where 1=1 " + where).intValue();
            String sql = "select * from dishes_package_list_new where 1=1 " + where + " limit " + (pageNo - 1) + "," + pageSize;
            List<Entity> list = Db.use(ds).query(sql);
            List<DishesPackage> dataList = EntityUtils.convertList(list, DishesPackage.class);
            result.addAll(dataList);
            result.setTotal(total);
            result.setTotalPage(totalPage(total, pageSize));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setTotal(0);
        }
        return result;
    }

    public Result<Integer> updateById(DishesPackage dishesPackage) {
        if (dishesPackage == null) {
            return Result.fail("入参错误");
        }
        try {
            int i = Db.use(ds).update(EntityUtils.create(dishesPackage), EntityUtils.idCond(dishesPackage));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Integer> insert(DishesPackage dishesPackage) {
        if (dishesPackage == null) {
            return Result.fail("入参错误");
        }
        try {
            int i = Db.use(ds).insert(EntityUtils.create(dishesPackage));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }
}
