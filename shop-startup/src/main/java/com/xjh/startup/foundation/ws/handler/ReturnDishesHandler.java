package com.xjh.startup.foundation.ws.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;

@Singleton
@WsApiType("returnDishes")
public class ReturnDishesHandler implements WsHandler {
    @Inject
    DeskService deskService;
    @Inject
    OrderDishesService orderDishesService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "returnDishes_ACK");
        try {
            int deskId = msg.getInteger("tables_id");
            Desk desk = deskService.getById(deskId);
            JSONArray jSONArrayOrderDishesIndexs = msg.getJSONArray("orderDishesIndexs");
            List<OrderDishes> orderDisheses = orderDishesService.selectByOrderId(desk.getOrderId());
            for (int i = 0; i < jSONArrayOrderDishesIndexs.size(); i++) {
                int orderDishesIndex = jSONArrayOrderDishesIndexs.getInteger(i);
                if (orderDishesIndex < orderDisheses.size() && orderDishesIndex >= 0) {
                    orderDishesService.returnOrderDishes(orderDisheses.get(orderDishesIndex));
                }
            }
            jSONObjectReturn.put("status", 0);
        } catch (Exception e) {
            //e.printStackTrace();
            jSONObjectReturn = new JSONObject();
            jSONObjectReturn.put("API_TYPE", "sendDishes_ACK");
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        return jSONObjectReturn;
    }
}
