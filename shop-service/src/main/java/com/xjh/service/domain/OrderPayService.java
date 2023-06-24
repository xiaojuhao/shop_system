package com.xjh.service.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayStatus;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.dao.query.OrderPayQuery;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.ws.NotifyService;

@Singleton
public class OrderPayService {
    @Inject
    OrderPayDAO orderPayDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    OrderService orderService;
    @Inject
    DeskService deskService;

    public Result<String> handlePaymentResult(PaymentResult paymentResult) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            // 取消支付
            if (paymentResult.getPayAction() == 0) {
                return Result.success("取消支付");
            }
            Result<Order> orderRs = orderDAO.selectByOrderId(paymentResult.getOrderId());
            if (!orderRs.isSuccess()) {
                return Result.fail("订单信息不存在:" + paymentResult.getOrderId());
            }
            Order order = orderRs.getData();
            double notPaidBillAmount = orderService.notPaidBillAmount(order);
            if (Math.abs(notPaidBillAmount - paymentResult.getPayAmount()) < 0.009) {
                paymentResult.setPayAmount(notPaidBillAmount);
            }
            if (notPaidBillAmount < paymentResult.getPayAmount()) {
                return Result.fail("支付金额大于订单金额");
            }
            OrderPay orderPay = new OrderPay();
            orderPay.setOrderId(paymentResult.getOrderId());
            orderPay.setAccountId(CurrentAccount.currentAccountId());
            orderPay.setAmount(paymentResult.getPayAmount());
            orderPay.setActualAmount(paymentResult.getActualAmount());
            orderPay.setVoucherNums(paymentResult.getVoucherNum());
            orderPay.setCardNumber(paymentResult.getCardNumber());
            orderPay.setRemark(paymentResult.getPayRemark());
            orderPay.setPaymentMethod(paymentResult.getPayMethod().code);
            orderPay.setPaymentStatus(EnumPayStatus.PAID.code);
            orderPay.setCreatetime(DateBuilder.now().mills());
            int payRs = this.insert(orderPay);
            if (payRs == 0) {
                Logger.error("保存支付信息失败: insert error >> " + payRs);
                return Result.fail("保存支付明细失败");
            }
            // 订单状态
            order.setOrderHadpaid(order.getOrderHadpaid() + orderPay.getAmount());
            if (Math.abs(notPaidBillAmount - paymentResult.getPayAmount()) <= 0.01) {
                order.setOrderStatus(EnumOrderStatus.PAID.status);
            } else {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            }
            Result<Integer> updateRs = orderService.updateByOrderId(order);
            if (!updateRs.isSuccess()) {
                Logger.error("更新订单支付结果失败: update error >> " + updateRs.getMsg());
                return Result.fail("更新订单支付结果失败," + updateRs.getMsg());
            }
            // 餐桌状态
            if (EnumOrderStatus.of(order.getOrderStatus()) == EnumOrderStatus.PAID) {
                Desk updateDesk = new Desk();
                updateDesk.setStatus(EnumDeskStatus.PAID.status());
                updateDesk.setDeskId(order.getDeskId());
                deskService.updateDeskByDeskId(updateDesk);
            }

            NotifyService.checkOutResult(order.getDeskId(), order.getOrderStatus(), orderPay.getAmount());
            return Result.success("支付成功");
        } catch (Exception ex) {
            Logger.error("保存支付信息失败:" + ex.getMessage());
            return Result.fail("保存支付信息失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public int insert(OrderPay orderPay) throws SQLException {
        return orderPayDAO.insert(orderPay);
    }

    public Result<Integer> deleteBy(OrderPayQuery query) {
        try {
            return Result.success(orderPayDAO.deleteBy(query));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
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
