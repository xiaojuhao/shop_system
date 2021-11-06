package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderServeStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumOrderType;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.OrderDiscountVO;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.model.CreateOrderParam;

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
    @Inject
    DeskService deskService;

    public List<Order> pageQuery(PageQueryOrderReq req) {
        return orderDAO.pageQuery(req);
    }

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

    public Result<String> changeDesk(Integer orderId, Integer targetDeskId) {
        Order order = this.getOrder(orderId);
        if (order == null) {
            return Result.fail("查询订单信息失败:" + orderId);
        }
        Desk targetDesk = deskService.getById(targetDeskId);
        Desk currDesk = deskService.getById(order.getDeskId());
        if (targetDesk == null) {
            return Result.fail("目标餐桌不存在:" + targetDeskId);
        }
        if (currDesk == null) {
            return Result.fail("获取订单餐桌失败:" + order.getDeskId());
        }
        if (EnumDeskStatus.of(targetDesk.getStatus()) != EnumDeskStatus.FREE) {
            return Result.fail("目标餐桌状态已占用");
        }
        // 占用新餐桌
        targetDesk.setOrderId(orderId);
        targetDesk.setStatus(EnumDeskStatus.IN_USE.status());
        Result<Integer> useRs = deskService.useDesk(targetDesk);
        if (!useRs.isSuccess()) {
            return Result.fail(useRs.getMsg());
        }
        // 更新订单信息
        Order updateOrder = new Order();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setDeskId(targetDesk.getDeskId());
        updateByOrderId(updateOrder);
        // 释放老餐桌
        Result<String> rs = deskService.closeDesk(currDesk.getDeskId());
        if (!rs.isSuccess()) {
            return Result.fail(rs.getMsg());
        } else {
            return Result.success(null);
        }
    }

    public Result<String> erase(Integer orderId, double eraseAmt) {
        if (eraseAmt < 0) {
            return Result.fail("抹零金额错误");
        }
        if (eraseAmt >= 10) {
            return Result.fail("抹零金额不能大于10");
        }
        Order order = this.getOrder(orderId);
        if (order != null) {
            double bill = this.notPaidBillAmount(order);
            if (eraseAmt > bill) {
                return Result.fail("抹零金额不能大于可支付金额");
            }

            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderErase(eraseAmt);
            this.updateByOrderId(update);
            return Result.success("");
        } else {
            return Result.fail("订单信息不存在");
        }
    }

    public Result<String> reduction(Integer orderId, double amt) {
        Order order = this.getOrder(orderId);
        if (order != null) {
            double bill = this.notPaidBillAmount(order);
            if (amt > bill) {
                return Result.fail("减免金额不能大于可支付金额");
            }
            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderReduction(amt);
            this.updateByOrderId(update);
            return Result.success("");
        } else {
            return Result.fail("订单信息不存在");
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

    public Result<OrderOverviewVO> buildOrderOverview(
            Order order,
            List<OrderDishes> orderDishesList,
            List<OrderPay> orderPays) {
        if (order != null) {
            OrderOverviewVO v = new OrderOverviewVO();
            Desk desk = deskService.getById(order.getDeskId());
            if (desk != null) {
                v.setDeskId(desk.getDeskId());
                v.setDeskName(desk.getDeskName());
            }
            v.orderId = order.getOrderId().toString();
            v.customerNum = order.getOrderCustomerNums();
            v.orderTime = DateBuilder.base(order.getCreateTime()).timeStr();
            v.orderNeedPay = this.notPaidBillAmount(order, orderDishesList);
            v.orderHadpaid = order.getOrderHadpaid();
            v.totalPrice = sumTotalPrice(order, orderDishesList);
            v.discountAmount = calcDiscountAmount(order, orderDishesList);
            v.payStatusName = EnumOrderStatus.of(order.getOrderStatus()).remark;
            v.deduction = order.getFullReduceDishesPrice();
            v.returnDishesPrice = this.sumReturnDishesPrice(orderDishesList);
            v.orderErase = OrElse.orGet(order.getOrderErase(), 0D);
            v.orderReduction = OrElse.orGet(order.getOrderReduction(), 0D);
            v.discountName = order.getDiscountReason();
            if (CommonUtils.isNotBlank(order.getOrderDiscountInfo())) {
                OrderDiscountVO d = JSON.parseObject(Base64.decodeStr(order.getOrderDiscountInfo()), OrderDiscountVO.class);
                if (d != null) {
                    v.discountName = d.getDiscountName();
                }
            }

            // 支付信息
            StringBuilder payInfo = new StringBuilder();
            CommonUtils.forEach(orderPays, p -> {
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
            Logger.error("countSubOrder ::" + ex.getMessage());
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
            Logger.info("查询数据失败" + ex.getMessage());
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
            Logger.error("查询子订单异常:" + ex.getMessage());
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
            double price = OrElse.orGet(od.getOrderDishesDiscountPrice(), 0D);
            int num = OrElse.orGet(od.getOrderDishesNums(), 1);
            billAmount += price * num;
        }
        return billAmount;
    }

    // 退菜金额
    public double sumReturnDishesPrice(List<OrderDishes> orderDishes) {
        if (CommonUtils.isEmpty(orderDishes)) {
            return 0;
        }
        double returnAmt = 0;
        for (OrderDishes od : orderDishes) {
            if (EnumOrderSaleType.of(od.getOrderDishesSaletype()) == EnumOrderSaleType.RETURN) {
                double price = OrElse.orGet(od.getOrderDishesDiscountPrice(), 0D);
                int num = OrElse.orGet(od.getOrderDishesNums(), 1);
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
        double totalBillAmt = sumBillAmount(orderDishes) - sumReturnDishesPrice(orderDishes);
        double orderErase = OrElse.orGet(order.getOrderErase(), 0D);
        double orderReduction = OrElse.orGet(order.getOrderReduction(), 0D);
        double paidAmt = OrElse.orGet(order.getOrderHadpaid(), 0D);
        double notPaid = Math.max(0, totalBillAmt - orderErase - orderReduction - paidAmt);
        if (notPaid < 0.01) {
            notPaid = 0D;
        }
        return CommonUtils.parseMoney(notPaid + "", 0D);
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
            order.setOrderDiscountInfo(Base64.encode(JSONObject.toJSONString(new OrderDiscountVO())));
            order.setOrderRecommender(param.getRecommender());
            order.setMemberId(0L);
            order.setOrderCustomerNums(param.getCustomerNum());
            order.setAccountId((long) CurrentAccount.currentAccountId());
            order.setOrderErase(0D);
            order.setOrderRefund(0D);
            order.setOrderReduction(0D);
            order.setOrderHadpaid(0D);
            order.setCreateTime(DateBuilder.now().mills());

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
        Logger.info("创建订单号: " + diffHours + "," + nextId + "," + id);
        return id;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
