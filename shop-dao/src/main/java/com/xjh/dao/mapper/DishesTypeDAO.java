package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesTypeDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<DishesType> selectAllValid() {
        try {
            DishesType cond = new DishesType();
            cond.setTypeStatus(1);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesType.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<DishesType> selectList(DishesType cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesType.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int deleteByPK(DishesType id) {
        try {
            if (id.getTypeId() == null) {
                throw new RuntimeException("ID必输");
            }
            return Db.use(ds).del(EntityUtils.idCond(id));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int updateByPK(DishesType update) {
        try {
            return Db.use(ds).update(EntityUtils.create(update),
                    EntityUtils.idCond(update));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int insert(DishesType newType) {
        try {
            return Db.use(ds).insert(EntityUtils.create(newType));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
