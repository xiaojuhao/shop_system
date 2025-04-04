package com.xjh.startup;

import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.service.domain.ConfigService;
import com.xjh.startup.foundation.constants.MainStageHolder;
import com.xjh.startup.foundation.ws.XjhWebSocketServer;
import com.xjh.startup.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class LoginApp extends Application {


    @Override
    public void start(Stage primaryStage) {
        // 主Stage
        MainStageHolder.hold(primaryStage);
        // 加载配置文件
        ConfigService.loadConfiguration();

        TimeRecord timeRecord = TimeRecord.start();
        primaryStage.setTitle("登录系统");
        primaryStage.setMaximized(true);
        primaryStage.setScene(LoginView.getLoginView());
        primaryStage.show();
        Logger.info("主页面渲染  , cost " + timeRecord.getCostAndReset());
        // 启动 webSocket服务器
        XjhWebSocketServer.startWS(8889);
        primaryStage.setOnCloseRequest(evt -> {
            Logger.exiting();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}