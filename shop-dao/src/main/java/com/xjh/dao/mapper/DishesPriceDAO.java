package com.xjh.dao.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DishesPriceDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(DishesPrice price) {
        try {
            return Db.use(ds).insert(EntityUtils.create(price));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int updateByPK(DishesPrice price) {
        try {
            return Db.use(ds).update(EntityUtils.create(price), EntityUtils.idCond(price));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int deleteByPK(Integer priceId) {
        try {
            if (priceId == null) {
                return 0;
            }
            DishesPrice id = new DishesPrice();
            id.setDishesPriceId(priceId);
            return Db.use(ds).del(EntityUtils.idCond(id));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public List<DishesPrice> selectList(DishesPrice cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, DishesPrice.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<DishesPrice> queryByDishesId(Integer dishesId) {
        if (dishesId == null) {
            return new ArrayList<>();
        }
        DishesPrice cond = new DishesPrice();
        cond.setDishesId(dishesId);
        return selectList(cond);
    }

    public DishesPrice queryByPriceId(Integer priceId) {
        if (priceId == null || priceId <= 0) {
            return null;
        }
        DishesPrice cond = new DishesPrice();
        cond.setDishesPriceId(priceId);
        return selectList(cond).stream().findFirst().orElse(null);
    }


}
