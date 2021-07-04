package com.xjh.startup.view;

import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.startup.view.model.OpenDeskInputParam;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OpenDeskDialog extends Dialog<OpenDeskInputParam> {
    public OpenDeskDialog(Desk table) {
        this.setTitle("开台");
        this.setWidth(300);
        // dialog.setHeaderText("Look, a Custom Login Dialog");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField customerNumFiled = new TextField();
        customerNumFiled.setPromptText("就餐人数");
        grid.add(new Label("桌号:"), 0, 0);
        grid.add(new Label(table.getDeskName()), 1, 0);
        grid.add(new Label("人数:"), 0, 1);
        grid.add(customerNumFiled, 1, 1);
        this.getDialogPane().setContent(grid);
        ButtonType openDesk = new ButtonType("开台", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeDesk = new ButtonType("关台", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(openDesk, ButtonType.CANCEL);
        this.setResultConverter(btn -> {
            OpenDeskInputParam rs = new OpenDeskInputParam();
            rs.setCustomerNum(CommonUtils.parseInt(customerNumFiled.getText(), 0));
            if (openDesk == btn) {
                rs.setResult(1);
            } else if (closeDesk == btn) {
                rs.setResult(2);
            } else {
                rs.setResult(0);
            }
            return rs;
        });
    }
}
