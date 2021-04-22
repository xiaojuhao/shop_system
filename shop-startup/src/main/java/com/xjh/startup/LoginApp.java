package com.xjh.startup;

import com.xjh.common.utils.TimeRecord;
import com.xjh.startup.foundation.utils.FxmlUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TimeRecord timeRecord = TimeRecord.start();
        // main
        primaryStage.setTitle("登录系统");
        VBox root = FxmlUtils.load("login");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(0, 400, 0, 400));
        System.out.println("加载登录界面:" + timeRecord.getCostAndReset());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");
        primaryStage.setMaximized(true);
        // primaryStage.getIcons().add(new Image("/img/logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("主页面渲染:" + timeRecord.getCostAndReset());
    }

    public static void main(String[] args) {
        launch(args);
    }
}