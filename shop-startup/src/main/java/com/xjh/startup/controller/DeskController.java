package com.xjh.startup.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.ResourceBundle;

import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.DeskService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import cn.hutool.core.lang.Holder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class DeskController implements Initializable {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    static Random random = new Random();

    static Holder<ScrollPane> holder = new Holder<>();

    public ScrollPane view() {
        ObservableList<SimpleObjectProperty<Desk>> tables = FXCollections.observableArrayList();

        ScrollPane s = new ScrollPane();
        holder.set(s);
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(600);
        s.setFitToWidth(true);
        s.setContent(pane);

        ThreadUtils.runInDaemon(() -> {
            deskService.getAllDesks().forEach(desk -> deskService.saveRunningData(desk));
            // 加载所有的tables
            deskService.getAllDesks().forEach(desk -> tables.add(new SimpleObjectProperty<>(desk)));
            // 渲染tables
            tables.forEach(desk -> Platform.runLater(() -> render(desk, pane)));
            // 监测变化
            while (holder.get() == s) {
                try {
                    Thread.sleep(1000);
                    tables.forEach(t -> {
                        Desk dd = deskService.getRunningData(t.get().getId());
                        if (dd != null && CommonUtils.ne(dd.getVerNo(), t.get().getVerNo())) {
                            Platform.runLater(() -> t.set(dd));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("************** DeskController 循环退出......."
                    + this + "\t\t" + Thread.currentThread().getName());
            System.gc();
        });
        // 随机更新table
        ThreadUtils.runInDaemon(() -> {
            while (holder.get() == s) {
                try {
                    Thread.sleep(1000);
                    Desk desk = deskService.getRunningData((long) random.nextInt(20));
                    if (desk != null) {
                        desk.setVerNo(desk.getVerNo() != null ? desk.getVerNo() + 1 : 1);
                        if (EnumDesKStatus.of(desk.getStatus()) == EnumDesKStatus.USED) {
                            desk.setStatus(EnumDesKStatus.FREE.status());
                            desk.setOrderCreateTime(null);
                        } else {
                            desk.setOrderCreateTime(LocalDateTime.now());
                            desk.setStatus(EnumDesKStatus.USED.status());
                        }
                        deskService.saveRunningData(desk);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return s;
    }

    void render(SimpleObjectProperty<Desk> desk, FlowPane pane) {
        Desk table = desk.get();
        double boxWidth = Math.max(pane.getWidth() / 6 - 15, 200);
        VBox vBox = new VBox();
        vBox.setPrefSize(boxWidth, boxWidth / 2);
        vBox.getStyleClass().add("desk");
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        EnumDesKStatus status = EnumDesKStatus.of(table.getStatus());
        if (status == EnumDesKStatus.USED || status == EnumDesKStatus.PAID) {
            vBox.setStyle("-fx-background-color: #ed6871;");
        } else {
            vBox.setStyle("-fx-background-color: #228B22;");
        }
        Label statusLabel = new Label(status.remark());
        Label timeLabel = new Label();
        timeLabel.setText(DateBuilder.base(table.getOrderCreateTime()).format("HH:mm:ss"));
        desk.addListener((cc, old, newV) -> {
            LocalDateTime orderTime = newV.getOrderCreateTime();
            EnumDesKStatus s = EnumDesKStatus.of(newV.getStatus());
            Platform.runLater(() -> statusLabel.setText(s.remark()));
            if (s == EnumDesKStatus.USED || s == EnumDesKStatus.PAID) {
                vBox.setStyle("-fx-background-color: #ed6871;");
            } else {
                vBox.setStyle("-fx-background-color: #228B22;");
            }
            if (CommonUtils.ne(old.getOrderCreateTime(), orderTime)) {
                Platform.runLater(() ->
                        timeLabel.setText(DateBuilder.base(orderTime).format("HH:mm:ss")));
            }
        });

        Label tableNameLabel = new Label(table.getDeskName());
        tableNameLabel.setFont(new javafx.scene.text.Font("微软雅黑", 15));
        vBox.getChildren().addAll(tableNameLabel, statusLabel, timeLabel);
        vBox.setOnMouseClicked(evt -> {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Login Dialog");
            dialog.setHeaderText("Look, a Custom Login Dialog");
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            username.setText("作者：" + table.getDeskName());
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);
            dialog.getDialogPane().setContent(grid);
            ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            dialog.showAndWait();

        });
        pane.getChildren().add(vBox);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("DeskController被销毁了。。。。。。。。");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("****** " + this.getClass().getName() + " initialize.......");
    }
}
