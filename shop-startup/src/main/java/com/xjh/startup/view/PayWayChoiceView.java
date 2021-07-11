package com.xjh.startup.view;

import java.util.Optional;

import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.scene.Node;
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

        pane.getChildren().add(cashButtonAction("现金结账", param));
        pane.getChildren().add(commonPaymentAction("银联POS机", param, EnumPayMethod.POS));
        pane.getChildren().add(commonPaymentAction("美团收单", param, EnumPayMethod.MEITUAN));
        pane.getChildren().add(commonPaymentAction("口碑收单", param, EnumPayMethod.KOUBEI));
        pane.getChildren().add(new Button("代金券结账"));
        pane.getChildren().add(new Button("充值卡结账"));
        pane.getChildren().add(new Button("逃单"));
        pane.getChildren().add(new Button("免单"));

        this.getChildren().add(pane);
    }

    private Node commonPaymentAction(String name, DeskOrderParam param, EnumPayMethod payMethod) {
        Button button = new Button(name);
        button.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentInfoInputDialog(param, payMethod).showAndWait();
            addPay(payResult.get());
        });
        return button;
    }

    private Node cashButtonAction(String name, DeskOrderParam param) {
        Button button = new Button(name);
        button.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentByCashDialog(param).showAndWait();
            addPay(payResult.get());
        });
        return button;
    }

    private void addPay(PaymentResult paymentResult) {
        Result<String> rs = orderPayService.handlePaymentResult(paymentResult);
        if (rs.isSuccess()) {
            this.getScene().getWindow().hide();
        } else {
            AlertBuilder.ERROR(rs.getMsg());
        }
    }
}
