package com.xjh.ws;


import com.alibaba.fastjson.JSONObject;

public interface WsHandler {
    JSONObject handle(JSONObject msg);
}
