package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Cart;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class CartDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int save(Cart cart) throws SQLException {
        Cart exists = selectByDeskId(cart.getDeskId());
        if (exists == null) {
            cart.setCreateTime(DateBuilder.now().mills());
            return Db.use(ds).insert(EntityUtils.create(cart));
        } else {
            cart.setId(exists.getId());
            return updateById(cart);
        }
    }

    public int updateById(Cart cart) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(cart),
                EntityUtils.idCond(cart)
        );
    }

    public Cart selectByDeskId(Integer deskId) throws SQLException {
        Cart cart = new Cart();
        cart.setDeskId(deskId);
        List<Cart> list = selectList(cart);
        if (list.size() > 1) {
            throw new SQLException("find too many results :" + list.size());
        }
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public List<Cart> selectList(Cart cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Cart.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
