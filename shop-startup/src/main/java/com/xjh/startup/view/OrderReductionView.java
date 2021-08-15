package com.xjh.startup.view;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderReductionView extends VBox {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderReductionView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        // 金额
        HBox reasonLine = new HBox();
        reasonLine.setPrefWidth(300);
        reasonLine.setMaxWidth(300);
        TextField eraseAmt = new TextField();
        eraseAmt.setPromptText("减免金额");
        reasonLine.getChildren().addAll(new Label("减免金额:"), eraseAmt);
        reasonLine.setPadding(new Insets(0, 0, 10, 0));
        box.getChildren().add(reasonLine);

        // 密码
        HBox pwdLine = new HBox();
        pwdLine.setPrefWidth(300);
        pwdLine.setMaxWidth(300);
        PasswordField pwd = new PasswordField();
        pwd.setPromptText("店长密码");
        pwdLine.getChildren().addAll(new Label("店长密码:"), pwd);
        pwdLine.setPadding(new Insets(0, 0, 20, 0));
        box.getChildren().add(pwdLine);
        // 退菜按钮
        Button returnBtn = new Button("确认");
        returnBtn.setOnMouseClicked(evt -> {
            String r = eraseAmt.getText();
            String password = pwd.getText();
            if (!CommonUtils.eq(password, "1234")) {
                AlertBuilder.ERROR("店长密码不符");
                return;
            }
            Logger.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "店长减免:" + r);
            doOrderReduction(param.getOrderId(), CommonUtils.parseDouble(r, 0D));
            this.getScene().getWindow().hide();
        });
        box.getChildren().add(returnBtn);
    }

    private void doOrderReduction(Integer orderId, double amt) {
        Order order = orderService.getOrder(orderId);
        if (order != null) {
            double bill = orderService.notPaidBillAmount(order);
            if (amt > bill) {
                AlertBuilder.ERROR("减免金额不能大于可支付金额");
                return;
            }
            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderReduction(amt);
            orderService.updateByOrderId(update);
        }
    }
}
