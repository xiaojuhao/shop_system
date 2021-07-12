package com.xjh.startup.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.startup.server.handlers.AddCartHandler;
import com.xjh.startup.server.handlers.CloseDeskHandler;
import com.xjh.startup.server.handlers.OpenDeskHandler;
import com.xjh.startup.server.handlers.OrderCartHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class XjhWebSocketServer extends WebSocketServer {
    public XjhWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public void stopQuietly() {
        try {
            this.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        JSONObject resp = null;
        String type = msg.getString("API_TYPE");
        if (CommonUtils.equals(type, "openDesk")) {
            resp = new OpenDeskHandler().handle(msg);
        } else if (CommonUtils.eq(type, "closetable")) {
            resp = new CloseDeskHandler().handle(msg);
        } else if (CommonUtils.eq(type, "addDishesToCart")) {
            resp = new AddCartHandler().handle(msg);
        } else if (CommonUtils.eq(type, "orderCart")) {
            resp = new OrderCartHandler().handle(msg);
        }
        if (resp != null) {
            ws.send(resp.toJSONString());
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
