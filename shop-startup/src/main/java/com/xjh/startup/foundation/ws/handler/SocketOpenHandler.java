package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;
import org.java_websocket.WebSocket;

@Singleton
@WsApiType("socketOpen")
public class SocketOpenHandler implements WsHandler {
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject content = new JSONObject();
        content.put("Server_version", "v1.0");
        content.put("clientIP", ws.getLocalSocketAddress());

        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "connect success");
        resp.put("contents", CommonUtils.asList(content));
        return resp;
    }
}
