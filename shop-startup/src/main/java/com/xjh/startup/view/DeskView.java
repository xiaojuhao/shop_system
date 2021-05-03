package com.xjh.startup.view;

import java.time.LocalDateTime;

import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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
    }

    private void setBackground(VBox vbox, EnumDesKStatus status) {
        if (status == EnumDesKStatus.USED || status == EnumDesKStatus.PAID) {
            vbox.setStyle("-fx-background-color: #ed6871;");
        } else {
            vbox.setStyle("-fx-background-color: #228B22;");
        }
    }
}
