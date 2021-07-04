package com.xjh.startup.view;

import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PayWayChoiceView extends VBox {
    public PayWayChoiceView(DeskOrderParam param) {
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefHeight(500);
        Button payByCash = new Button("现金结账");
        payByCash.setOnMouseClicked(event -> {
            new PaymentByCashDialog(param).showAndWait();

        });
        pane.getChildren().add(payByCash);
        pane.getChildren().add(new Button("银联POS机"));
        pane.getChildren().add(new Button("美团收单结账"));
        pane.getChildren().add(new Button("代金券结账"));
        pane.getChildren().add(new Button("充值卡结账"));
        pane.getChildren().add(new Button("逃单"));
        pane.getChildren().add(new Button("免单"));

        this.getChildren().add(pane);
    }
}
