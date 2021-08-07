package com.xjh.startup;

import com.xjh.common.utils.Holder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.startup.server.XjhWebSocketServer;
import com.xjh.startup.view.FxmlView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {
    public static Holder<Stage> loginStage = new Holder<>();
    public static Holder<XjhWebSocketServer> server = new Holder<>();

    @Override
    public void start(Stage primaryStage) {
        TimeRecord timeRecord = TimeRecord.start();
        // for test
        VBox login = FxmlView.load("login");
        assert login != null;
        login.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(0, 400, 0, 400));
        Scene loginScene = new Scene(login);
        loginScene.getStylesheets().add("/css/style.css");
        LogUtils.info("加载登录界面, cost " + timeRecord.getCostAndReset());

        primaryStage.setTitle("登录系统");
        primaryStage.setMaximized(true);
        primaryStage.setScene(loginScene);
        primaryStage.setOnHidden(evt -> server.get().stopQuietly());
        primaryStage.show();
        loginStage.hold(primaryStage);
        LogUtils.info("主页面渲染, cost " + timeRecord.getCostAndReset());
        // 启动 webSocket服务器
        server.hold(new XjhWebSocketServer(8889)).start();
        LogUtils.info("启动WebSocket服务器，cost " + timeRecord.getCostAndReset());
    }

    public static void main(String[] args) {
        launch(args);
    }
}