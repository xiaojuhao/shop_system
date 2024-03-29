package com.xjh.startup.view;

import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SmallForm;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OrderRepayView extends SmallForm {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderRepayView(DeskOrderParam param) {
        this.setSpacing(20);
        // 标题
        Label confirmText = new Label("确定重新结账？");
        confirmText.setFont(Font.font(16));
        confirmText.setPrefWidth(200);
        addLine(newCenterLine(confirmText));

        // 金额
        TextField pwd = createTextField("店长密码");
        addLine(newCenterLine(createLabel("店长密码:"), pwd));
        // 退菜按钮
        Button cancel = new Button("取 消");
        cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());
        Button ok = new Button("确 认");
        ok.setOnMouseClicked(evt -> {
            Result<String> rs = orderService.repayOrder(param.getOrderId(), pwd.getText());
            if (!rs.isSuccess()) {
                AlertBuilder.ERROR(rs.getMsg());
                return;
            }
            this.getScene().getWindow().hide();
        });
        HBox buttonLine = newCenterLine(cancel, ok);
        buttonLine.setSpacing(30);
        addLine(buttonLine);

        Label remark = new Label("注: 微信，支付宝，微信余额，微信优惠券，\n代金券，储值卡，微生活代金券，微生活\n积分抵扣，微生活储值卡，微信银联支付\n不可以重新结账");
        remark.setTextFill(Color.RED);
        addLine(remark);
    }
}
