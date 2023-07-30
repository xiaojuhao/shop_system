package com.xjh.startup.view.paymethods;

import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.CommonUtils;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PaymentDialogOfCommon extends Dialog<PaymentResult> {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public PaymentDialogOfCommon(DeskOrderParam param, EnumPayMethod method) {
        this.setTitle(method.name);
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
        TextField cardNumFiled = new TextField();
        grid.add(new Label("交易编号:"), 0, row);
        grid.add(cardNumFiled, 1, row);

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
            result.setPayMethod(method);
            if (btn == confirmPayBtn) {
                result.setPayAction(EnumPayAction.DO_PAY);
                result.setPayAmount(CommonUtils.parseMoney(payAmountField.getText(), 0D));
                result.setPayRemark(remarkField.getText());
                result.setCardNumber(cardNumFiled.getText());
                if(CommonUtils.isBlank(result.getCardNumber())){
                    result.setErrorMsg("交易编号信息必输");
                }
            } else {
                result.setPayAction(EnumPayAction.CANCEL_PAY);
                result.setPayAmount(0D);
                result.setPayRemark(null);
            }
            return result;
        });
    }
}
