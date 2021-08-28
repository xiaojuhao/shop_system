package com.xjh.ws.handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.CartService;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("clearCart")
public class ClearCartHandler implements WsHandler {
    @Inject
    CartService cartService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        Integer deskId = msg.getInteger("tables_id");
        Result<String> rs = cartService.clearCart(deskId);

        JSONObject result = new JSONObject();
        result.put("API_TYPE", "clearCart_ACK");
        if (rs.isSuccess()) {
            result.put("status", 0);
        } else {
            result.put("status", 1);
            result.put("msg", "清空购物车失败!");
        }
        return result;
    }
}
