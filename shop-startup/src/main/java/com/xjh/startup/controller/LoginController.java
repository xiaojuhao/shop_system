package com.xjh.startup.controller;

import com.xjh.service.domain.AdminService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;

    AdminService adminService = GuiceContainer.getInstance(AdminService.class);

    public void login() throws Exception {
        String account = accountField.getText().trim();
        String password = passwordField.getText().trim();
        //调用登录功能
        if (adminService.login(account, password)) {
            //创建主界面舞台
            Stage mainStage = new Stage();
            //读入布局
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane root = fxmlLoader.load();
            // 菜单
            root.setTop(new MenuBarController(root).renderMenuBar());
            // 主体内容
            root.setCenter(new DeskController().view());
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/style.css");
            mainStage.setTitle("小句号点餐系统");
            mainStage.setWidth(800);
            mainStage.setHeight(600);
            mainStage.setMaximized(true);
            mainStage.setScene(scene);
            //            mainStage.getIcons().add(new Image("/img/logo.png"));
            mainStage.show();
            //将这个管理员信息传给主控制器

            //            Admin admin = adminService.getAdminByAccount(account);
            //            MainController mainController = fxmlLoader.getController();
            //            mainController.setAdmin(admin);
            Stage loginStage = (Stage) accountField.getScene().getWindow();
            loginStage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setContentText("账号或密码错误，登录失败!");
            alert.showAndWait();
        }
    }
}
