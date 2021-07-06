package com.xjh.startup.server.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.startup.foundation.guice.GuiceContainer;

public class OrderCartHandler {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

    public JSONObject handle(JSONObject msg) {
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "orderCart_ACK");

        int deskId = msg.getIntValue("tables_id");
        Desk desk = deskService.getById(deskId);
        if (desk == null) {
            resp.put("status", 1);
            resp.put("msg", "下单失败:桌号有误");
            return resp;
        }
        if (desk.getOrderId() == null) {
            resp.put("status", 1);
            resp.put("msg", "下单失败:餐桌未开台");
            return resp;
        }
        PlaceOrderFromCartReq param = new PlaceOrderFromCartReq();
        param.setOrderId(desk.getOrderId());
        param.setDeskId(deskId);
        Result<String> createOrderRs = cartService.createOrder(param);
        if (createOrderRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", createOrderRs.getMsg());
        }
        return resp;
    }
}
