package com.xjh.service.domain;

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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.xjh.common.utils.CommonUtils.*;
import static com.xjh.common.utils.DateBuilder.*;

@Singleton
public class BillListService {
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


    public BillListDO queryBillList(PageQueryOrderReq cond, List<Date> missedDates) {
        Date start = OrElse.orGet(toDate(cond.getStartDate()), yestoday().date());
        Date end = OrElse.orGet(toDate(cond.getEndDate()), today().date());
        // 查询统计表里面的记录
        List<BillListDO> list = billListDAO.selectList(start, end);
        List<Long> billListDates = collect(collect(list, BillListDO::getDateTime), this::zeroAM);
        List<Long> rangeDates = collect(collect(DateRange.of(start, end).listDates(), Date::getTime), this::zeroAM);
        // 缺失的日期
        rangeDates.removeAll(billListDates);
        for (Long missedDate : rangeDates) {
            // doStatistics(new Date(missedDate), (bo, noon, night, supper) -> list.add(bo));
            missedDates.add(new Date(missedDate));
        }
        // 合并结果
        BillListDO merged = mergeList(list);
        return OrElse.orGet(merged, new BillListDO());
    }

    public BillListNoonDO queryBillListNoon(PageQueryOrderReq cond) {
        Date start = OrElse.orGet(toDate(cond.getStartDate()), yestoday().date());
        Date end = OrElse.orGet(toDate(cond.getEndDate()), today().date());
        // 查询统计表&合并结果
        BillListNoonDO merged = mergeList(billListNoonDAO.selectList(start, end));
        return OrElse.orGet(merged, new BillListNoonDO());
    }

    public BillListNightDO queryBillListNight(PageQueryOrderReq cond) {
        Date start = OrElse.orGet(toDate(cond.getStartDate()), yestoday().date());
        Date end = OrElse.orGet(toDate(cond.getEndDate()), today().date());
        // 查询统计表&合并结果
        BillListNightDO merged = mergeList(billListNightDAO.selectList(start, end));
        return OrElse.orGet(merged, new BillListNightDO());
    }

    public BillListSupperDO queryBillListSupper(PageQueryOrderReq cond) {
        Date start = OrElse.orGet(toDate(cond.getStartDate()), yestoday().date());
        Date end = OrElse.orGet(toDate(cond.getEndDate()), today().date());
        // 查询统计表&合并结果
        BillListSupperDO merged = mergeList(billListSupperDAO.selectList(start, end));
        return OrElse.orGet(merged, new BillListSupperDO());
    }

    Long zeroAM(Long dateTime) {
        return DateBuilder.base(new Date(dateTime)).zeroAM().date().getTime();
    }

    public static <BILL> BILL mergeList(List<BILL> list) {
        if (CommonUtils.isEmpty(list)) {
            return null;
        }
        TimeRecord timer = TimeRecord.start();
        BILL v = firstOf(list);
        for (int i = 1; i < list.size(); i++) {
            mergeWith(v, list.get(i));
        }
        System.out.println("合并" + list.size() + "条记录, 耗时:" + timer.getCost());
        return v;
    }

    public static <BILL> void mergeWith(BILL v1, BILL v2) {
        Set<String> ignores = newHashset("dateTime", "id");
        Map<String, ReflectionUtils.PropertyDescriptor> pds = ReflectionUtils.resolvePD(v1.getClass());
        for (Map.Entry<String, ReflectionUtils.PropertyDescriptor> pd : pds.entrySet()) {
            if (ignores.contains(pd.getKey())) {
                continue;
            }
            Object val1 = pd.getValue().readValue(v1);
            if (val1 instanceof Integer) {
                int val2 = parseInt(pd.getValue().readValue(v2), 0);
                pd.getValue().writeValue(v1, ((int) val1) + val2);
            }
            if (val1 instanceof Double) {
                double val2 = parseDouble(pd.getValue().readValue(v2), 0D);
                pd.getValue().writeValue(v1, ((double) val1) + val2);
            }
        }
    }

    public void doStatistics(Date date, BillListCallback callback) throws Exception {
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

        callback.invoke(bo, noon, night, supper);
    }

    public void saveBill(BillListDO bo,
                         BillListNoonDO noon,
                         BillListNightDO night,
                         BillListSupperDO supper) throws Exception {
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

    public interface BillListCallback {
        void invoke(BillListDO bo,
                    BillListNoonDO noon,
                    BillListNightDO night,
                    BillListSupperDO supper) throws Exception;
    }

}
