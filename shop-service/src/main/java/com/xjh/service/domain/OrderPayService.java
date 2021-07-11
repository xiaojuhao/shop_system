package com.xjh.service.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayStatus;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.service.domain.model.PaymentResult;

@Singleton
public class OrderPayService {
    @Inject
    OrderPayDAO orderPayDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    OrderService orderService;

    public Result<String> handlePaymentResult(PaymentResult paymentResult) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            // 取消支付
            if (paymentResult.getPayAction() == 0) {
                return Result.success("取消支付");
            }
            Order order = orderDAO.selectByOrderId(paymentResult.getOrderId());
            if (order == null) {
                return Result.fail("订单信息不存在:" + paymentResult.getOrderId());
            }
            double notPaidBillAmount = orderService.notPaidBillAmount(paymentResult.getOrderId());
            if (notPaidBillAmount < paymentResult.getPayAmount()) {
                return Result.fail("支付金额大于订单金额");
            }
            OrderPay orderPay = new OrderPay();
            orderPay.setOrderId(paymentResult.getOrderId());
            orderPay.setAccountId(0);
            orderPay.setAmount(paymentResult.getPayAmount());
            orderPay.setActualAmount(paymentResult.getPayAmount());
            orderPay.setCardNumber(paymentResult.getCardNumber());
            orderPay.setRemark(paymentResult.getPayRemark());
            orderPay.setPaymentMethod(paymentResult.getPayMethod().code);
            orderPay.setPaymentStatus(EnumPayStatus.PAID.code);
            orderPay.setCreatetime(DateBuilder.now().mills());
            int payRs = this.insert(orderPay);
            if (payRs == 0) {
                LogUtils.error("保存支付信息失败: insert error >> " + payRs);
                return Result.fail("保存支付明细失败");
            }
            //
            order.setOrderHadpaid(order.getOrderHadpaid() + orderPay.getAmount());
            if (Math.abs(notPaidBillAmount - paymentResult.getPayAmount()) <= 0.01) {
                order.setOrderStatus(EnumOrderStatus.PAID.status);
            } else {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            }
            Result<Integer> updateRs = orderService.updateByOrderId(order);
            if (!updateRs.isSuccess()) {
                LogUtils.error("更新订单支付结果失败: update error >> " + updateRs.getMsg());
                return Result.fail("更新订单支付结果失败," + updateRs.getMsg());
            }
            return Result.success("支付成功");
        } catch (Exception ex) {
            LogUtils.error("保存支付信息失败:" + ex.getMessage());
            return Result.fail("保存支付信息失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public int insert(OrderPay orderPay) throws SQLException {
        return orderPayDAO.insert(orderPay);
    }

    public List<OrderPay> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        return orderPayDAO.selectByOrderId(orderId);
    }

    public List<OrderPay> selectList(OrderPay example) {
        return orderPayDAO.selectList(example);
    }
}
