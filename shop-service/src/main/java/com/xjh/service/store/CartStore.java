package com.xjh.service.store;

import com.xjh.common.kvdb.Committable;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;

public class CartStore {
    static CartDB cartDB = CartDB.inst();

    public static Cart getCart(Integer deskId) {
        String key = "cart_" + deskId;
        return cartDB.get(key, Cart.class);
    }

    public static Result<String> saveCart(Cart cart) {
        if (cart == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + cart.getDeskId();
        Committable committable = cartDB.beginTransaction();
        cartDB.put(key, cart);
        cartDB.commit(committable);
        return Result.success(null);
    }

    public static Result<String> clearCart(Integer deskId) {
        if (deskId == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + deskId;
        Committable committable = cartDB.beginTransaction();
        cartDB.remove(key);
        cartDB.commit(committable);
        return Result.fail("");
    }
}
