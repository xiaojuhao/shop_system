package com.xjh.startup.foundation.ws.handler;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.ws.WsHandler;
import com.xjh.service.ws.WsApiType;
import org.java_websocket.WebSocket;

@Singleton
@WsApiType("closetable")
public class CloseDeskHandler implements WsHandler {
    @Inject
    DeskService deskService;

    public JSONObject handle(WebSocket ws, JSONObject msg) {
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
