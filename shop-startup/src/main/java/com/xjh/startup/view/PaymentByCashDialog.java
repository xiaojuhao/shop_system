package com.xjh.startup.view;

import com.xjh.common.utils.CommonUtils;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PaymentByCashDialog extends Dialog<Integer> {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public PaymentByCashDialog(DeskOrderParam param) {
        this.setTitle("现金结账");
        this.setWidth(300);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        int row = 0;
        grid.add(new Label("桌号:"), 0, row);
        grid.add(new Label(param.getDeskName()), 1, row);

        row++;
        double pay = orderService.notPaidBillAmount(param.getOrderId());
        grid.add(new Label("待支付:"), 0, row);
        grid.add(new Label(CommonUtils.formatMoney(pay) + " 元"), 1, row);

        row++;
        TextField customerNumFiled = new TextField();
        customerNumFiled.setPromptText("支付金额");
        grid.add(new Label("支付金额:"), 0, row);
        grid.add(customerNumFiled, 1, row);

        row++;
        TextField remarkField = new TextField();
        remarkField.setPromptText("支付消息");
        grid.add(new Label("备注:"), 0, row);
        grid.add(remarkField, 1, row);

        this.getDialogPane().setContent(grid);
        ButtonType openDesk = new ButtonType("确认支付", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(openDesk, ButtonType.CANCEL);
        this.setResultConverter(btn -> 1);
    }
}
