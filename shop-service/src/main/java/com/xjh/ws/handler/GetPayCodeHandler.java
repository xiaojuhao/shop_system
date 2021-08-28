package com.xjh.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.service.domain.DeskService;
import com.xjh.ws.WsHandler;

@Singleton
public class GetPayCodeHandler implements WsHandler {
    @Inject
    DeskService deskService;

    public JSONObject handle(JSONObject msg) {
        int deskId = msg.getInteger("tables_id");
        String payType = msg.getString("pay_type");
        String remark = msg.getString("remark");
        double amount = msg.getDouble("amount");
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "getPayCode_ACK");
        jSONObjectReturn.put("status", 1);
        jSONObjectReturn.put("deskId", deskId);
        jSONObjectReturn.put("msg", "请到收银台结账!");
        return jSONObjectReturn;
    }
}
