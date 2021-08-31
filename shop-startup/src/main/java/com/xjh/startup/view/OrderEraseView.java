package com.xjh.startup.view;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
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
            Logger.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "抹零金额:" + r);
            Result<String> rs = orderService.erase(param.getOrderId(), r);
            if (!rs.isSuccess()) {
                AlertBuilder.ERROR(rs.getMsg());
                return;
            }
            this.getScene().getWindow().hide();
        });
        addLine(newLine(cancel, ok));
    }
}
