package com.xjh.ws.handler;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.service.domain.CartService;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("removeDishesFromCart")
public class RemoveDishesFromCartHandler implements WsHandler {
    @Inject
    CartService cartService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "removeDishesFromCart_ACK");

        try {
            int deskId = msg.getInteger("tables_id");
            Integer cartDishesId = msg.getInteger("cartDishesId");
            CartVO cartVO = cartService.getCart(deskId).getData();
            if (cartVO != null) {
                List<CartItemVO> cartItems = cartVO.getContents();
                cartItems = CommonUtils.filter(cartItems, it -> Objects.equals(it.getCartDishesId(), cartDishesId));
                cartVO.setContents(cartItems);
            }
            Result<CartVO> updateRs = cartService.updateCart(deskId, cartVO);
            if (updateRs.isSuccess()) {
                jSONObjectReturn.put("status", 0);
            } else {
                jSONObjectReturn.put("status", 1);
                jSONObjectReturn.put("msg", "删除失败");
            }
        } catch (Exception e) {
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        return jSONObjectReturn;
    }
}
