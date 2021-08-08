package com.xjh.startup.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.ws.handler.AddCartHandler;
import com.xjh.ws.handler.CloseDeskHandler;
import com.xjh.ws.handler.OpenDeskHandler;
import com.xjh.ws.handler.OrderCartHandler;
import com.xjh.ws.handler.SocketOpenHandler;


public class XjhWebSocketServer extends WebSocketServer {
    AddCartHandler addCartHandler = GuiceContainer.getInstance(AddCartHandler.class);
    CloseDeskHandler closeDeskHandler = GuiceContainer.getInstance(CloseDeskHandler.class);
    OpenDeskHandler openDeskHandler = GuiceContainer.getInstance(OpenDeskHandler.class);
    OrderCartHandler orderCartHandler = GuiceContainer.getInstance(OrderCartHandler.class);
    SocketOpenHandler socketOpenHandler = GuiceContainer.getInstance(SocketOpenHandler.class);

    public XjhWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public void startWS() {
        LogUtils.info("启动WebSocket服务器......");
        super.start();
    }

    public void stopQuietly() {
        try {
            LogUtils.info("停止WebSocket服务器......");
            this.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        JSONObject resp = socketOpenHandler.handle(webSocket);
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
            resp = openDeskHandler.handle(msg);
        } else if (CommonUtils.eq(type, "closetable")) {
            resp = closeDeskHandler.handle(msg);
        } else if (CommonUtils.eq(type, "addDishesToCart")) {
            resp = addCartHandler.handle(msg);
        } else if (CommonUtils.eq(type, "orderCart")) {
            resp = orderCartHandler.handle(msg);
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
