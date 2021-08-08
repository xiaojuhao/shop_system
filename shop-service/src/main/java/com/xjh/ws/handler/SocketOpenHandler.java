package com.xjh.ws.handler;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;

@Singleton
public class SocketOpenHandler {
    public JSONObject handle(WebSocket webSocket) {
        JSONObject content = new JSONObject();
        content.put("Server_version", "v1.0");
        content.put("clientIP", webSocket.getLocalSocketAddress());

        JSONArray contents = new JSONArray();
        contents.add(content);

        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "connect success");
        resp.put("contents", contents);
        return resp;
    }
}