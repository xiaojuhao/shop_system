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
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import cn.hutool.core.lang.Holder;
import com.xjh.startup.view.OpenDeskDialog;
import com.xjh.startup.view.OrderDetail;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeskController implements Initializable {
    static final String TIME_FORMAT = "HH:mm:ss";
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
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
        Platform.runLater(() -> desk.set(dd));
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
            LocalDateTime ot = DateBuilder.base(_new.getOrderCreateTime()).dateTime();
            EnumDesKStatus s = EnumDesKStatus.of(_new.getStatus());
            setBackground(vBox, s);
            statusLabel.setText(s.remark());
            long usedSeconds = DateBuilder.intervalSeconds(ot, LocalDateTime.now());
            if (s != EnumDesKStatus.FREE) {
                timeLabel.setText("已用" + CommonUtils.formatSeconds(usedSeconds));
            }
        });
        vBox.setOnMouseClicked(evt -> {
            Desk runningData = deskService.getById(desk.get().getId());
            EnumDesKStatus runStatus = EnumDesKStatus.FREE;
            if (runningData != null) {
                runStatus = EnumDesKStatus.of(runningData.getStatus());
            }
            if (runStatus == EnumDesKStatus.USED) {
                Stage orderInfo = new Stage();
                orderInfo.initOwner(pane.getScene().getWindow());
                orderInfo.initModality(Modality.WINDOW_MODAL);
                orderInfo.initStyle(StageStyle.DECORATED);
                orderInfo.centerOnScreen();
                orderInfo.setWidth(pane.getScene().getWindow().getWidth() / 10 * 9);
                orderInfo.setHeight(pane.getScene().getWindow().getHeight() / 10 * 9);
                orderInfo.setTitle("订单详情");
                orderInfo.setScene(new Scene(new OrderDetail(runningData)));
                orderInfo.show();
            } else {
                Dialog<Integer> dialog = new OpenDeskDialog<>(desk.get());
                Optional<Integer> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == 1) {
                    deskService.openDesk(table.getId());
                } else if (result.isPresent() && result.get() == 2) {
                    deskService.closeDesk(table.getId());
                }
            }
        });
        pane.getChildren().add(vBox);
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
