package com.xjh.startup;

import com.xjh.common.utils.TimeRecord;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Time;

public class LoginApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TimeRecord timeRecord = TimeRecord.start();
        // main
        primaryStage.setTitle("登录系统");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

        Parent root = fxmlLoader.load();
        System.out.println("加载登录界面:" + timeRecord.getCostAndRestart());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");
        primaryStage.setMaximized(true);
        // primaryStage.getIcons().add(new Image("/img/logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("主页面渲染:" + timeRecord.getCostAndClear());
    }

    public static void main(String[] args) {
        launch(args);
    }
}