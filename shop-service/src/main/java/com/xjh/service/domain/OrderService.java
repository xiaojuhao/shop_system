package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderServeStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumOrderType;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.OrderDiscount;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.service.domain.model.CreateOrderParam;
import com.xjh.service.domain.model.OrderBillVO;

import cn.hutool.core.codec.Base64;

@Singleton
public class OrderService {
    @Inject
    OrderDAO orderDAO;
    @Inject
    SubOrderDAO subOrderDAO;
    @Inject
    OrderDishesService orderDishesService;
    @Inject
    OrderPayService orderPayService;

    public Order getOrder(Integer orderId) {
        try {
            if (orderId == null) {
                return null;
            }
            return orderDAO.selectByOrderId(orderId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Result<Integer> updateByOrderId(Order order) {
        if (order == null || order.getOrderId() == null) {
            return Result.fail("更新订单失败");
        }
        try {
            int u = orderDAO.updateByOrderId(order);
            if (u > 0) {
                return Result.success(1);
            } else {
                return Result.fail("保存订单失败, 更新记录0");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<OrderBillVO> calcOrderBill(Order order, List<OrderDishes> orderDishesList) {
        if (order != null) {
            OrderBillVO v = new OrderBillVO();
            v.orderId = order.getOrderId().toString();
            v.customerNum = order.getOrderCustomerNums();
            v.orderTime = DateBuilder.base(order.getCreateTime()).timeStr();
            v.orderNeedPay = this.notPaidBillAmount(order, orderDishesList);
            v.orderHadpaid = order.getOrderHadpaid();
            v.totalPrice = sumTotalPrice(order, orderDishesList);
            v.discountAmount = calcDiscountAmount(order, orderDishesList);
            v.payStatusName = EnumOrderStatus.of(order.getOrderStatus()).remark;
            v.deduction = order.getFullReduceDishesPrice();
            v.returnAmount = this.sumReturnAmount(orderDishesList);
            v.orderErase = CommonUtils.orElse(order.getOrderErase(), 0D);
            v.orderReduction = CommonUtils.orElse(order.getOrderReduction(), 0D);
            // 支付信息
            List<OrderPay> pays = orderPayService.selectByOrderId(order.getOrderId());
            StringBuilder payInfo = new StringBuilder();
            CommonUtils.forEach(pays, p -> {
                payInfo.append(DateBuilder.base(p.getCreatetime()).timeStr())
                        .append(" 收到付款:")
                        .append(CommonUtils.formatMoney(p.getAmount()))
                        .append(", 来自")
                        .append(EnumPayMethod.of(p.getPaymentMethod()).name);
                if (CommonUtils.isNotBlank(p.getCardNumber())) {
                    payInfo.append(",交易号:").append(p.getCardNumber());
                }
                payInfo.append("\r\n");
            });
            v.payInfoRemark = payInfo.toString();
            return Result.success(v);
        }
        return Result.fail("订单不存在");
    }

    private double sumTotalPrice(Order order, List<OrderDishes> orderDishesList) {
        return CommonUtils.collect(orderDishesList, OrderDishes::getOrderDishesPrice)
                .stream().filter(Objects::nonNull)
                .reduce(0D, Double::sum);
    }

    private double calcDiscountAmount(Order order, List<OrderDishes> orderDishesList) {
        double total = CommonUtils.collect(orderDishesList, OrderDishes::getOrderDishesPrice)
                .stream().filter(Objects::nonNull)
                .reduce(0D, Double::sum);
        double discountedPrice = CommonUtils.collect(orderDishesList, OrderDishes::getOrderDishesDiscountPrice)
                .stream().filter(Objects::nonNull)
                .reduce(0D, Double::sum);
        return Math.max(0, total - discountedPrice);
    }

    public Result<Integer> countSubOrder(Integer orderId) {
        try {
            return Result.success(subOrderDAO.countSubOrders(orderId));
        } catch (Exception ex) {
            LogUtils.error("countSubOrder ::" + ex.getMessage());
            return Result.fail(ex.getMessage());
        }
    }

    public Result<LocalDateTime> firstSubOrderTime(Integer orderId) {
        try {
            Long mills = subOrderDAO.firsSubOrderTime(orderId);
            if (mills != null) {
                return Result.success(DateBuilder.base(mills).dateTime());
            } else {
                return Result.success(null);
            }
        } catch (Exception ex) {
            LogUtils.info("查询数据失败" + ex.getMessage());
            return Result.fail("查询数据失败" + ex.getMessage());
        }
    }

    public Result<List<SubOrder>> findSubOrders(Integer orderId) {
        try {
            if (orderId == null) {
                return Result.success(new ArrayList<>());
            }
            SubOrder cond = new SubOrder();
            cond.setOrderId(orderId);
            List<SubOrder> subOrders = subOrderDAO.selectList(cond);
            if (CommonUtils.isEmpty(subOrders)) {
                return Result.success(new ArrayList<>());
            }
            return Result.success(subOrders);
        } catch (Exception ex) {
            LogUtils.error("查询子订单异常:" + ex.getMessage());
            // AlertBuilder.ERROR("查询子订单异常");
            return Result.fail("查询子订单异常:" + ex.getMessage());
        }
    }

    public double sumBillAmount(List<OrderDishes> orderDishes) {
        if (CommonUtils.isEmpty(orderDishes)) {
            return 0;
        }
        double billAmount = 0;
        for (OrderDishes od : orderDishes) {
            double price = CommonUtils.orElse(od.getOrderDishesDiscountPrice(), 0D);
            int num = CommonUtils.orElse(od.getOrderDishesNums(), 1);
            billAmount += price * num;
        }
        return billAmount;
    }

    public double sumReturnAmount(List<OrderDishes> orderDishes) {
        if (CommonUtils.isEmpty(orderDishes)) {
            return 0;
        }
        double returnAmt = 0;
        for (OrderDishes od : orderDishes) {
            if (EnumOrderSaleType.of(od.getOrderDishesSaletype()) == EnumOrderSaleType.RETURN) {
                double price = CommonUtils.orElse(od.getOrderDishesDiscountPrice(), 0D);
                int num = CommonUtils.orElse(od.getOrderDishesNums(), 1);
                returnAmt += price * num;
            }
        }
        return returnAmt;
    }

    public double notPaidBillAmount(Integer orderId) {
        return notPaidBillAmount(getOrder(orderId));
    }

    public double notPaidBillAmount(Order order) {
        if (order == null) {
            return 0;
        }
        List<OrderDishes> orderDishes = orderDishesService.selectByOrderId(order.getOrderId());
        return notPaidBillAmount(order, orderDishes);
    }

    public double notPaidBillAmount(Order order, List<OrderDishes> orderDishes) {
        if (order == null) {
            return 0;
        }
        double totalBillAmt = sumBillAmount(orderDishes) - sumReturnAmount(orderDishes);
        double orderErase = CommonUtils.orElse(order.getOrderErase(), 0D);
        double orderReduction = CommonUtils.orElse(order.getOrderReduction(), 0D);
        double paidAmt = CommonUtils.orElse(order.getOrderHadpaid(), 0D);
        return totalBillAmt - orderErase - orderReduction - paidAmt;
    }

    public Order createOrder(CreateOrderParam param) throws SQLException {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Order order = new Order();
            order.setOrderId(param.getOrderId());
            order.setDeskId(param.getDeskId());
            order.setOrderStatus(EnumOrderStatus.UNPAID.status);
            order.setStatus(EnumOrderServeStatus.START.status);
            order.setOrderType(EnumOrderType.NORMAL.type);
            order.setOrderDiscountInfo(Base64.encode(JSONObject.toJSONString(new OrderDiscount())));
            order.setMemberId(0L);
            order.setOrderCustomerNums(param.getCustomerNum());
            order.setAccountId(0L);
            order.setOrderErase(0D);
            order.setOrderRefund(0D);
            order.setOrderReduction(0D);
            order.setOrderHadpaid(0D);

            if (order.getCreateTime() == null) {
                order.setCreateTime(DateBuilder.now().mills());
            }
            orderDAO.insert(order);
            return order;
        } finally {
            clear.run();
        }
    }

    public Integer createNewOrderId() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        String timeStr = DateBuilder.today().format("yyyyMMddHH");
        int diffHours = (int) DateBuilder.diffHours(start, DateBuilder.base(timeStr).dateTime());
        if (diffHours <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + timeStr);
        }
        int nextId = nextId(timeStr);
        if (nextId >= 2 << 15) {
            throw new RuntimeException("循环次数已达最大上限:" + timeStr);
        }
        // 前17位保存时间，后15位保存序列号
        int id = diffHours << 15 | nextId;
        LogUtils.info("创建订单号: " + diffHours + "," + nextId + "," + id);
        return id;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
