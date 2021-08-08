package com.xjh.startup;

import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.startup.foundation.constants.LoginSceneHolder;
import com.xjh.startup.foundation.constants.MainStageHolder;
import com.xjh.startup.server.XjhWebSocketServer;
import com.xjh.startup.view.FxmlView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {


    @Override
    public void start(Stage primaryStage) {
        // 主Stage
        MainStageHolder.hold(primaryStage);

        TimeRecord timeRecord = TimeRecord.start();
        // for test
        VBox login = FxmlView.load("login");
        assert login != null;
        login.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(0, 400, 0, 400));
        Scene loginScene = new Scene(login);
        loginScene.getStylesheets().add("/css/style.css");
        LoginSceneHolder.hold(loginScene);
        LogUtils.info("加载登录界面, cost " + timeRecord.getCostAndReset());

        primaryStage.setTitle("登录系统");
        primaryStage.setMaximized(true);
        primaryStage.setScene(loginScene);
        primaryStage.show();
        LogUtils.info("主页面渲染, cost " + timeRecord.getCostAndReset());
        // 启动 webSocket服务器
        XjhWebSocketServer ws = new XjhWebSocketServer(8889);
        ws.startWS();
        primaryStage.setOnCloseRequest(evt -> ws.stopQuietly());
        LogUtils.info("启动WebSocket服务器，cost " + timeRecord.getCostAndReset());
    }

    public static void main(String[] args) {
        launch(args);
    }
}