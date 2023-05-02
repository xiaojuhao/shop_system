package com.xjh.service.jobs;

import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumOrderPeriodType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.enumeration.EnumSubOrderType;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.foundation.SumActualPrice;
import com.xjh.dao.foundation.SumTotalPrice;
import com.xjh.dao.mapper.*;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.OrderService;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public class BillListJob {
    @Inject
    BillListDAO billListDAO;
    @Inject
    BillListNoonDAO billListNoonDAO;
    @Inject
    BillListNightDAO billListNightDAO;
    @Inject
    BillListSupperDAO billListSupperDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    OrderService orderService;
    @Inject
    SubOrderDAO subOrderDAO;
    @Inject
    OrderDishesDAO orderDishesDAO;
    @Inject
    OrderPayDAO orderPayDAO;


    public void startJob() {
        try {
            doJob();
        } catch (Exception ex) {
            Logger.error("BillListJob >> " + ex.getMessage());
        }
    }

    public void doJob() throws Exception {
        Date start = DateBuilder.today().date();
        BillListDO newest = billListDAO.newestDO();
        if (newest != null) {
            start = new Date(newest.getDateTime());
        } else {
            Order order = orderDAO.firstOrderOf(null);
            if (order != null) {
                start = new Date(order.getCreateTime());
            }
        }
        start = DateBuilder.base(start).zeroAM().date();
        DateRange dateRange = DateRange.of(start, DateBuilder.yestoday().date());
        System.out.println("统计区间: " + dateRange);
        while (dateRange.hasNext()) {
            doStatistics(dateRange.nextDay());
        }
    }

    public void doStatistics(Date date) throws SQLException {
        date = DateBuilder.base(date).zeroAM().date();

        System.out.println("统计>> " + DateBuilder.base(date).format("yyyy-MM-dd"));
        PageQueryOrderReq cond = new PageQueryOrderReq();
        cond.setStartDate(DateBuilder.base(date).dateTime().toLocalDate());
        cond.setEndDate(DateBuilder.base(date).plusDays(1).dateTime().toLocalDate());
        cond.setPageSize(Integer.MAX_VALUE);
        List<Order> orderList = orderDAO.pageQuery(cond);

        BillListDO bo = new BillListDO();
        BillListNoonDO noon = new BillListNoonDO();
        BillListNightDO night = new BillListNightDO();
        BillListSupperDO supper = new BillListSupperDO();

        bo.setDateTime(date.getTime());
        noon.setDateTime(date.getTime());
        night.setDateTime(date.getTime());
        supper.setDateTime(date.getTime());

        sumOrderBill(bo, noon, night, supper, orderList);

        billListDAO.save(bo);
        billListNoonDAO.save(noon);
        billListNightDAO.save(night);
        billListSupperDAO.save(supper);

    }

    public void sumOrderBill(BillListDO bo,
                             BillListNoonDO noon,
                             BillListNightDO night,
                             BillListSupperDO supper,
                             List<Order> orders) {
        List<Integer> orderIdList = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
        List<SubOrder> subOrderList = subOrderDAO.selectByOrderIds(orderIdList);
        List<OrderDishes> orderDishesList = orderDishesDAO.selectByOrderIds(orderIdList);
        List<OrderPay> orderPayList = orderPayDAO.selectByOrderIds(orderIdList);


        Predicate<SubOrder> isH5 = it -> EnumSubOrderType.of(it.getOrderType()) == EnumSubOrderType.H5;

        Map<Integer, List<SubOrder>> subOrderMap = CommonUtils.groupBy(subOrderList, SubOrder::getOrderId);
        for (Order order : orders) {
            List<SubOrder> subs = subOrderMap.get(order.getOrderId());
            OrderOverviewVO billView = orderService.buildOrderOverview(order,
                    orderDishesList,
                    orderPayList).getData();

            CommonUtils.forEach(subs, sub -> {
                if (isH5.test(sub)) {
                    bo.h5OrderNums += 1;
                }
            });

            bo.totalDiscountPrice += billView.discountAmount;
            bo.totalErasePrice += billView.orderErase;
            bo.totalReturnPrice += billView.returnDishesPrice;
            bo.totalHadPaidPrice += billView.orderHadpaid;
            bo.totalRefundPrice += billView.orderRefund;
            bo.totalReductionPrice += billView.orderReduction;

            EnumOrderStatus orderStatus = EnumOrderStatus.of(order.getOrderStatus());
            switch (orderStatus) {
                case ESCAPE:
                    bo.totalEscapePrice += billView.orderNeedPay;
                    bo.totalEscapeNums += 1;
                    break;
                case FREE:
                    bo.totalFreePrice += billView.orderNeedPay;
                    bo.totalFreeNums += 1;
                    break;
                case UNPAID:
                    bo.totalUnpaidPrice += billView.orderNeedPay;
                    bo.totalUnpaidNums += 1;
                    break;
                default:
                    bo.customerNums += order.getOrderCustomerNums();
            }

            EnumOrderPeriodType periodType = EnumOrderPeriodType.check(order.getCreateTime());
            switch (periodType) {
                case NOON:
                    noon.customerNums += order.getOrderCustomerNums();
                    noon.totalHadPaidPrice += billView.orderHadpaid;
                    break;
                case NIGHT:
                    night.customerNums += order.getOrderCustomerNums();
                    night.totalHadPaidPrice += billView.orderHadpaid;
                case SUPER:
                    supper.customerNums += order.getOrderCustomerNums();
                    supper.totalHadPaidPrice += billView.orderHadpaid;
                default:
                    bo.customerNums += order.getOrderCustomerNums();
                    bo.totalHadPaidPrice += billView.orderHadpaid;
            }

            // 按渠道统计
            CommonUtils.forEach(orderPayList, pay -> {
                EnumPayMethod pm = EnumPayMethod.of(pay.getPaymentMethod());
                // 支付渠道累计金额
                ReflectionUtils.PropertyDescriptor totalPricePD = getSumTotalPricePD(BillListDO.class, pm);
                if (totalPricePD != null) {
                    double s = CommonUtils.parseDouble(totalPricePD.readValue(bo), 0D);
                    totalPricePD.writeValue(bo, s + pay.getAmount());
                }
                // 支付渠道实际金额
                ReflectionUtils.PropertyDescriptor actualPricePD = getSumActualPricePD(BillListDO.class, pm);
                if (actualPricePD != null) {
                    double s = CommonUtils.parseDouble(actualPricePD.readValue(bo), 0D);
                    actualPricePD.writeValue(bo, s + pay.getActualAmount());
                }
            });
        }
    }

    public static ReflectionUtils.PropertyDescriptor getSumTotalPricePD(Class<?> clz, EnumPayMethod pm) {
        for (ReflectionUtils.PropertyDescriptor pd : ReflectionUtils.resolvePD(clz).values()) {
            if (pd.getField().isAnnotationPresent(SumTotalPrice.class)) {
                if (pd.getField().getAnnotation(SumTotalPrice.class).value() == pm) {
                    return pd;
                }
            }
        }
        return null;
    }

    public static ReflectionUtils.PropertyDescriptor getSumActualPricePD(Class<?> clz, EnumPayMethod pm) {
        for (ReflectionUtils.PropertyDescriptor pd : ReflectionUtils.resolvePD(clz).values()) {
            if (pd.getField().isAnnotationPresent(SumActualPrice.class)) {
                if (pd.getField().getAnnotation(SumActualPrice.class).value() == pm) {
                    return pd;
                }
            }
        }
        return null;
    }
}
