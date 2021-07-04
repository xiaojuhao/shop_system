package com.xjh.common.utils;

import javafx.scene.control.Alert;

public class AlertBuilder {
    public static void INFO(String msg) {
        INFO("消息通知", msg);
    }

    public static void INFO(String title, String msg) {
        Alert _alert = new Alert(Alert.AlertType.INFORMATION);
        _alert.setTitle(title);
        _alert.setHeaderText(msg);
        _alert.showAndWait();
    }

    public static void ERROR(String msg) {
        ERROR("告警信息", msg);
    }

    public static void ERROR(String title, String msg) {
        Alert _alert = new Alert(Alert.AlertType.ERROR);
        _alert.setTitle(title);
        _alert.setHeaderText(msg);
        _alert.showAndWait();
    }
}
