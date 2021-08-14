package com.xjh.startup.view;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OrderEraseView extends VBox {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderEraseView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        // 标题
        Label maxAmt = new Label("9元");
        maxAmt.setFont(Font.font(12));
        maxAmt.setTextFill(Color.RED);
        maxAmt.setPadding(new Insets(0, 0, 20, 0));
        HBox title = new HBox();
        title.setPrefWidth(300);
        title.setMaxWidth(300);
        title.getChildren().addAll(new Label("最大抹零金额:"), maxAmt);
        box.getChildren().add(title);

        // 金额
        HBox reasonLine = new HBox();
        reasonLine.setPrefWidth(300);
        reasonLine.setMaxWidth(300);
        TextField eraseAmt = new TextField();
        eraseAmt.setPromptText("抹零金额");
        box.getChildren().add(eraseAmt);
        reasonLine.getChildren().addAll(new Label("设置抹零金额:"), eraseAmt);
        reasonLine.setPadding(new Insets(0, 0, 20, 0));
        box.getChildren().add(reasonLine);
        // 退菜按钮
        Button returnBtn = new Button("确认");
        returnBtn.setOnMouseClicked(evt -> {
            String r = eraseAmt.getText();
            LogUtils.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "抹零金额:" + r);
            doOrderErase(param.getOrderId(), CommonUtils.parseDouble(r, 0D));
            this.getScene().getWindow().hide();
        });
        box.getChildren().add(returnBtn);
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
