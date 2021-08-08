package com.xjh.startup.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.startup.LoginApp;
import com.xjh.startup.view.DeskListView;
import com.xjh.startup.view.MenuBarView;
import com.xjh.startup.view.SysConfigView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable {
    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;
    @FXML
    ImageView wxImg;
    @FXML
    ImageView zfbImg;
    @FXML
    ImageView dingdingImg;

    public void login() throws Exception {
        if (!SysConfigView.checkConfig()) {
            AlertBuilder.ERROR("提示", "系统基础配置缺失，请先配置!");
            return;
        }
        String account = accountField.getText().trim();
        String password = passwordField.getText().trim();
        // 调用登录功能
        if ("1".equals(account)) {
            // 创建主界面舞台
            Stage mainStage = new Stage();
            //读入布局
            // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane main = new BorderPane();
            main.setTop(new MenuBarView(main).renderMenuBar());
            // 主体内容
            main.setCenter(new DeskListView().view());
            Scene scene = new Scene(main);
            scene.getStylesheets().add("/css/style.css");
            mainStage.setTitle("小句号点餐系统");
            Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
            double width = screenRectangle.getWidth();
            double height = screenRectangle.getHeight();
            mainStage.setWidth(width - 10);
            mainStage.setHeight(height - 10);
            //mainStage.setMaximized(true);
            mainStage.setScene(scene);
            mainStage.setOnHidden(evt -> LoginApp.server.get().stopQuietly());
            mainStage.show();
            Stage loginStage = (Stage) accountField.getScene().getWindow();
            loginStage.close();
        } else {
            AlertBuilder.ERROR("提示", "账号或密码错误，登录失败!");
        }
    }

    public void showConfig() {
        Stage cfgStg = new Stage();
        cfgStg.initOwner(accountField.getScene().getWindow());
        cfgStg.initModality(Modality.WINDOW_MODAL);
        cfgStg.initStyle(StageStyle.DECORATED);
        cfgStg.centerOnScreen();
        cfgStg.setWidth(600);
        cfgStg.setHeight(500);
        cfgStg.setTitle("系统配置");
        cfgStg.setScene(new Scene(new SysConfigView()));
        cfgStg.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountField.setText("1");
        passwordField.setText("1");
        wxImg.setImage(new Image("/img/weixin.png"));
        zfbImg.setImage(new Image("/img/zhifubao.png"));
        dingdingImg.setImage(new Image("/img/dingding.jpeg"));
    }
}
