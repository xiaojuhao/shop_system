package com.xjh.startup.foundation.ws;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.hutool.core.lang.Snowflake;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.service.ws.SocketUtils;
import com.xjh.service.ws.WsApiType;
import com.xjh.service.ws.WsAttachment;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.startup.foundation.ioc.GuiceContainer;

import static com.xjh.common.utils.CommonUtils.abbr;
import static com.xjh.service.ws.NotifyService.notifyServerClosed;

public class XjhWebSocketServer extends WebSocketServer {
    private final Map<String, WsHandler> handlers = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean();

    public static void startWS(int port) {
        XjhWebSocketServer server = new XjhWebSocketServer(port);
        server.start();
        Logger.info("启动WebSocket服务器...... >> listen on " + port);
        // 关闭系统时退出
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopQuietly));
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
            notifyServerClosed();
            this.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(Long.toHexString(snowflake.nextId()));
        }
    }

    static Snowflake snowflake = new Snowflake(1, 1);

    @Override
    public void onOpen(WebSocket ws, ClientHandshake clientHandshake) {
        tryInitHandlers();
        JSONObject resp = handlers.get("socketOpen").handle(ws, null);
        WsAttachment att = new WsAttachment();
        att.setRequestId("ws" + Long.toHexString(snowflake.nextId()));
        ws.setAttachment(att);
        Logger.info(att.getRequestId() + " >> WS链接打开: " + resp);
        ws.send(resp.toJSONString());
        // 维护起来
        SocketUtils.put(att.getRequestId(), ws);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        WsAttachment att = webSocket.getAttachment();
        Logger.info(att.getRequestId() + " >> WS链接关闭");
        SocketUtils.remove(att.getRequestId());
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        // 本地请求id
        CurrentRequest.resetRequestId();
        try {
            WsAttachment attachment = ws.getAttachment();
            Logger.info(traceUuid(ws) + " >> 收到了消息: " + message);
            JSONObject msg = JSONObject.parseObject(message);

            WsHandler handler = getHandler(msg);
            if (handler != null) {
                JSONObject resp = handler.handle(ws, msg);
                Logger.info(traceUuid(ws) + " >> 响应结果: " + abbr(resp.toJSONString(), 200));
                ws.send(resp.toJSONString());

                attachment.drains();
            } else {
                Logger.info(traceUuid(ws) + " >> 无法响应内容");
            }
        } catch (Exception ex) {
            Logger.error("onMessage >> " + ex.getMessage());
        } finally {
            CurrentRequest.clear();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
        if (webSocket != null) {
            WsAttachment uuid = webSocket.getAttachment();
            Logger.info(uuid.getRequestId() + " >> socket出现了异常, 关闭链接" + e);
            webSocket.close();
        }
    }

    public static String traceUuid(WebSocket ws) {
        WsAttachment attachment = ws.getAttachment();
        return attachment.getRequestId() + "-" + CurrentRequest.requestId();
    }

    @Override
    public void onStart() {

    }
}
