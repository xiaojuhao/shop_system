package com.xjh.startup.view;

import com.alibaba.fastjson.JSON;
import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.paymethods.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.Optional;

import static com.xjh.common.enumeration.EnumPayMethod.*;

public class PayWayChoiceView extends SmallForm {
    OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);
    // OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    // DeskService deskService = GuiceContainer.getInstance(DeskService.class);

    public PayWayChoiceView(DeskOrderParam param) {
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPrefHeight(500);

        pane.getChildren().add(cashButtonAction("现金结账", param));
        pane.getChildren().add(commonPaymentAction("银联POS机", param, UNIONPAY_POS));
        pane.getChildren().add(commonPaymentAction("美团收单结账", param, BANKCARD));
        pane.getChildren().add(commonPaymentAction("口碑收单", param, KOUBEI));
        pane.getChildren().add(couponPaymentAction("美团代金券", param, 1, MEITUAN_COUPON));
        pane.getChildren().add(couponPaymentAction("美团套餐买单", param, 2, MEITUAN_PACKAGE));
        pane.getChildren().add(couponPaymentAction("假日套餐补差价结账", param, 4, WANDA_PACKAGE));
        pane.getChildren().add(voucherPaymentAction("代金券结账", param, VOUCHER));
        pane.getChildren().add(preCardPaymentAction("储值卡结账", param, STORECARD));
        // pane.getChildren().add(createButton("逃单", () -> handleEscapeOrder(param)));
        // pane.getChildren().add(createButton("免单", () -> handleFreeOrder(param)));

        this.getChildren().add(pane);
    }

    private Node commonPaymentAction(String name, DeskOrderParam param, EnumPayMethod payMethod) {
        Button button = new Button(name);
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentDialogOfCommon(param, payMethod).showAndWait();
            addPay(payResult.get());
        });
        return button;
    }

    private Node preCardPaymentAction(String name, DeskOrderParam param, EnumPayMethod payMethod) {
        Button button = new Button(name);
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setOnMouseClicked(event -> {
            PaymentDialogOfPreCard scene = new PaymentDialogOfPreCard(param, name, payMethod);
            ModelWindow window = new ModelWindow(this.getScene().getWindow());
            window.setHeight(300);
            window.setWidth(500);
            window.setScene(new Scene(scene));
            scene.initialize();
            window.showAndWait();
            System.out.println("返回值:" + JSON.toJSONString(window.getUserData()));
            addPay((PaymentResult) window.getUserData());
        });
        return button;
    }

    // 代金券支付
    private Node voucherPaymentAction(String name, DeskOrderParam param, EnumPayMethod payMethod) {
        Button button = new Button(name);
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setOnMouseClicked(event -> {
            PaymentDialogOfVoucher scene = new PaymentDialogOfVoucher(param, name, payMethod);
            ModelWindow window = new ModelWindow(this.getScene().getWindow());
            window.setHeight(300);
            window.setWidth(500);
            window.setScene(new Scene(scene));
            scene.initialize();
            window.showAndWait();
            System.out.println("返回值:" + JSON.toJSONString(window.getUserData()));
            addPay((PaymentResult) window.getUserData());
        });
        return button;
    }

    private Node couponPaymentAction(String name, DeskOrderParam param, int type, EnumPayMethod payMethod) {
        Button button = new Button(name);
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setOnMouseClicked(event -> {
            PaymentDialogOfCoupon scene = new PaymentDialogOfCoupon(param, type, name, payMethod);
            ModelWindow window = new ModelWindow(this.getScene().getWindow());
            window.setHeight(330);
            window.setScene(new Scene(scene));
            scene.initialize();
            window.showAndWait();
            System.out.println("返回值:" + JSON.toJSONString(window.getUserData()));
            addPay((PaymentResult) window.getUserData());
        });
        return button;
    }

    private Node cashButtonAction(String name, DeskOrderParam param) {
        Button button = new Button(name);
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentDialogOfCash(param).showAndWait();
            addPay(payResult.get());
        });
        return button;
    }

//    private Node createButton(String name, Runnable clickAction) {
//        Button button = new Button(name);
//        button.setMinWidth(100);
//        button.setMaxWidth(100);
//        button.setOnMouseClicked(evt -> clickAction.run());
//        return button;
//    }

//    private void handleEscapeOrder(DeskOrderParam param) {
//        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, "您确定要为当前订单做逃单处理吗？", new ButtonType("取消", ButtonBar.ButtonData.NO), new ButtonType("确定", ButtonBar.ButtonData.YES));
//        _alert.setTitle("逃单信息");
//        _alert.setHeaderText("逃单处理");
//        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
//            orderService.escapeOrder(param.getOrderId());
//            this.getScene().getWindow().hide();
//        }
//    }

//    private void handleFreeOrder(DeskOrderParam param) {
//        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, "您确定要为当前订单做免单处理吗？", new ButtonType("取消", ButtonBar.ButtonData.NO), new ButtonType("确定", ButtonBar.ButtonData.YES));
//        _alert.setTitle("免单信息");
//        _alert.setHeaderText("免单处理");
//        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
//            orderService.freeOrder(param.getOrderId());
//            this.getScene().getWindow().hide();
//        }
//    }

//    private void notSupportConfirm() {
//        AlertBuilder.ERROR("功能还未实现。。。");
//    }

    private void addPay(PaymentResult paymentResult) {
        // 取消按钮
        if (paymentResult.getPayAction() == EnumPayAction.CANCEL_PAY) {
            return;
        }
        if (CommonUtils.isNotBlank(paymentResult.getErrorMsg())) {
            AlertBuilder.ERROR(paymentResult.getErrorMsg());
            return;
        }
        if (paymentResult.getPayAmount() < 0.001) {
            AlertBuilder.ERROR("支付金额不能为0");
            return;
        }
        // 确认支付处理
        Result<String> rs = orderPayService.handlePaymentResult(paymentResult);
        if (rs.isSuccess()) {
            if (paymentResult.getPayMethod() == VOUCHER) {
                AlertBuilder.INFO("代金券支付成功");
            }
            this.getScene().getWindow().hide();
        } else {
            AlertBuilder.ERROR(rs.getMsg());
        }
    }
}
