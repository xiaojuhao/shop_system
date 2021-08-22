package com.xjh.startup.view;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.Order;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderReductionView extends SmallForm {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);

    public OrderReductionView(DeskOrderParam param) {
        // 金额
        TextField reductionAmt = new TextField();
        reductionAmt.setPromptText("减免金额");
        addLine(newLine(new Label("减免金额:"), reductionAmt));

        // 密码
        PasswordField pwd = new PasswordField();
        pwd.setPromptText("店长密码");
        addLine(newLine(new Label("店长密码:"), pwd));
        // 退菜按钮
        Button returnBtn = new Button("确认");
        returnBtn.setOnMouseClicked(evt -> {
            String r = reductionAmt.getText();
            String password = pwd.getText();
            if (!storeService.checkManagerPwd(password)) {
                AlertBuilder.ERROR("店长密码不符");
                return;
            }
            Logger.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "店长减免:" + r);
            doOrderReduction(param.getOrderId(), CommonUtils.parseDouble(r, 0D));
            this.getScene().getWindow().hide();
        });
        addLine(returnBtn);
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
