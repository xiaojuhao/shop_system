package com.xjh.startup.view;

import java.time.LocalDateTime;
import java.util.Optional;

import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.model.OpenDeskParam;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.OpenDeskInputParam;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeskView extends VBox {
    static final String TIME_FORMAT = "HH:mm:ss";

    public DeskView(SimpleObjectProperty<Desk> desk, double prefWidth) {
        Desk table = desk.get();
        this.setPrefSize(prefWidth, prefWidth / 2);
        this.getStyleClass().add("desk");
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);

        EnumDesKStatus status = EnumDesKStatus.of(table.getStatus());
        setBackground(this, status);
        Label statusLabel = new Label(status.remark());
        Label timeLabel = new Label();
        timeLabel.setText(DateBuilder.base(table.getOrderCreateTime()).format(TIME_FORMAT));
        Label tableName = new Label(table.getDeskName());
        tableName.setFont(new javafx.scene.text.Font("微软雅黑", 15));
        this.getChildren().addAll(tableName, statusLabel, timeLabel);

        desk.addListener((cc, _old, _new) -> {
            LocalDateTime ot = DateBuilder.base(_new.getOrderCreateTime()).dateTime();
            EnumDesKStatus s = EnumDesKStatus.of(_new.getStatus());
            setBackground(this, s);
            statusLabel.setText(s.remark());
            long usedSeconds = DateBuilder.intervalSeconds(ot, LocalDateTime.now());
            if (s != EnumDesKStatus.FREE) {
                timeLabel.setText("已用" + CommonUtils.formatSeconds(usedSeconds));
            }
        });

        this.setOnMouseClicked(evt -> {
            DeskService deskService = GuiceContainer.getInstance(DeskService.class);
            Desk runningData = deskService.getById(desk.get().getDeskId());
            EnumDesKStatus runStatus = EnumDesKStatus.FREE;
            if (runningData != null) {
                runStatus = EnumDesKStatus.of(runningData.getStatus());
            }
            if (runStatus == EnumDesKStatus.USED) {
                Stage orderInfo = new Stage();
                orderInfo.initOwner(this.getScene().getWindow());
                orderInfo.initModality(Modality.WINDOW_MODAL);
                orderInfo.initStyle(StageStyle.DECORATED);
                orderInfo.centerOnScreen();
                orderInfo.setWidth(this.getScene().getWindow().getWidth() / 10 * 9);
                orderInfo.setHeight(this.getScene().getWindow().getHeight() / 10 * 9);
                orderInfo.setTitle("订单详情");
                orderInfo.setScene(new Scene(new OrderDetail(runningData)));
                orderInfo.show();
                System.gc();
            } else {
                OpenDeskDialog dialog = new OpenDeskDialog(desk.get());
                Optional<OpenDeskInputParam> result = dialog.showAndWait();
                if (result.isPresent() && result.get().getResult() == 1) {
                    if (result.get().getCustomerNum() <= 0) {
                        AlertBuilder.ERROR("请输入就餐人数");
                        return;
                    }
                    OpenDeskParam openDeskParam = new OpenDeskParam();
                    openDeskParam.setDeskId(desk.get().getDeskId());
                    openDeskParam.setCustomerNum(result.get().getCustomerNum());
                    deskService.openDesk(openDeskParam);
                }
            }
        });
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
        System.out.println("DeskVeiew    销毁了。。。。。。。");
    }
}
