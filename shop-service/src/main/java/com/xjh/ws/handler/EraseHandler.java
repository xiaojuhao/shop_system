package com.xjh.ws.handler;


import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderService;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("change")
public class EraseHandler implements WsHandler {
    @Inject
    DeskService deskService;
    @Inject
    OrderService orderService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "change_ACK");

        try {
            int deskId = msg.getInteger("tables_id");
            Desk desk = deskService.getById(deskId);
            //            Order order = orderManager.getOrder(desk);
            //            if (order.getOrderRefund() > 0) {
            //                throw new Exception("已经进行反结账操作，不可以再进行抹零操作");
            //            }
            double smallChangePrice = msg.getDouble("changeprice");
            Result<String> eraseRs = orderService.erase(desk.getOrderId(), smallChangePrice);
            if (eraseRs.isSuccess()) {
                jSONObjectReturn.put("status", 0);
            } else {
                jSONObjectReturn.put("status", 1);
                jSONObjectReturn.put("msg", "抹零失败!");
            }
            //boolean result = placeOrderManager.updateSmallChange(desk, smallChangePrice, this);
        } catch (Exception e) {
            jSONObjectReturn = new JSONObject();
            jSONObjectReturn.put("API_TYPE", "change_ACK");
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        return jSONObjectReturn;
    }
}
