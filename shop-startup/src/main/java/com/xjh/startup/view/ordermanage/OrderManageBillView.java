package com.xjh.startup.view.ordermanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumOrderPeriodType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.enumeration.EnumSubOrderType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleForm;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class OrderManageBillView extends SimpleForm {
    static String YYYYMMDD = "yyyy-MM-dd";
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    SubOrderDAO subOrderDAO = GuiceContainer.getInstance(SubOrderDAO.class);
    OrderDishesDAO orderDishesDAO = GuiceContainer.getInstance(OrderDishesDAO.class);
    OrderPayDAO orderPayDAO = GuiceContainer.getInstance(OrderPayDAO.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);
    Window window;

    public OrderManageBillView(PageQueryOrderReq cond, Window window) {
        cond = CopyUtils.deepClone(cond);
        this.window = window;
        buildHeader(cond);
        addLine(new Separator(Orientation.HORIZONTAL));
        buildBill(cond);
    }

    private void buildHeader(PageQueryOrderReq cond) {
        double quarWidth = window.getWidth() * 0.9 * 0.24;
        StoreVO store = storeService.getStore().getData();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 0, 0, 0));
        grid.setPrefWidth(window.getWidth());
        Label startLabel = new Label("??????:" + DateBuilder.base(cond.getStartDate()).format(YYYYMMDD));
        Label endLabel = new Label("??????:" + DateBuilder.base(cond.getEndDate()).format(YYYYMMDD));
        startLabel.setPrefWidth(quarWidth);
        endLabel.setPrefWidth(quarWidth);
        startLabel.setPadding(new Insets(10, 0, 0, 0));
        endLabel.setPadding(new Insets(10, 0, 0, 0));
        grid.add(startLabel, 0, 0);
        grid.add(endLabel, 1, 0);

        Label storeName = new Label("??????:" + store.getName());
        Label generateTime = new Label("????????????:" + DateBuilder.today().format(YYYYMMDD));
        storeName.setPadding(new Insets(10, 0, 0, 0));
        generateTime.setPadding(new Insets(10, 0, 0, 0));
        grid.add(storeName, 0, 1);
        grid.add(generateTime, 1, 1);

        Button print = new Button("??????");
        print.setPrefWidth(50);
        print.setPrefHeight(50);
        GridPane.setMargin(print, new Insets(5, 0, 5, quarWidth));

        Button reload = new Button("????????????");
        reload.setOnAction(evt -> buildBill(cond));
        reload.setPrefWidth(100);
        reload.setPrefHeight(50);
        GridPane.setMargin(reload, new Insets(5, 0, 5, 20));
        grid.add(print, 3, 0, 1, 2);
        grid.add(reload, 4, 0, 1, 2);

        addLine(newCenterLine(grid));
    }

    private void buildBill(PageQueryOrderReq cond) {
        double quartWidth = window.getWidth() / 4 * 0.95;
        List<Order> orderList = orderService.pageQuery(cond);
        List<Integer> orderIdList = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
        List<SubOrder> subOrderList = subOrderDAO.selectByOrderIds(orderIdList);
        List<OrderDishes> orderDishesList = orderDishesDAO.selectByOrderIds(orderIdList);
        List<OrderPay> orderPayList = orderPayDAO.selectByOrderIds(orderIdList);
        BillBO bo = new BillBO();
        sumOrderBill(bo, orderList, subOrderList, orderDishesList, orderPayList);
        HBox line = new HBox();
        line.getChildren().addAll(
                printCanvas(buildStat1(bo), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat2(bo), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat3(bo), Math.max(quartWidth, 220)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat4(bo), Math.max(quartWidth, 300)));
        addLine(line);
    }

    private void sumOrderBill(BillBO bo,
            List<Order> orders,
            List<SubOrder> subOrders,
            List<OrderDishes> orderDishesList,
            List<OrderPay> orderPayList) {
        Predicate<SubOrder> isH5 = it -> EnumSubOrderType.of(it.getOrderType()) == EnumSubOrderType.H5;

        Map<Integer, List<SubOrder>> subOrderMap = CommonUtils.groupBy(subOrders, SubOrder::getOrderId);
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
            bo.totalDue += billView.orderNeedPay;
            bo.actualAmount += billView.orderHadpaid;

            bo.totalDiscountPrice += billView.discountAmount;
            bo.totalErasePrice += billView.orderErase;
            bo.totalReturnPrice += billView.returnDishesPrice;
            bo.totalHadPaidPrice += billView.orderHadpaid;
            bo.totalRefundPrice += billView.orderRefund;
            bo.totalRedunction += billView.orderReduction;

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
                    bo.totalSuccNums += 1;
                    bo.customerNums += order.getOrderCustomerNums();
            }

            EnumOrderPeriodType periodType = EnumOrderPeriodType.check(order.getCreateTime());
            switch (periodType) {
                case NOON:
                    bo.customerNumsNoon += order.getOrderCustomerNums();
                    bo.actualAmountNoon += billView.orderHadpaid;
                    break;
                case NIGHT:
                    bo.customerNumsNight += order.getOrderCustomerNums();
                    bo.actualAmountNight += billView.orderHadpaid;
                default:
                    bo.customerNumsSupper += order.getOrderCustomerNums();
                    bo.actualAmountSupper += billView.orderHadpaid;
            }

            // ???????????????
            CommonUtils.forEach(orderPayList, pay -> {
                EnumPayMethod pm = EnumPayMethod.of(pay.getPaymentMethod());
                double s = OrElse.orGet(bo.payMethodSummarize.get(pm), 0D);
                bo.payMethodSummarize.put(pm, s + pay.getAmount());

                double a = OrElse.orGet(bo.payMethodActualSummarize.get(pm), 0D);
                bo.payMethodActualSummarize.put(pm, a + pay.getActualAmount());
            });
        }
    }

    public List<BillItem> buildStat1(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("H5?????????", bo.h5OrderNums));
        list.add(new BillItem("??????????????????", bo.totalSuccNums));
        list.add(new BillItem("??????????????????", new Money(bo.totalSuccNums == 0 ? 0D : bo.actualAmount / bo.totalSuccNums)));
        list.add(new BillItem("????????????", bo.customerNums));
        list.add(new BillItem("??????????????????", new Money(bo.customerNums == 0 ? 0D : bo.actualAmount / bo.customerNums)));
        list.add(new BillItem("??????????????????", new Money(bo.customerNumsNoon == 0 ? 0D : bo.actualAmountNoon / bo.customerNumsNoon)));
        list.add(new BillItem("??????????????????", new Money(bo.customerNumsNight == 0 ? 0D : bo.actualAmountNight / bo.customerNumsNight)));
        list.add(new BillItem("??????????????????", new Money(bo.customerNumsSupper == 0 ? 0D : bo.actualAmountSupper / bo.customerNumsSupper)));
        return list;
    }

    public List<BillItem> buildStat2(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("- ??????", new Money(bo.totalErasePrice)));
        list.add(new BillItem("- ????????????", new Money(bo.totalRedunction)));
        list.add(new BillItem("- ??????", new Money(bo.totalDiscountPrice)));
        list.add(new BillItem("- ??????", new Money(bo.totalReturnPrice)));
        list.add(new BillItem("- ?????????", new Money(bo.totalRefundPrice)));
        list.add(new BillItem("- ????????????", new Money(bo.totalFreePrice)));
        list.add(new BillItem("- ????????????", new Money(bo.totalEscapePrice)));
        list.add(new BillItem("- ?????????", new Money(bo.totalUnpaidPrice)));
        list.add(new BillItem("- ??????????????????", "0.00"));
        return list;
    }

    public List<BillItem> buildStat3(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        for (EnumPayMethod payMethod : EnumPayMethod.values()) {
            list.add(new BillItem("+ " + payMethod.name, new Money(bo.payMethodSummarize.get(payMethod))));
        }
        return list;
    }

    public List<BillItem> buildStat4(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        for (EnumPayMethod payMethod : EnumPayMethod.values()) {
            if (payMethod.showActual) {
                list.add(new BillItem("+ " + payMethod.name + "????????????", new Money(bo.payMethodActualSummarize.get(payMethod))));
            }
        }
        return list;
    }

    private Canvas printCanvas(List<BillItem> dataList, double width) {
        Canvas canvas = new Canvas();
        canvas.setWidth(width);
        canvas.setHeight(1300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < dataList.size(); i++) {
            BillItem data = dataList.get(i);
            gc.fillText(data.getTitle() + ":", 10, 23 * i + 23);
            gc.fillText(data.getValue(), width / 3 * 2, 23 * i + 23);
        }
        gc.restore();
        return canvas;
    }

    @Data
    public static class BillItem {
        public BillItem(String title, Object value) {
            this.title = title;
            this.value = value.toString();
        }

        String title;
        String value;
    }

    public static class BillBO {
        int h5OrderNums;
        // ????????????
        int totalSuccNums;
        int customerNums;
        int customerNumsNoon = 0;//??????
        int customerNumsSupper = 0;//??????
        int customerNumsNight = 0;//??????

        double totalDue;

        double actualAmount = 0;//????????????
        double actualAmountFull = 0;//????????????
        double actualAmountNoon = 0;//??????????????????
        double actualAmountSupper = 0;//??????????????????
        double actualAmountNight = 0;//??????????????????

        double totalDiscountPrice = 0;//????????????
        double totalErasePrice = 0;//????????????
        double totalReturnPrice = 0;//???????????????
        double totalHadPaidPrice = 0;//???????????????
        double totalRefundPrice = 0;//???????????????

        double totalEscapePrice;
        int totalEscapeNums;

        double totalFreePrice;
        int totalFreeNums;

        double totalUnpaidPrice;
        int totalUnpaidNums;

        double totalRedunction;

        Map<EnumPayMethod, Double> payMethodSummarize = new HashMap<>();
        Map<EnumPayMethod, Double> payMethodActualSummarize = new HashMap<>();

    }


}
