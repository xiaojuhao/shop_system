package com.xjh.startup.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.startup.server.handlers.AddCartHandler;
import com.xjh.startup.server.handlers.CloseDeskHandler;
import com.xjh.startup.server.handlers.OpenDeskHandler;

public class XjhWebSocketServer extends WebSocketServer {
    public XjhWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        JSONObject content = new JSONObject();
        content.put("Server_version", "v1.0");
        content.put("clientIP", webSocket.getLocalSocketAddress());

        JSONArray contents = new JSONArray();
        contents.add(content);

        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "connect success");
        resp.put("contents", contents);
        webSocket.send(resp.toJSONString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        JSONObject msg = JSONObject.parseObject(message);
        String type = msg.getString("API_TYPE");
        if (CommonUtils.equals(type, "openDesk")) {
            ws.send(new OpenDeskHandler().handle(msg).toJSONString());
        } else if (CommonUtils.eq(type, "closetable")) {
            ws.send(new CloseDeskHandler().handle(msg).toJSONString());
        } else if (CommonUtils.eq(type, "addDishesToCart")) {
            ws.send(new AddCartHandler().handle(msg).toJSONString());
        }
        ws.close();
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
