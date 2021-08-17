package com.xjh.startup.another;

import com.xjh.common.utils.DateBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketTest extends WebSocketServer {
    public WebSocketTest(int port) {
        super(new InetSocketAddress(port));
    }
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("链接成功");
        webSocket.setAttachment("链接时间:" + DateBuilder.now().timeStr());
        webSocket.send("链接成功, " + webSocket + ", " + webSocket.getAttachment());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("关闭成功:" + i + ", " + s + " , " + b);
        webSocket.send("关闭成功, " + webSocket + " , " + webSocket.getAttachment());

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("收到了消息:" + s);
        webSocket.send("收到了消息:" + s + ", " + webSocket+ ", " + webSocket.getAttachment());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        if(e instanceof WebsocketNotConnectedException){
            System.out.println("失去了链接");
        }else {
            System.out.println("异常:" + e);
            webSocket.send("异常");
        }
    }

    @Override
    public void onStart() {
        System.out.println("start");
    }

    public static void main(String[] args) {
        WebSocketTest ws = new WebSocketTest(8800);
        ws.start();
    }
}
