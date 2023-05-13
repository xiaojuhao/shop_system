package com.xjh.ws;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.service.domain.CartService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.xjh.common.utils.CommonUtils.firstOf;

@Singleton
public class NotifyCenter {
    public static int FRONT_STS_SUCCESS = 0;
    public static int FRONT_STS_FAILURE = 1;

    @Inject
    CartService cartService;

    public static void notifyCartCleared(int deskId) {
        // 通知前端更新购物车
        JSONObject next = new JSONObject();
        next.put("API_TYPE", "orderCart");
        next.put("deskId", deskId);
        // next.put("operateAccount", order.getAccountId());
        SocketUtils.sendMsg(deskId, next);
    }

    // 通知前端，添加菜品到购物车
    public void cartAddOneRecord(Integer deskId, CartItemVO item) {
        CartVO cart = new CartVO();
        cart.setDeskId(deskId);
        List<CartItemVO> contentItems = cartService.getCartItems(deskId);
        cart.setContents(contentItems);
        // 通知前端
        JSONObject notify = new JSONObject();
        notify.put("API_TYPE", "cartAddOneRecord");
        notify.put("dishesAttribute", new JSONObject());
        notify.put("totalPrice", cartService.sumCartPrice(cart));
        notify.put("cartDishesId", cart.getId());
        notify.put("num", item.getNums());
        notify.put("dishesPriceId", 0);
        notify.put("dishesId", item.getDishesId());
        notify.put("type", "dishes");
        notify.put("deskId", cart.getDeskId());
        notify.put("operateAccount", "root");
        notify.put("cartDishesesNums", cart.sumDishesNum());
        SocketUtils.sendMsg(deskId, notify);
    }

    public void removeDishesFromCart(Integer deskId, List<Integer> removedCartDishesIds) {
        CartVO cart = new CartVO();
        cart.setDeskId(deskId);
        List<CartItemVO> contentItems = cartService.getCartItems(deskId);
        cart.setContents(contentItems);

        JSONObject notify = new JSONObject();
        notify.put("API_TYPE", "removeDishesFromCart");
        notify.put("deskId", deskId);
        notify.put("operateAccount", "root");
        notify.put("cartDishesesNums", cart.sumDishesNum());
        notify.put("totalPrice", cartService.sumCartPrice(cart));
        notify.put("cartDishesId", firstOf(removedCartDishesIds));
        SocketUtils.sendMsg(deskId, notify);
    }
}
