package com.xjh.startup.foundation.ws;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;
import com.xjh.ws.handler.SocketOpenHandler;

public class XjhWebSocketServer extends WebSocketServer {
    private final Map<String, WsHandler> handlers = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean();

    public static XjhWebSocketServer startWS(int port) {
        XjhWebSocketServer server = new XjhWebSocketServer(port);
        server.start();
        Logger.info("启动WebSocket服务器...... >> listen on " + port);
        return server;
    }

    private WsHandler getHandler(JSONObject msg) {
        tryInitHandlers();
        return handlers.get(msg.getString("API_TYPE"));
    }

    private void tryInitHandlers() {
        if (initialized.compareAndSet(false, true)) {
            GuiceContainer.getInjector().getBindings().forEach((k, v) -> {
                Object inst = GuiceContainer.getInjector().getInstance(k);
                WsApiType wsType = inst.getClass().getAnnotation(WsApiType.class);
                if (inst instanceof WsHandler && wsType != null) {
                    String[] types = wsType.value();
                    Logger.info("Initialize WebSocket Handler: " + CommonUtils.stringJoin(types, ","));
                    for (String t : types) {
                        handlers.put(t, (WsHandler) inst);
                    }
                }
            });
        }
    }

    public XjhWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public void stopQuietly() {
        try {
            Logger.info("停止WebSocket服务器......");
            this.stop();
            initialized.set(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake clientHandshake) {
        JSONObject resp = handlers.get("socketOpen").handle(ws, null);
        String uuid = CommonUtils.randomStr(10);
        ws.setAttachment(uuid);
        Logger.info(uuid + " >> WS链接打开: " + resp);
        ws.send(resp.toJSONString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String uuid = webSocket.getAttachment();
        Logger.info(uuid + " >> WS链接关闭");
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        String uuid = ws.getAttachment();
        Logger.info(uuid + " >> 收到了消息: " + message);
        JSONObject msg = JSONObject.parseObject(message);

        WsHandler handler = getHandler(msg);
        if (handler != null) {
            JSONObject resp = handler.handle(ws, msg);
            Logger.info(uuid + " >> 响应结果: " + resp);
            ws.send(resp.toJSONString());
        } else {
            Logger.info(uuid + " >> 无法响应内容");
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
