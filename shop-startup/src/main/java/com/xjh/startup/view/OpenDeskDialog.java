package com.xjh.startup.view;

import com.xjh.common.enumeration.OpenDeskResult;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.startup.view.model.OpenDeskInputParam;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class OpenDeskDialog extends Dialog<OpenDeskInputParam> {
    public OpenDeskDialog(Desk table) {
        this.setTitle("开台");
        this.setWidth(300);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        // 桌号
        int row = 0;
        grid.add(new Label("桌号:"), 0, row);
        grid.add(new Label(table.getDeskName()), 1, row);

        // 就餐人数
        row++;
        grid.add(new Label("人数:"), 0, row);
        TextField custNum = new TextField();
        grid.add(custNum, 1, row);
        this.getDialogPane().setContent(grid);
        // 操作按钮
        ButtonType openDesk = new ButtonType("开台", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(openDesk, ButtonType.CANCEL);
        this.setResultConverter(btn -> {
            OpenDeskInputParam rs = new OpenDeskInputParam();
            rs.setCustomerNum(CommonUtils.parseInt(custNum.getText(), 0));
            rs.setResult(openDesk == btn ? OpenDeskResult.OPEN : OpenDeskResult.CANCEL);
            return rs;
        });
    }
}
