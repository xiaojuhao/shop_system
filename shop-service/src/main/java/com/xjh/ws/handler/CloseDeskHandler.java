package com.xjh.ws.handler;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.DeskService;

@Singleton
public class CloseDeskHandler {
    @Inject
    DeskService deskService;

    public JSONObject handle(JSONObject msg) {
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "closetable_ACK");

        int deskId = msg.getIntValue("tables_id");

        Result<String> openDeskRs = deskService.closeDesk(deskId);

        if (openDeskRs.isSuccess()) {
            resp.put("status", 0);
        } else {
            resp.put("status", 1);
            resp.put("msg", "关台失败:" + openDeskRs.getMsg());
        }
        return resp;
    }
}
