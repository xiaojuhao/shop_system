package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;
import com.xjh.startup.view.OrderDetailView;
import org.java_websocket.WebSocket;

import javax.inject.Inject;

import static com.xjh.service.ws.NotifyService.FRONT_STS_FAILURE;
import static com.xjh.service.ws.NotifyService.FRONT_STS_SUCCESS;

@Singleton
@WsApiType("orderCart")
public class OrderCartHandler implements WsHandler {
    @Inject
    CartService cartService;
    @Inject
    DeskService deskService;

    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "orderCart_ACK");

        int deskId = msg.getIntValue("tables_id");
        Desk desk = deskService.getById(deskId);
        if (desk == null) {
            resp.put("status", FRONT_STS_FAILURE);
            resp.put("msg", "下单失败:桌号有误");
            return resp;
        }
        if (desk.getOrderId() == null) {
            resp.put("status", FRONT_STS_FAILURE);
            resp.put("msg", "下单失败:餐桌未开台");
            return resp;
        }

        PlaceOrderFromCartReq param = new PlaceOrderFromCartReq();
        param.setOrderId(desk.getOrderId());
        param.setDeskId(deskId);
        param.setAccountId(0);
        Result<String> createOrderRs = cartService.createOrder(param);
        if (createOrderRs.isSuccess()) {
            resp.put("status", FRONT_STS_SUCCESS);
            OrderDetailView.refreshView(deskId);
        } else {
            resp.put("status", FRONT_STS_FAILURE);
            resp.put("msg", createOrderRs.getMsg());
        }

        return resp;
    }
}
