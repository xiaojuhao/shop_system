package com.xjh.startup.view;

import java.util.Optional;

import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PayWayChoiceView extends VBox {
    OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);

    public PayWayChoiceView(DeskOrderParam param) {
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefHeight(500);
        Button payByCash = new Button("现金结账");
        payByCash.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentByCashDialog(param).showAndWait();
            addPay(payResult.get());
        });
        pane.getChildren().add(payByCash);
        Button posBtn = new Button("银联POS机");
        posBtn.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentByPOSDialog(param).showAndWait();
            addPay(payResult.get());
        });
        pane.getChildren().add(posBtn);
        Button meituan = new Button("美团收单结账");
        meituan.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentByMeituanDialog(param).showAndWait();
            addPay(payResult.get());
        });
        pane.getChildren().add(meituan);
        pane.getChildren().add(new Button("代金券结账"));
        pane.getChildren().add(new Button("充值卡结账"));
        pane.getChildren().add(new Button("逃单"));
        pane.getChildren().add(new Button("免单"));

        this.getChildren().add(pane);
    }

    private void addPay(PaymentResult paymentResult) {
        orderPayService.addPay(paymentResult);
        this.getScene().getWindow().hide();
    }
}
