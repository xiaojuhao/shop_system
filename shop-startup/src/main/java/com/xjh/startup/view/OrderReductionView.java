package com.xjh.startup.view;

import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SmallForm;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class OrderReductionView extends SmallForm {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);

    public OrderReductionView(DeskOrderParam param) {
        // 金额
        TextField reductionAmt = new TextField();
        reductionAmt.setPromptText("减免金额");
        addLine(newCenterLine(new Label("减免金额:"), reductionAmt));

        // 密码
        PasswordField pwd = new PasswordField();
        pwd.setPromptText("店长密码");
        addLine(newCenterLine(new Label("店长密码:"), pwd));
        // 退菜按钮
        Button cancel = new Button("取消");
        cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());
        Button okBtn = new Button("确认");
        okBtn.setOnMouseClicked(evt -> {
            String r = reductionAmt.getText();
            String password = pwd.getText();
            if (!storeService.checkManagerPwd(password)) {
                AlertBuilder.ERROR("店长密码不符");
                return;
            }
            Logger.info("订单:" + param.getOrderId() + ", 桌号:" + param.getDeskName() + "店长减免:" + r);
            Result<String> reductionRs = orderService.reduction(param.getOrderId(), CommonUtils.parseDouble(r, 0D));
            if (reductionRs.isSuccess()) {
                this.getScene().getWindow().hide();
            } else {
                AlertBuilder.ERROR(reductionRs.getMsg());
            }
        });
        addLine(newCenterLine(cancel, okBtn));
    }
}
