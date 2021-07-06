package com.xjh.startup.server.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.startup.foundation.guice.GuiceContainer;

public class AddCartHandler {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    CartService cartService = GuiceContainer.getInstance(CartService.class);

    public JSONObject handle(JSONObject msg) {
        JSONObject resp = new JSONObject();
        int deskId = msg.getIntValue("tables_id");
        int dishesId = msg.getIntValue("dishesId");

        CartItemVO cartItem = new CartItemVO();
        cartItem.setDishesId(dishesId);
        cartItem.setDishesPriceId(0);
        cartItem.setNums(1);
        cartItem.setIfDishesPackage(0);
        Result<CartVO> addCartRs = cartService.addItem(deskId, cartItem);
        resp.put("API_TYPE", "addDishesToCart_ACK");
        if (addCartRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", "关台失败:" + addCartRs.getMsg());
        }
        return resp;
    }
}
