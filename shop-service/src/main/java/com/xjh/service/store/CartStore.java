package com.xjh.service.store;

import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;

public class CartStore {
    static CartDB cartDB = CartDB.inst();

    private static String buildCartId(Integer deskId) {
        return "cart_" + deskId;
    }

    public static Cart getCart(Integer deskId) {
        return cartDB.get(buildCartId(deskId), Cart.class);
    }

    public static Result<String> saveCart(Cart cart) {
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
