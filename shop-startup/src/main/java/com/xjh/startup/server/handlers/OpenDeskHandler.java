package com.xjh.startup.server.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.OpenDeskParam;
import com.xjh.startup.foundation.guice.GuiceContainer;

public class OpenDeskHandler {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

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
