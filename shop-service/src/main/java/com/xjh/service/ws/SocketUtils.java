package com.xjh.service.ws;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.DelayedRunnable;
import com.xjh.common.utils.Safe;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

    static DelayQueue<DelayedRunnable> delayQueue = new DelayQueue<>();
    static AtomicBoolean started = new AtomicBoolean(false);

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void delay(Runnable run, int delaySec) {
        delayQueue.offer(new DelayedRunnable(run, delaySec), delaySec, TimeUnit.SECONDS);
        trigger();
    }

    public static void trigger() {
        if (started.compareAndSet(false, true)) {
            executorService.submit(() -> {
                while (true) {
                    DelayedRunnable dr = delayQueue.take();
                    Safe.run(dr.runnable);
                }
            });
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            delay(() -> System.out.println("aaaaaaaaaa" + finalI), i);
        }
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

    public static void broadcastMsg(JSONObject msg) {
        for (WebSocket ws : map.values()) {
            try {
                WsAttachment att = ws.getAttachment();
                System.out.println("【 " + att.getRequestId() + " 】响应Socket: " + msg.toJSONString());
                ws.send(msg.toJSONString());
            } catch (Exception ex) {
                System.out.println("【异常】响应Socket: " + ex.getMessage() + ", " + msg.toJSONString());
            }
        }
    }

    static ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    public static void asyncSendMsg(Integer deskId, JSONObject msg) {
        executorService2.submit(() -> sendMsg(deskId, msg));
    }

}
