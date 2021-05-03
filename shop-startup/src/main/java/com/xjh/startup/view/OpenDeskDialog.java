package com.xjh.startup.view;

import com.xjh.dao.dataobject.Desk;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OpenDeskDialog extends Dialog<Integer> {
    public OpenDeskDialog(Desk table) {
        this.setTitle("开台");
        this.setWidth(300);
        // dialog.setHeaderText("Look, a Custom Login Dialog");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField custNum = new TextField();
        custNum.setPromptText("就餐人数");
        grid.add(new Label("桌号:"), 0, 0);
        grid.add(new Label(table.getDeskName()), 1, 0);
        grid.add(new Label("人数:"), 0, 1);
        grid.add(custNum, 1, 1);
        this.getDialogPane().setContent(grid);
        ButtonType openDesk = new ButtonType("开台", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeDesk = new ButtonType("关台", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(openDesk, ButtonType.CANCEL);
        this.setResultConverter(btn -> {
            if (openDesk == btn) {
                return 1;
            } else if (closeDesk == btn) {
                return 2;
            } else {
                return 0;
            }
        });
    }
}
