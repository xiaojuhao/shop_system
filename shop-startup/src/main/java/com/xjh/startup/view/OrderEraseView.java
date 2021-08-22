package com.xjh.startup.view;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.Order;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OrderEraseView extends SmallForm {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderEraseView(DeskOrderParam param) {
        // 标题
        Label maxAmt = new Label("9元");
        maxAmt.setFont(Font.font(12));
        maxAmt.setTextFill(Color.RED);
        maxAmt.setPrefWidth(200);
        addLine(newLine(createLabel("最大抹零金额:"), maxAmt));

        // 金额
        TextField eraseAmt = createTextField("抹零金额");
        addLine(newLine(createLabel("抹零金额:"), eraseAmt));
        // 退菜按钮
        Button cancel = new Button("取消");
        cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());
        Button ok = new Button("确认");
        ok.setOnMouseClicked(evt -> {
            double r = CommonUtils.parseDouble(eraseAmt.getText(), -1D);
            if (r < 0) {
                AlertBuilder.ERROR("抹零金额错误");
                return;
            }
            if (r >= 10) {
                AlertBuilder.ERROR("抹零金额不能大于10");
                return;
            }
            Logger.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "抹零金额:" + r);
            doOrderErase(param.getOrderId(), r);
            this.getScene().getWindow().hide();
        });
        addLine(newLine(cancel, ok));
    }

    private void doOrderErase(Integer orderId, double eraseAmt) {
        Order order = orderService.getOrder(orderId);
        if (order != null) {
            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderErase(eraseAmt);
            orderService.updateByOrderId(update);
        }
    }
}
