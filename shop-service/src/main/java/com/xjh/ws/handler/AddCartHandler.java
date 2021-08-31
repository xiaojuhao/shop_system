package com.xjh.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.service.domain.CartService;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.ws.WsHandler;
import com.xjh.ws.WsApiType;
import org.java_websocket.WebSocket;

@Singleton
@WsApiType(value = "addDishesToCart")
public class AddCartHandler implements WsHandler {
    @Inject
    CartService cartService;
    @Inject
    DishesDAO dishesDAO;

    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "addDishesToCart_ACK");

        int deskId = msg.getIntValue("tables_id");
        int dishesId = msg.getIntValue("dishesId");
        int dishesNum = msg.getIntValue("num");
        Dishes dishes = dishesDAO.getById(dishesId);
        if (dishes == null) {
            resp.put("status", 1);
            resp.put("msg", "菜品" + dishesId + "不存在");
            return resp;
        }
        CartItemVO cartItem = new CartItemVO();
        cartItem.setDishesId(dishesId);
        cartItem.setDishesPriceId(0);
        cartItem.setNums(dishesNum);
        cartItem.setIfDishesPackage(0);
        Result<CartVO> addCartRs = cartService.addItem(deskId, cartItem);

        if (addCartRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", "添加购物车失败:" + addCartRs.getMsg());
        }
        return resp;
    }
}
