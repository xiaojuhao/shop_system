package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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
import com.xjh.dao.query.OrderPayQuery;
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
            return orderDAO.selectByOrderId(orderId).getData();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Result<String> changeDesk(Integer orderId, Integer targetDeskId) {
        Order order = this.getOrder(orderId);
        if (order == null) {
            return Result.fail("????????????????????????:" + orderId);
        }
        Desk targetDesk = deskService.getById(targetDeskId);
        Desk currDesk = deskService.getById(order.getDeskId());
        if (targetDesk == null) {
            return Result.fail("?????????????????????:" + targetDeskId);
        }
        if (currDesk == null) {
            return Result.fail("????????????????????????:" + order.getDeskId());
        }
        if (EnumDeskStatus.of(targetDesk.getStatus()) != EnumDeskStatus.FREE) {
            return Result.fail("???????????????????????????");
        }
        // ???????????????
        targetDesk.setOrderId(orderId);
        targetDesk.setStatus(EnumDeskStatus.IN_USE.status());
        Result<Integer> useRs = deskService.useDesk(targetDesk);
        if (!useRs.isSuccess()) {
            return Result.fail(useRs.getMsg());
        }
        // ??????????????????
        Order updateOrder = new Order();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setDeskId(targetDesk.getDeskId());
        updateByOrderId(updateOrder);
        // ???????????????
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
        // ??????????????????
        List<OrderPay> pays = orderPayService.selectByOrderId(orderId);
        double orderHaidPaid = pays.stream().map(OrderPay::getAmount).reduce(Double::sum).orElse(0D);
        List<OrderDishes> orderDishes = orderDishesService.selectByOrderId(orderId);
        order.setOrderHadpaid(orderHaidPaid);
        order.setFullReduceDishesPrice(0D);
        order.setOrderDiscountInfo(JSON.toJSONString(new OrderDiscountVO()));
        OrderOverviewVO billView = this.buildOrderOverview(order, orderDishes, pays).getData();
        if (billView.getOrderNeedPay() <= 0.01) {
            order.setOrderStatus(EnumOrderStatus.PAID.status);
        } else if (orderHaidPaid > 0.01) {
            order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
        } else {
            order.setOrderStatus(EnumOrderStatus.UNPAID.status);
        }
        orderDAO.updateByOrderId(order);
        // ??????????????????
        Desk desk = new Desk();
        desk.setDeskId(order.getDeskId());
        desk.setOrderId(order.getOrderId());
        deskService.useDesk(desk);
        return Result.success("???????????????");
    }

    public Result<String> erase(Integer orderId, double eraseAmt) {
        if (eraseAmt < 0) {
            return Result.fail("??????????????????");
        }
        if (eraseAmt >= 10) {
            return Result.fail("????????????????????????10");
        }
        Order order = this.getOrder(orderId);
        if (order != null) {
            double bill = this.notPaidBillAmount(order);
            if (eraseAmt > bill) {
                return Result.fail("???????????????????????????????????????");
            }

            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderErase(eraseAmt);
            this.updateByOrderId(update);
            return Result.success("");
        } else {
            return Result.fail("?????????????????????");
        }
    }

    public Result<String> reduction(Integer orderId, double amt) {
        Order order = this.getOrder(orderId);
        if (order != null) {
            double bill = this.notPaidBillAmount(order);
            if (amt > bill) {
                return Result.fail("???????????????????????????????????????");
            }
            Order update = new Order();
            update.setOrderId(orderId);
            update.setOrderReduction(amt);
            this.updateByOrderId(update);
            return Result.success("");
        } else {
            return Result.fail("?????????????????????");
        }
    }

    public Result<Integer> updateByOrderId(Order order) {
        if (order == null || order.getOrderId() == null) {
            return Result.fail("??????????????????");
        }
        try {
            Result<Integer> rs = orderDAO.updateByOrderId(order);
            if (rs.isSuccess()) {
                return Result.success(1);
            } else {
                return Result.fail("??????????????????, ????????????0");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("????????????????????????:" + ex.getMessage());
        }
    }

    public Result<Integer> escapeOrder(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            // ??????????????????
            deskService.closeDesk(order.getDeskId());
            // ??????????????????
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: ???????????? 1: ????????????
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 5?????????
            orderUpdate.setOrderStatus(EnumOrderStatus.ESCAPE.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("????????????????????????:" + ex.getMessage());
        }
    }

    public Result<Integer> freeOrder(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            // ??????????????????
            deskService.closeDesk(order.getDeskId());
            // ??????????????????
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: ???????????? 1: ????????????
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 6?????????
            orderUpdate.setOrderStatus(EnumOrderStatus.FREE.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("????????????????????????:" + ex.getMessage());
        }
    }

    public Result<Integer> recoverOrder(Integer orderId) {
        Order order = getOrder(orderId);
        Desk desk = deskService.getById(order.getDeskId());
        if (EnumDeskStatus.of(desk.getStatus()) != EnumDeskStatus.FREE) {
            return Result.fail("??????[" + desk.getDeskName() + "]?????????????????????????????????");
        }
        try {
            // ??????????????????
            Desk deskUpdate = new Desk();
            deskUpdate.setDeskId(desk.getDeskId());
            deskUpdate.setOrderId(order.getOrderId());
            deskUpdate.setStatus(EnumDeskStatus.PAID.status());
            deskService.updateDeskByDeskId(deskUpdate);
            // ??????????????????
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: ???????????? 1: ????????????
            orderUpdate.setStatus(EnumOrderServeStatus.START.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("????????????????????????:" + ex.getMessage());
        }
    }

    public Result<Integer> changeOrderToPaid(Integer orderId) {
        Order order = getOrder(orderId);
        try {
            // ??????????????????
            deskService.closeDesk(order.getDeskId());
            // ??????????????????
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            // 0: ???????????? 1: ????????????
            orderUpdate.setStatus(EnumOrderServeStatus.END.status);
            // 6?????????
            orderUpdate.setOrderStatus(EnumOrderStatus.PAID.status);
            this.updateByOrderId(orderUpdate);

            return Result.success(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("????????????????????????:" + ex.getMessage());
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
                OrderDiscountVO d = JSON.parseObject(Base64.decodeStr(order.getOrderDiscountInfo()), OrderDiscountVO.class);
                if (d != null) {
                    v.discountName = d.getDiscountName();
                }
            }

            // ????????????
            StringBuilder payInfo = new StringBuilder();
            CommonUtils.forEach(orderPays, p -> {
                payInfo.append(DateBuilder.base(p.getCreatetime()).timeStr())
                        .append(" ????????????:")
                        .append(CommonUtils.formatMoney(p.getAmount()))
                        .append(", ??????")
                        .append(EnumPayMethod.of(p.getPaymentMethod()).name);
                if (CommonUtils.isNotBlank(p.getCardNumber())) {
                    payInfo.append(",?????????:").append(p.getCardNumber());
                }
                payInfo.append("\r\n");
            });
            v.payInfoRemark = payInfo.toString();
        }
        return Result.success(v);
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
            Logger.info("??????????????????" + ex.getMessage());
            return Result.fail("??????????????????" + ex.getMessage());
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
            Logger.error("?????????????????????:" + ex.getMessage());
            // AlertBuilder.ERROR("?????????????????????");
            return Result.fail("?????????????????????:" + ex.getMessage());
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

    // ????????????
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
        while (true) {
            Integer newOrderId = createNewOrderId1();
            if (getOrder(newOrderId) == null) {
                return newOrderId;
            }
        }
    }

    public Integer createNewOrderId1() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        String timeStr = DateBuilder.today().format("yyyyMMddHH");
        int diffHours = (int) DateBuilder.diffHours(start, DateBuilder.base(timeStr).dateTime());
        if (diffHours <= 0) {
            throw new RuntimeException("????????????????????????:" + timeStr);
        }
        int nextId = nextId(timeStr);
        if (nextId >= 2 << 15) {
            throw new RuntimeException("??????????????????????????????:" + timeStr);
        }
        // ???17?????????????????????15??????????????????
        int id = diffHours << 15 | nextId;
        Logger.info("???????????????: " + diffHours + "," + nextId + "," + id);
        return id;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
