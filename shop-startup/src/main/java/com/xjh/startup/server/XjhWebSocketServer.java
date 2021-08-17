package com.xjh.startup.server;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.ws.handler.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;


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
        Logger.info("启动WebSocket服务器......");
        super.start();
    }

    public void stopQuietly() {
        try {
            Logger.info("停止WebSocket服务器......");
            this.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        JSONObject resp = socketOpenHandler.handle(webSocket);
        String uuid = CommonUtils.randomStr(10);
        webSocket.setAttachment(uuid);
        Logger.info(uuid + " >> WS链接打开: " + resp);
        webSocket.send(resp.toJSONString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String uuid = webSocket.getAttachment();
        Logger.info(uuid + " >> WS链接关闭");
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        String uuid = ws.getAttachment();
        Logger.info(uuid + " >> 收到了消息:" + message);
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
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        String uuid = webSocket.getAttachment();
        Logger.info(uuid + " >> socket出现了异常, 关闭链接" + e);
        webSocket.close();
    }

    @Override
    public void onStart() {

    }
}
