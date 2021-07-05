package com.xjh.startup.view;

import java.util.Optional;

import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.PaymentResult;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PayWayChoiceView extends VBox {
    public PayWayChoiceView(DeskOrderParam param) {
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefHeight(500);
        Button payByCash = new Button("现金结账");
        payByCash.setOnMouseClicked(event -> {
            Optional<PaymentResult> payResult = new PaymentByCashDialog(param).showAndWait();
            addPay(param, payResult.get());
        });
        pane.getChildren().add(payByCash);
        pane.getChildren().add(new Button("银联POS机"));
        pane.getChildren().add(new Button("美团收单结账"));
        pane.getChildren().add(new Button("代金券结账"));
        pane.getChildren().add(new Button("充值卡结账"));
        pane.getChildren().add(new Button("逃单"));
        pane.getChildren().add(new Button("免单"));

        this.getChildren().add(pane);
    }

    private void addPay(DeskOrderParam param, PaymentResult paymentResult) {
        try {
            // 取消支付
            if (paymentResult.getPayAction() == 0) {
                return;
            }
            OrderDAO orderDAO = GuiceContainer.getInstance(OrderDAO.class);
            Order order = orderDAO.selectByOrderId(param.getOrderId());
            OrderService orderService = GuiceContainer.getInstance(OrderService.class);
            if (order == null) {
                AlertBuilder.ERROR("订单信息不存在:" + param.getOrderId());
                return;
            }
            double notPaidBillAmount = orderService.notPaidBillAmount(param.getOrderId());
            if (notPaidBillAmount <= paymentResult.getPayAmount()) {
                AlertBuilder.ERROR("支付金额大于订单金额");
                return;
            }
            OrderPayDAO orderPayDAO = GuiceContainer.getInstance(OrderPayDAO.class);
            OrderPay orderPay = new OrderPay();
            orderPay.setOrderId(param.getOrderId());
            orderPay.setAccountId(0);
            orderPay.setAmount(paymentResult.getPayAmount());
            orderPay.setActualAmount(paymentResult.getPayAmount());
            orderPay.setCardNumber(paymentResult.getCardNumber());
            orderPay.setRemark(paymentResult.getPayRemark());
            orderPay.setPaymentMethod(paymentResult.getPayMethod().code);
            orderPay.setPaymentStatus(EnumPayStatus.PAID.code);
            orderPay.setCreatetime(DateBuilder.now().mills());
            int payRs = orderPayDAO.insert(orderPay);
            if (payRs == 0) {
                LogUtils.error("保存支付信息失败: insert error >> " + payRs);
                AlertBuilder.ERROR("保存支付明细失败");
                return;
            }
            //
            order.setOrderHadpaid(order.getOrderHadpaid() + orderPay.getAmount());
            if (Math.abs(notPaidBillAmount - paymentResult.getPayAmount()) <= 0.01) {
                order.setOrderStatus(EnumOrderStatus.PAID.status);
            } else {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            }
            int updateRs = orderDAO.updateByOrderId(order);
            if (updateRs == 0) {
                LogUtils.error("更新订单支付结果失败: update error >> " + payRs);
                AlertBuilder.ERROR("更新订单支付结果失败");
            }
        } catch (Exception ex) {
            LogUtils.error("保存支付信息失败:" + ex.getMessage());
            AlertBuilder.ERROR("保存支付信息失败:" + ex.getMessage());
        }
    }
}
