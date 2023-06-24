package com.xjh.startup.foundation.ws;


import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;

public interface WsHandler {
    JSONObject handle(WebSocket ws, JSONObject msg);
}
