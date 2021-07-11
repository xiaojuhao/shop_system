package com.xjh.startup.view;

import java.util.Optional;

import com.xjh.common.enumeration.EnumOrderServeStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PayWayChoiceView extends VBox {
    OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

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
        Button escape = new Button("逃单");
        escape.setOnMouseClicked(evt -> handleEscape(param));
        pane.getChildren().add(escape);
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

    private void handleEscape(DeskOrderParam param) {
        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION,
                "您确定要为当前订单做逃单处理吗？",
                new ButtonType("取消", ButtonBar.ButtonData.NO),
                new ButtonType("确定", ButtonBar.ButtonData.YES));
        _alert.setTitle("逃单信息");
        _alert.setHeaderText("逃单处理");
        Optional<ButtonType> _buttonType = _alert.showAndWait();
        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            Order order = orderService.getOrder(param.getOrderId());
            // 更新订单状态
            Order updateOrder = new Order();
            updateOrder.setOrderId(order.getOrderId());
            updateOrder.setOrderStatus(EnumOrderStatus.ESCAPE.status);
            updateOrder.setStatus(EnumOrderServeStatus.END.status);
            Result<Integer> updateOrderRs = orderService.updateByOrderId(updateOrder);
            if (!updateOrderRs.isSuccess()) {
                AlertBuilder.ERROR(updateOrderRs.getMsg());
                return;
            }
            // 关台
            Result<String> closeDeskRs = deskService.closeDesk(param.getDeskId());
            if (!closeDeskRs.isSuccess()) {
                AlertBuilder.ERROR(closeDeskRs.getMsg());
                return;
            }
            this.getScene().getWindow().hide();
        }
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
