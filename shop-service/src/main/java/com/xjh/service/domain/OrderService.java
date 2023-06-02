package com.xjh.service.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.*;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.OrderDiscountVO;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.dao.query.OrderPayQuery;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.model.CreateOrderParam;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.xjh.common.utils.CommonUtils.tryDecodeBase64;
import static com.xjh.common.utils.CommonUtils.tryEncodeBase64;

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
            return orderDAO.selectByOrderId(orderId).getData();
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

    public Result<String> repayOrder(Integer orderId, String pwd) {
        Order order = orderDAO.selectByOrderId(orderId).getData();

        OrderPayQuery cond = new OrderPayQuery();
        cond.setOrderId(orderId);
        cond.setPaymentStatus(1);
        cond.setExcludePayMethods(Lists.newArrayList(
                EnumPayMethod.WECHAT.code, EnumPayMethod.ALIPAY.code));
        orderPayService.deleteBy(cond);
        // 更新支付金额
        List<OrderPay> pays = orderPayService.selectByOrderId(orderId);
        double orderHaidPaid = pays.stream().map(OrderPay::getAmount).reduce(Double::sum).orElse(0D);
        List<OrderDishes> orderDishes = orderDishesService.selectByOrderId(orderId);
        order.setOrderHadpaid(orderHaidPaid);
        order.setFullReduceDishesPrice(0D);
        order.setOrderDiscountInfo(tryEncodeBase64(JSON.toJSONString(new OrderDiscountVO())));
        OrderOverviewVO billView = this.buildOrderOverview(order, orderDishes, pays).getData();
        if (billView.getOrderNeedPay() <= 0.01) {
            order.setOrderStatus(EnumOrderStatus.PAID.status);
        } else if (orderHaidPaid > 0.01) {
            order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
        } else {
            order.setOrderStatus(EnumOrderStatus.UNPAID.status);
        }
        orderDAO.updateByOrderId(order);
        // 更新餐桌状态
        Desk desk = new Desk();
        desk.setDeskId(order.getDeskId());
        desk.setOrderId(order.getOrderId());
        deskService.useDesk(desk);
        return Result.success("重结算成功");
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
            Result<Integer> rs = orderDAO.updateByOrderId(order);
            if (rs.isSuccess()) {
                return Result.success(1);
            } else {
                return Result.fail("保存订单失败, 更新记录0");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<Integer> escapeOrder(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            // 恢复订单状态
            deskService.closeDesk(order.getDeskId());
            // 恢复订单状态
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: 开始用餐 1: 用餐结束
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 5：逃单
            orderUpdate.setOrderStatus(EnumOrderStatus.ESCAPE.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<Integer> freeOrder(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            // 恢复订单状态
            deskService.closeDesk(order.getDeskId());
            // 恢复订单状态
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: 开始用餐 1: 用餐结束
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 6：免单
            orderUpdate.setOrderStatus(EnumOrderStatus.FREE.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<Integer> recoverOrder(Integer orderId) {
        Order order = getOrder(orderId);
        Desk desk = deskService.getById(order.getDeskId());
        if (EnumDeskStatus.of(desk.getStatus()) != EnumDeskStatus.FREE) {
            return Result.fail("餐桌[" + desk.getDeskName() + "]正在使用中，不可以恢复");
        }
        try {
            // 恢复订单状态
            Desk deskUpdate = new Desk();
            deskUpdate.setDeskId(desk.getDeskId());
            deskUpdate.setOrderId(order.getOrderId());
            deskUpdate.setStatus(EnumDeskStatus.PAID.status());
            deskService.updateDeskByDeskId(deskUpdate);
            // 恢复订单状态
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: 开始用餐 1: 用餐结束
            orderUpdate.setStatus(EnumOrderServeStatus.START.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<Integer> changeOrderToPaid(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            deskService.closeDesk(order.getDeskId());
            // 恢复订单状态
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: 开始用餐 1: 用餐结束
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 6：免单
            orderUpdate.setOrderStatus(EnumOrderStatus.PAID.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("保存订单数据失败:" + ex.getMessage());
        }
    }

    public Result<OrderOverviewVO> buildOrderOverview(
            Order order,
            List<OrderDishes> orderDishesList,
            List<OrderPay> orderPays) {
        OrderOverviewVO v = new OrderOverviewVO();
        if (order != null) {
            v.deskId = order.getDeskId();
            v.deskName = deskService.getDeskName(order.getDeskId());
            v.orderId = order.getOrderId().toString();
            v.customerNum = order.getOrderCustomerNums();
            v.orderTime = DateBuilder.base(order.getCreateTime()).timeStr();
            v.orderNeedPay = this.notPaidBillAmount(order, orderDishesList);
            v.orderHadpaid = order.getOrderHadpaid();
            v.orderRefund = OrElse.orGet(order.getOrderRefund(), 0D);
            v.totalPrice = sumTotalPrice(order, orderDishesList);
            v.returnedCash = OrElse.orGet(order.getOrderReturnCash(), 0D);
            v.discountAmount = calcDiscountAmount(order, orderDishesList);
            v.payStatusName = EnumOrderStatus.of(order.getOrderStatus()).remark;
            v.deduction = order.getFullReduceDishesPrice();
            v.returnDishesPrice = this.sumReturnDishesPrice(orderDishesList);
            v.orderErase = OrElse.orGet(order.getOrderErase(), 0D);
            v.orderReduction = OrElse.orGet(order.getOrderReduction(), 0D);
            v.discountName = order.getDiscountReason();
            if (CommonUtils.isNotBlank(order.getOrderDiscountInfo())) {
                OrderDiscountVO d = JSON.parseObject(tryDecodeBase64(order.getOrderDiscountInfo()), OrderDiscountVO.class);
                if (d != null) {
                    v.discountName = d.getDiscountName();
                }
                if (CommonUtils.isBlank(v.getDiscountName())) {
                    v.discountName = "无";
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
        }
        return Result.success(v);
    }

    private double sumTotalPrice(Order order, List<OrderDishes> orderDishesList) {
        return CommonUtils.collect(orderDishesList, OrderDishes::sumOrderDishesPrice)
                .stream().filter(Objects::nonNull)
                .reduce(0D, Double::sum);
    }

    private double calcDiscountAmount(Order order, List<OrderDishes> orderDishesList) {
        double total = CommonUtils.collect(orderDishesList, OrderDishes::sumOrderDishesPrice)
                .stream().filter(Objects::nonNull)
                .reduce(0D, Double::sum);
        double discountedPrice = CommonUtils.collect(orderDishesList, OrderDishes::sumOrderDishesDiscountPrice)
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
            billAmount += od.sumOrderDishesDiscountPrice();
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
                returnAmt += od.sumOrderDishesDiscountPrice();
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

    public Result<Order> createOrder(CreateOrderParam param) throws SQLException {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Order order = new Order();
            // order.setOrderId(param.getOrderId());
            order.setDeskId(param.getDeskId());
            order.setOrderStatus(EnumOrderStatus.UNPAID.status);
            order.setStatus(EnumOrderServeStatus.START.status);
            order.setOrderType(EnumOrderType.NORMAL.type);
            order.setOrderDiscountInfo(tryEncodeBase64(JSONObject.toJSONString(new OrderDiscountVO())));
            order.setOrderRecommender(param.getRecommender());
            order.setMemberId(0L);
            order.setOrderCustomerNums(param.getCustomerNum());
            order.setAccountId((long) CurrentAccount.currentAccountId());
            order.setOrderErase(0D);
            order.setOrderRefund(0D);
            order.setOrderReduction(0D);
            order.setOrderHadpaid(0D);
            order.setCreateTime(DateBuilder.now().mills());

            Result<Integer> rs = orderDAO.insert(order);
            if(rs.isSuccess()){
                order.setOrderId(rs.getData());
            } else {
                return Result.fail(rs.getCode(), rs.getMsg());
            }
            return Result.success(order);
        } finally {
            clear.run();
        }
    }

    @Deprecated
    public Integer createNewOrderId() {
        while (true) {
            Integer newOrderId = createNewOrderId1();
            if (getOrder(newOrderId) == null) {
                return newOrderId;
            }
        }
    }
    @Deprecated
    public Integer createNewOrderId1() {
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
    @Deprecated
    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
