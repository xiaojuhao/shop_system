package com.xjh.startup.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
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

public class DeskController implements Initializable {
    static final String TIME_FORMAT = "HH:mm:ss";
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    static Holder<ScrollPane> instance = new Holder<>();

    public ScrollPane view() {
        ScrollPane s = new ScrollPane();
        instance.set(s);
        ObservableList<SimpleObjectProperty<Desk>> tables = FXCollections.observableArrayList();

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(600);
        s.setFitToWidth(true);
        s.setContent(pane);

        ThreadUtils.runInDaemon(() -> {
            // 加载所有的tables
            deskService.getAllDesks().forEach(desk -> tables.add(new SimpleObjectProperty<>(desk)));
            // 渲染tables
            tables.forEach(desk -> Platform.runLater(() -> render(desk, pane)));
            // 监测变化
            while (instance.get() == s) {
                tables.forEach(this::detectChange);
                CommonUtils.sleep(1000);
            }
            System.out.println("******* DeskController 循环退出." + Thread.currentThread().getName());
            System.gc();
        });
        return s;
    }

    void detectChange(SimpleObjectProperty<Desk> desk) {
        Desk dd = deskService.getById(desk.get().getId());
        if (dd == null) {
            return;
        }
        if (CommonUtils.ne(dd.getVerNo(), desk.get().getVerNo())
                || EnumDesKStatus.of(dd.getStatus()) == EnumDesKStatus.USED) {
            Platform.runLater(() -> desk.set(dd));
        }
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
        setBackground(vBox, status);
        Label statusLabel = new Label(status.remark());
        Label timeLabel = new Label();
        timeLabel.setText(DateBuilder.base(table.getOrderCreateTime()).format(TIME_FORMAT));
        Label tableName = new Label(table.getDeskName());
        tableName.setFont(new javafx.scene.text.Font("微软雅黑", 15));
        vBox.getChildren().addAll(tableName, statusLabel, timeLabel);

        desk.addListener((cc, _old, _new) -> {
            LocalDateTime ot = _new.getOrderCreateTime();
            EnumDesKStatus s = EnumDesKStatus.of(_new.getStatus());
            setBackground(vBox, s);
            statusLabel.setText(s.remark());
            long usedSeconds = DateBuilder.intervalSeconds(ot, LocalDateTime.now());
            if (s != EnumDesKStatus.FREE) {
                timeLabel.setText("已用" + showSeconds(usedSeconds));
            }
        });
        vBox.setOnMouseClicked(evt -> {
            Dialog<Integer> dialog = new Dialog<>();
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
            Desk runningData = deskService.getById(desk.get().getId());
            EnumDesKStatus runStatus = EnumDesKStatus.FREE;
            if (runningData != null) {
                runStatus = EnumDesKStatus.of(runningData.getStatus());
            }
            ButtonType openDesk = new ButtonType("开台", ButtonData.OK_DONE);
            ButtonType closeDesk = new ButtonType("关台", ButtonData.OK_DONE);
            if (runStatus == EnumDesKStatus.USED) {
                dialog.getDialogPane().getButtonTypes().addAll(closeDesk, ButtonType.CANCEL);
            } else {
                dialog.getDialogPane().getButtonTypes().addAll(openDesk, ButtonType.CANCEL);
            }
            dialog.setResultConverter(btn -> {
                if (openDesk == btn) {
                    return 1;
                } else if (closeDesk == btn) {
                    return 2;
                } else {
                    return 0;
                }
            });
            Optional<Integer> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == 1) {
                deskService.openDesk(table.getId());
            } else if (result.isPresent() && result.get() == 2) {
                deskService.closeDesk(table.getId());
            }

        });
        pane.getChildren().add(vBox);
    }

    private String showSeconds(long seconds) {
        if (seconds <= 60) {
            return padding(seconds) + "秒";
        }
        if (seconds < 3600) {
            return padding((seconds / 60))
                    + "分" + padding((seconds % 60)) + "秒";
        }
        seconds = seconds % (3600 * 24);
        return padding((seconds / 3600)) + "时"
                + padding(((seconds % 3600) / 60)) + "分"
                + padding((seconds % 60)) + "秒";
    }

    private String padding(long time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    private void setBackground(VBox vbox, EnumDesKStatus status) {
        if (status == EnumDesKStatus.USED || status == EnumDesKStatus.PAID) {
            vbox.setStyle("-fx-background-color: #ed6871;");
        } else {
            vbox.setStyle("-fx-background-color: #228B22;");
        }
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
