package com.xjh.startup;

import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.guice.GuiceContainer;
import com.xjh.startup.foundation.constants.MainStageHolder;
import com.xjh.startup.view.LoginView;
import com.xjh.ws.XjhWebSocketServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class LoginApp extends Application {


    @Override
    public void start(Stage primaryStage) {
        // 主Stage
        MainStageHolder.hold(primaryStage);

        TimeRecord timeRecord = TimeRecord.start();
        primaryStage.setTitle("登录系统");
        primaryStage.setMaximized(true);
        primaryStage.setScene(LoginView.getLoginView());
        primaryStage.show();
        Logger.info("主页面渲染, cost " + timeRecord.getCostAndReset());
        // 启动 webSocket服务器
        XjhWebSocketServer ws = GuiceContainer.getInstance(XjhWebSocketServer.class);
        ws.startWS();
        primaryStage.setOnCloseRequest(evt -> ws.stopQuietly());
        Logger.info("启动WebSocket服务器，cost " + timeRecord.getCostAndReset());
    }

    public static void main(String[] args) {
        launch(args);
    }
}