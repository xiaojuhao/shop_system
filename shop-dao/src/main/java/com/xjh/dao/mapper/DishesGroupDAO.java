package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesGroup;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

@Singleton
public class DishesGroupDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int saveDishesGroup(DishesGroup dg) throws SQLException {
        if(dg.getDishesGroupId() != null){
            return updateByDishesGroupId(dg);
        }
        return Db.use(ds).insert(EntityUtils.create(dg));
    }

    public int insert(DishesGroup dg) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(dg));
    }

    public int updateByDishesGroupId(DishesGroup dg) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(dg),
                EntityUtils.idCond(dg)
        );
    }

    public DishesGroup selectByDishesGroupId(Integer dishesGroupId) {
        DishesGroup cond = new DishesGroup();
        cond.setDishesGroupId(dishesGroupId);
        Result<List<DishesGroup>> groups = selectList(cond);
        if (CommonUtils.isEmpty(groups.getData())) {
            return null;
        }
        return groups.getData().get(0);
    }

    public Result<DishesGroup> selectByDishesGroupName(String dishesGroupName) {
        DishesGroup cond = new DishesGroup();
        cond.setDishesGroupName(dishesGroupName);
        Result<List<DishesGroup>> groups = selectList(cond);
        if (CommonUtils.isEmpty(groups.getData())) {
            return Result.fail("没有找到记录");
        }
        return Result.success(groups.getData().get(0));
    }

    public Result<List<DishesGroup>> selectList(DishesGroup cond)  {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return Result.success(EntityUtils.convertList(list, DishesGroup.class));
        }catch (Exception ex){
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }

    }

}
