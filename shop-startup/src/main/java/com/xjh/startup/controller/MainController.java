package com.xjh.startup.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private StackPane mainContainer;

    //    @FXML
    //    private Label timeLabel;
    //    @FXML
    //    private ImageView adminAvatar;
    //    @FXML
    //    private Label adminName;

    //    private Admin admin;
    //
    //    public void setAdmin(Admin admin) {
    //        this.admin = admin;
    //    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //开启一个UI线程 ,将登录界面传过来的管理员信息显示在主界面的右上角
        //        Platform.runLater(new Runnable() {
        //            @Override
        //            public void run() {
        //                Image image = new Image(admin.getAvatar());
        //                adminAvatar.setImage(image);
        //                //将头像显示为圆形
        //                Circle circle = new Circle();
        //                circle.setCenterX(20.0);
        //                circle.setCenterY(20.0);
        //                circle.setRadius(20.0);
        //                adminAvatar.setClip(circle);
        //                //显示管理员姓名
        //                adminName.setText(admin.getName());
        //            }
        //        });
        //启一个线程，用来同步获取系统时间
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                while (true) {
        //                    //获取系统当前时间
        //                    LocalDateTime now = LocalDateTime.now();
        //                    //格式化时间
        //                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        //                    String timeString = dateTimeFormatter.format(now);
        //                    //启一个UI线程
        //                    Platform.runLater(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            //将格式化后的日期时间显示在标签上
        //                            timeLabel.setText(timeString);
        //                        }
        //                    });
        //                    try {
        //                        Thread.sleep(1000);
        //                    } catch (InterruptedException e) {
        //                        System.err.println("中断异常");
        //                    }
        //                }
        //            }
        //        }).start();

        //        try {
        //            AnchorPane anchorPane = new FXMLLoader(getClass().getResource("/fxml/default.fxml")).load();
        //            mainContainer.getChildren().add(anchorPane);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
    }


    public void listDefault() throws Exception {
        switchView("default.fxml");
    }

    public void listType() throws Exception {
        switchView("type.fxml");
    }

    public void listTypeAnalysis() throws Exception {
        switchView("type_analysis.fxml");
    }

    public void listBook() throws Exception {
        switchView("book.fxml");
    }

    public void viewBook() throws Exception {
        switchView("view_book.fxml");
    }

    public void listBookAnalysis() throws Exception {
        switchView("book_analysis.fxml");
    }

    public void listAdmin() throws Exception {
        switchView("admin.fxml");
    }

    public void listReader() throws Exception {
        switchView("reader.fxml");
    }

    public void listReaderAnalysis() throws Exception {
        switchView("reader_analysis.fxml");
    }

    public void listPersonal() throws Exception {
        //        mainContainer.getChildren().clear();
        //        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/personal.fxml"));
        //        AnchorPane anchorPane = fxmlLoader.load();
        //        mainContainer.getChildren().add(anchorPane);
        //        PersonalController personalController = fxmlLoader.getController();
        //        personalController.setAdmin(admin);
    }

    private void switchView(String fileName) throws Exception {
        //清空原有内容
        mainContainer.getChildren().clear();
        AnchorPane anchorPane = new FXMLLoader(getClass().getResource("/fxml/" + fileName)).load();
        mainContainer.getChildren().add(anchorPane);
    }

    //退出系统
    public void logout() throws Exception {
        //关闭主界面
        Stage mainStage = (Stage) mainContainer.getScene().getWindow();
        mainStage.close();
        //弹出登录界面
        Stage loginStage = new Stage();
        loginStage.setTitle("Admin Login");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");
        loginStage.setMaximized(true);
        loginStage.getIcons().add(new Image("/img/logo.png"));
        loginStage.setScene(scene);
        loginStage.show();
    }
}
