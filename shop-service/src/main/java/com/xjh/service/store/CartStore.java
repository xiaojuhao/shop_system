package com.xjh.service.store;

import com.xjh.common.kvdb.impl.CartDB;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;

import com.xjh.common.valueobject.CartVO;

import java.util.ArrayList;

public class CartStore {
    static CartDB cartDB = CartDB.inst();

    private static String buildCartId(Integer deskId) {
        return "cart_" + deskId;
    }

    public static CartVO getCart(Integer deskId) {
        CartVO cart = cartDB.get(buildCartId(deskId), CartVO.class);
        if (cart == null) {
            cart = new CartVO();
            cart.setDeskId(deskId);
            cart.setContents(new ArrayList<>());
            cart.setCreateTime(DateBuilder.now().mills());
        }
        return cart;
    }

    public static Result<String> saveCart(CartVO cart) {
        if (cart == null) {
            return Result.fail("保存失败,入参错误");
        }
        cartDB.putInTransaction(buildCartId(cart.getDeskId()), cart);
        return Result.success(null);
    }

    public static Result<String> clearCart(Integer deskId) {
        if (deskId == null) {
            return Result.fail("保存失败,入参错误");
        }
        cartDB.removeInTransaction(buildCartId(deskId));
        return Result.success(null);
    }
}
