package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderServeStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumOrderType;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.OrderDiscount;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.SubOrderDAO;
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

    public double sumBillAmount(Integer orderId) {
        List<OrderDishes> orderDishes = orderDishesService.selectOrderDishes(orderId);
        double billAmount = 0;
        for (OrderDishes od : orderDishes) {
            billAmount += od.getOrderDishesDiscountPrice() * od.getOrderDishesNums();
        }
        return billAmount;
    }

    public double sumReturnAmount(Integer orderId) {
        List<OrderDishes> orderDishes = orderDishesService.selectOrderDishes(orderId);
        double billAmount = 0;
        for (OrderDishes od : orderDishes) {
            if (EnumOrderSaleType.of(od.getOrderDishesSaletype()) == EnumOrderSaleType.RETURN) {
                billAmount += od.getOrderDishesDiscountPrice() * od.getOrderDishesNums();
            }
        }
        return billAmount;
    }

    public double notPaidBillAmount(Integer orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            return 0;
        }
        double totalAmount = sumBillAmount(orderId) - sumReturnAmount(orderId);
        return totalAmount - order.getOrderErase() - order.getOrderReduction() - order.getOrderHadpaid();
    }

    public Order createOrder(CreateOrderParam param) throws SQLException {
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
        return diffHours << 15 | nextId;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
