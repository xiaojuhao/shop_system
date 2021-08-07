package com.xjh.ws.handler;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.OpenDeskParam;

@Singleton
public class OpenDeskHandler {
    @Inject
    DeskService deskService;

    public JSONObject handle(JSONObject msg) {
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "openDesk_ACK");

        int customerNums = msg.getIntValue("meal_number");
        int deskId = msg.getIntValue("tables_id");
        String recommender = msg.getString("recommender");

        OpenDeskParam openDeskParam = new OpenDeskParam();
        openDeskParam.setDeskId(deskId);
        openDeskParam.setCustomerNum(customerNums);
        Result<String> openDeskRs = deskService.openDesk(openDeskParam);
        if (openDeskRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", openDeskRs.getMsg());
        }
        return resp;
    }
}
