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
        Label startLabel = new Label("开始:" + DateBuilder.base(cond.getStartDate()).format(YYYYMMDD));
        Label endLabel = new Label("结束:" + DateBuilder.base(cond.getEndDate()).format(YYYYMMDD));
        startLabel.setPrefWidth(quarWidth);
        endLabel.setPrefWidth(quarWidth);
        startLabel.setPadding(new Insets(10, 0, 0, 0));
        endLabel.setPadding(new Insets(10, 0, 0, 0));
        grid.add(startLabel, 0, 0);
        grid.add(endLabel, 1, 0);

        Label storeName = new Label("店名:" + store.getName());
        Label generateTime = new Label("生成时间:" + DateBuilder.today().format(YYYYMMDD));
        storeName.setPadding(new Insets(10, 0, 0, 0));
        generateTime.setPadding(new Insets(10, 0, 0, 0));
        grid.add(storeName, 0, 1);
        grid.add(generateTime, 1, 1);

        Button print = new Button("打印");
        print.setPrefWidth(50);
        print.setPrefHeight(50);
        GridPane.setMargin(print, new Insets(5, 0, 5, quarWidth));

        Button reload = new Button("重载数据");
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
                printCanvas(buildStat4(), Math.max(quartWidth, 300)));
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

            // 按渠道统计
            CommonUtils.forEach(orderPayList, pay -> {
                EnumPayMethod pm = EnumPayMethod.of(pay.getPaymentMethod());
                double s = OrElse.orGet(bo.payMethodSummarize.get(pm), 0D);
                bo.payMethodSummarize.put(pm, s + pay.getAmount());
            });
        }
    }

    public List<BillItem> buildStat1(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("H5点单数", bo.h5OrderNums));
        list.add(new BillItem("成功交易桌数", bo.totalSuccNums));
        list.add(new BillItem("平均每桌单价", new Money(bo.totalSuccNums == 0 ? 0D : bo.actualAmount / bo.totalSuccNums)));
        list.add(new BillItem("来客数量", bo.customerNums));
        list.add(new BillItem("平均每客单价", new Money(bo.customerNums == 0 ? 0D : bo.actualAmount / bo.customerNums)));
        list.add(new BillItem("午市每客单价", new Money(bo.customerNumsNoon == 0 ? 0D : bo.actualAmountNoon / bo.customerNumsNoon)));
        list.add(new BillItem("晚市每客单价", new Money(bo.customerNumsNight == 0 ? 0D : bo.actualAmountNight / bo.customerNumsNight)));
        list.add(new BillItem("夜宵每客单价", new Money(bo.customerNumsSupper == 0 ? 0D : bo.actualAmountSupper / bo.customerNumsSupper)));
        return list;
    }

    public List<BillItem> buildStat2(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("- 抹零", new Money(bo.totalErasePrice)));
        list.add(new BillItem("- 店长减免", new Money(bo.totalRedunction)));
        list.add(new BillItem("- 打折", new Money(bo.totalDiscountPrice)));
        list.add(new BillItem("- 退菜", new Money(bo.totalReturnPrice)));
        list.add(new BillItem("- 反结账", new Money(bo.totalRefundPrice)));
        list.add(new BillItem("- 免单金额", new Money(bo.totalFreePrice)));
        list.add(new BillItem("- 逃单金额", new Money(bo.totalEscapePrice)));
        list.add(new BillItem("- 未结账", new Money(bo.totalUnpaidPrice)));
        list.add(new BillItem("- 自助差价补齐", "0.00"));
        return list;
    }

    public List<BillItem> buildStat3(BillBO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("+ 现金", new Money(bo.payMethodSummarize.get(EnumPayMethod.CASH))));
        list.add(new BillItem("+ 银行卡", new Money(bo.payMethodSummarize.get(EnumPayMethod.BANKCARD))));
        list.add(new BillItem("+ 微信", new Money(bo.payMethodSummarize.get(EnumPayMethod.WECHAT))));
        list.add(new BillItem("+ 支付宝", new Money(bo.payMethodSummarize.get(EnumPayMethod.ALIPAY))));
        list.add(new BillItem("+ 储值卡", new Money(bo.payMethodSummarize.get(EnumPayMethod.STORECARD))));
        list.add(new BillItem("+ 美团券", new Money(bo.payMethodSummarize.get(EnumPayMethod.MEITUAN))));
        list.add(new BillItem("+ 代金券", "0.00"));
        list.add(new BillItem("+ 口碑商家", new Money(bo.payMethodSummarize.get(EnumPayMethod.KOUBEI))));
        list.add(new BillItem("+ 店铺减免", "0.00"));
        list.add(new BillItem("+ 套餐买单", "0.00"));
        list.add(new BillItem("+ 公众号", "0.00"));
        list.add(new BillItem("+ 公众号减免", "0.00"));
        list.add(new BillItem("+ 其他优惠", "0.00"));
        list.add(new BillItem("+ 店铺优惠券", "0.00"));
        list.add(new BillItem("+ 假日套餐补差价", "0.00"));
        list.add(new BillItem("+ 微生活代金券", "0.00"));
        list.add(new BillItem("+ 微生活积分抵扣", "0.00"));
        list.add(new BillItem("+ 微生活积储值卡", "0.00"));
        list.add(new BillItem("+ 微信银联支付", "0.00"));
        list.add(new BillItem("+ 外卖", "0.00"));
        list.add(new BillItem("+ 银联POS机", new Money(bo.payMethodSummarize.get(EnumPayMethod.POS))));
        list.add(new BillItem("+ 交行活动", "0.00"));
        list.add(new BillItem("+ 招行活动", "0.00"));
        list.add(new BillItem("+ 商场活动", "0.00"));
        list.add(new BillItem("+ 美团闪惠", "0.00"));
        return list;
    }

    public List<BillItem> buildStat4() {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("+ 套餐后台价格", "0.00"));
        list.add(new BillItem("+ 美团后台价格", "0.00"));
        list.add(new BillItem("+ 口碑后台价格", "0.00"));
        list.add(new BillItem("+ 店铺优惠后台价值", "0.00"));
        list.add(new BillItem("+ 其他后台价值", "0.00"));
        list.add(new BillItem("+ 补差价后台价值", "0.00"));
        list.add(new BillItem("+ 公众号实际价值", "0.00"));
        list.add(new BillItem("+ 微生活代金券实际价值", "0.00"));
        list.add(new BillItem("+ 微生活积分抵扣实际价值", "0.00"));
        list.add(new BillItem("+ 微生活储值卡实际价值", "0.00"));
        list.add(new BillItem("+ 外卖实际价值", "0.00"));
        list.add(new BillItem("+ 交行活动实际价值", "0.00"));
        list.add(new BillItem("+ 招行活动实际价值", "0.00"));
        list.add(new BillItem("+ 商场活动实际价值", "0.00"));
        list.add(new BillItem("+ 美团闪惠实际价值", "0.00"));
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
        // 成功桌数
        int totalSuccNums;
        int customerNums;
        int customerNumsNoon = 0;//午市
        int customerNumsSupper = 0;//夜宵
        int customerNumsNight = 0;//晚市

        double totalDue;

        double actualAmount = 0;//实际收入
        double actualAmountFull = 0;//实际收入
        double actualAmountNoon = 0;//午市实际收入
        double actualAmountSupper = 0;//夜宵实际收入
        double actualAmountNight = 0;//晚市实际收入

        double totalDiscountPrice = 0;//折扣总额
        double totalErasePrice = 0;//抹零总额
        double totalReturnPrice = 0;//已退菜总额
        double totalHadPaidPrice = 0;//已付款总额
        double totalRefundPrice = 0;//反结账总额

        double totalEscapePrice;
        int totalEscapeNums;

        double totalFreePrice;
        int totalFreeNums;

        double totalUnpaidPrice;
        int totalUnpaidNums;

        double totalRedunction;

        Map<EnumPayMethod, Double> payMethodSummarize = new HashMap<>();

    }


}
