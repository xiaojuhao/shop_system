package com.xjh.startup.server.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.guice.GuiceContainer;

public class CloseDeskHandler {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

    public JSONObject handle(JSONObject msg) {
        JSONObject resp = new JSONObject();
        int deskId = msg.getIntValue("tables_id");

        Result<String> openDeskRs = deskService.closeDesk(deskId);
        resp.put("API_TYPE", "closetable_ACK");
        if (openDeskRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", "关台失败:" + openDeskRs.getMsg());
        }
        return resp;
    }
}
