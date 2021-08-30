package com.xjh.service.store;

import com.xjh.common.kvdb.Committable;
import com.xjh.common.kvdb.cart.CartKvDB;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;

public class CartStore {
    static CartKvDB cartKvDB = CartKvDB.inst();

    public static Cart getCart(Integer deskId) {
        String key = "cart_" + deskId;
        return cartKvDB.get(key, Cart.class);
    }

    public static Result<String> saveCart(Cart cart) {
        if (cart == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + cart.getDeskId();
        Committable committable = cartKvDB.beginTransaction();
        cartKvDB.put(key, cart);
        cartKvDB.commit(committable);
        return Result.success(null);
    }

    public static Result<String> clearCart(Integer deskId) {
        if (deskId == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + deskId;
        Committable committable = cartKvDB.beginTransaction();
        cartKvDB.remove(key);
        cartKvDB.commit(committable);
        return Result.fail("");
    }
}
