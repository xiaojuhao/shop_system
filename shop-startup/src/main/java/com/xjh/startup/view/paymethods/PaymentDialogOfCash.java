package com.xjh.startup.view.paymethods;

import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.CommonUtils;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PaymentDialogOfCash extends Dialog<PaymentResult> {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public PaymentDialogOfCash(DeskOrderParam param) {
        this.setTitle("现金结账");
        this.setWidth(300);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        int row = 0;
        grid.add(new Label("桌号:"), 0, row);
        grid.add(new Label(param.getDeskName()), 1, row);

        row++;
        double pay = orderService.notPaidBillAmount(param.getOrderId());
        grid.add(new Label("待支付:"), 0, row);
        grid.add(new Label(CommonUtils.formatMoney(pay) + " 元"), 1, row);

        row++;
        TextField payAmountField = new TextField();
        payAmountField.setPromptText("支付金额");
        payAmountField.setText(CommonUtils.formatMoney(pay));
        grid.add(new Label("支付金额:"), 0, row);
        grid.add(payAmountField, 1, row);

        row++;
        TextField remarkField = new TextField();
        remarkField.setPromptText("支付消息");
        grid.add(new Label("备注:"), 0, row);
        grid.add(remarkField, 1, row);

        this.getDialogPane().setContent(grid);
        ButtonType confirmPayBtn = new ButtonType("确认支付", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(confirmPayBtn, ButtonType.CANCEL);
        this.setResultConverter(btn -> {
            PaymentResult result = new PaymentResult();
            result.setOrderId(param.getOrderId());
            result.setPayMethod(EnumPayMethod.CASH);
            if (btn == confirmPayBtn) {
                result.setPayAction(EnumPayAction.DO_PAY);
                result.setPayAmount(CommonUtils.parseMoney(payAmountField.getText(), 0D));
                result.setPayRemark(remarkField.getText());
            } else {
                result.setPayAction(EnumPayAction.CANCEL_PAY);
                result.setPayAmount(0D);
                result.setPayRemark(null);
            }
            return result;
        });
    }
}
