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
        DishesPrice cond = new DishesPrice();
        cond.setDishesPriceId(priceId);
        return selectList(cond).stream().findFirst().orElse(null);
    }


}
