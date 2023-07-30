package com.xjh.dao.mapper;

import cn.hutool.core.codec.Base64;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Const;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.dao.dataobject.CartDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.List;

import static com.xjh.common.utils.CommonUtils.firstOf;
import static com.xjh.common.utils.CommonUtils.tryDecodeBase64;

@Singleton
public class CartDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<String> save(CartVO cart) throws SQLException {
        CartDO dd = getCartDO(cart.getDeskId());
        if (dd == null) {
            dd = new CartDO();
            dd.setDeskId(cart.getDeskId());
            dd.setCreateTime(System.currentTimeMillis());
            String contents = JSON.toJSONString(cart.getContents());
            dd.setContents(Const.KEEP_BASE64 ? Base64.encode(contents) : contents);
            int i = Db.use(ds).insert(EntityUtils.create(dd));
            return (i > 0 ? Result.success("保存购物车成功") : Result.fail("保存购物车失败"));
        } else {
            String contents = JSON.toJSONString(cart.getContents());
            dd.setContents(Const.KEEP_BASE64 ? Base64.encode(contents) : contents);
            int i = Db.use(ds).update(EntityUtils.create(dd), EntityUtils.idCond(dd));
            return (i > 0 ? Result.success("更新购物车成功") : Result.fail("更新购物车失败"));
        }
    }

    public Result<String> clearCart(Integer deskId){
        try{
            CartDO cart = getCartDO(deskId);
            if(cart != null){
                Db.use(ds).del(EntityUtils.idCond(cart));
            }
            return Result.success("");
        }catch (Exception ex){
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public CartDO getCartDO(Integer deskId) {
        try {
            StringBuilder sql = new StringBuilder("select * from cart where deskId = " + deskId);
            List<Entity> list = Db.use(ds).query(sql.toString());
            if (list.size() > 0) {
                return EntityUtils.convert(list.get(0), CartDO.class);
            }
            CartDO newCart = new CartDO();
            newCart.setDeskId(deskId);
            newCart.setContents(Const.KEEP_BASE64 ? Base64.encode("[]") : "[]");
            newCart.setCreateTime(System.currentTimeMillis());
            List<Object> keys = Db.use(ds).insertForGeneratedKeys(EntityUtils.create(newCart));
            Integer key = CommonUtils.parseInt(firstOf(keys), null);
            newCart.setId(key);
            return newCart;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public CartVO getDeskCard(Integer deskId) {
        try {
            CartDO cart = getCartDO(deskId);
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setDeskId(cart.getDeskId());
            String contents = tryDecodeBase64(cart.getContents());
            vo.setContents(JSON.parseArray(contents, CartItemVO.class));
            vo.setCreateTime(cart.getCreateTime());
            return vo;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
