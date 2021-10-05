package com.xjh.startup.view.base;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class OkCancelDialog extends Dialog<ButtonType> {
    public OkCancelDialog(String title, String notify) {
        this.setTitle(title);
        this.getDialogPane().setContent(new Label(notify));
        ButtonType confirmPayBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(confirmPayBtn, ButtonType.CANCEL);
    }
}
