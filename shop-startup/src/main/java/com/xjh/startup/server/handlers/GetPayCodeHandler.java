package com.xjh.startup.server.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.guice.GuiceContainer;

public class GetPayCodeHandler {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

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
