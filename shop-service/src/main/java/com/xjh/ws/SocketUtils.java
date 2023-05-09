package com.xjh.ws;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SocketUtils {
    static Map<String, WebSocket> map = new ConcurrentHashMap<>();

    public static void put(String id, WebSocket ws) {
        map.put(id, ws);
    }

    public static void remove(String id) {
        if (id == null || id.equals("")) {
            return;
        }
        map.remove(id);
    }

    public static List<WebSocket> sockets() {
        return new ArrayList<>(map.values());
    }

    public static void sendMsg(Integer deskId, JSONObject msg) {
        for (WebSocket ws : map.values()) {
            WsAttachment att = ws.getAttachment();
            if (Objects.equals(att.getDeskId(), deskId)) {
                System.out.println("【 " + att.getRequestId() + " 】响应Socket: " + msg.toJSONString());
                ws.send(msg.toJSONString());
            }
        }
    }

}
