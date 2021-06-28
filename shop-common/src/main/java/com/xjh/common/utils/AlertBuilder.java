package com.xjh.common.utils;

import javafx.scene.control.Alert;

public class AlertBuilder {
    public static Alert INFO(String title, String msg) {
        Alert _alert = new Alert(Alert.AlertType.INFORMATION);
        _alert.setTitle("通知消息");
        _alert.setHeaderText("添加购物车成功");
        return _alert;
    }

    public static Alert ERROR(String title, String msg) {
        Alert _alert = new Alert(Alert.AlertType.ERROR);
        _alert.setTitle("通知消息");
        _alert.setHeaderText("添加购物车成功");
        return _alert;
    }
}
