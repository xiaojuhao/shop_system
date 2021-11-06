package com.xjh.startup.view.ordermanage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumSubOrderType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.SubOrder;
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

public class OrderManageBillView extends SimpleForm {
    static String YYYYMMDD = "yyyy-MM-dd";
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    SubOrderDAO subOrderDAO = GuiceContainer.getInstance(SubOrderDAO.class);
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
        HBox line = new HBox();
        line.getChildren().addAll(
                printCanvas(buildStat1(orderList, subOrderList), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat2(), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat3(), Math.max(quartWidth, 220)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat4(), Math.max(quartWidth, 300)));
        addLine(line);
    }

    public List<Data> buildStat1(List<Order> orderList, List<SubOrder> subOrderList) {
        int h5 = 0;
        Predicate<SubOrder> isH5 = s -> EnumSubOrderType.of(s.getOrderType()) == EnumSubOrderType.H5;
        for (SubOrder subOrder : subOrderList) {
            // h5订单
            if (isH5.test(subOrder)) {
                h5++;
            }
        }
        List<Data> list = new ArrayList<>();
        list.add(new Data("H5点单数", h5 + ""));
        list.add(new Data("成功交易桌数", "0"));
        list.add(new Data("平均每桌单价", "0.00"));
        list.add(new Data("来客数量", "0"));
        list.add(new Data("平均每客单价", "0.00"));
        list.add(new Data("午市平均单价", "0.00"));
        list.add(new Data("晚市平均单价", "0.00"));
        list.add(new Data("夜宵平均单价", "0.00"));
        return list;
    }

    public List<Data> buildStat2() {
        List<Data> list = new ArrayList<>();
        list.add(new Data("- 抹零", "0.00"));
        list.add(new Data("- 店长减免", "0.00"));
        list.add(new Data("- 打折", "0.00"));
        list.add(new Data("- 退菜", "0.00"));
        list.add(new Data("- 反结账", "0.00"));
        list.add(new Data("- 免单金额", "0.00"));
        list.add(new Data("- 逃单金额", "0.00"));
        list.add(new Data("- 未结账", "0.00"));
        list.add(new Data("- 自助差价补齐", "0.00"));
        return list;
    }

    public List<Data> buildStat3() {
        List<Data> list = new ArrayList<>();
        list.add(new Data("+ 现金", "0.00"));
        list.add(new Data("+ 银行卡", "0.00"));
        list.add(new Data("+ 微信", "0.00"));
        list.add(new Data("+ 支付宝", "0.00"));
        list.add(new Data("+ 储值卡", "0.00"));
        list.add(new Data("+ 美团券", "0.00"));
        list.add(new Data("+ 代金券", "0.00"));
        list.add(new Data("+ 口碑商家", "0.00"));
        list.add(new Data("+ 店铺减免", "0.00"));
        list.add(new Data("+ 套餐买单", "0.00"));
        list.add(new Data("+ 公众号", "0.00"));
        list.add(new Data("+ 公众号减免", "0.00"));
        list.add(new Data("+ 其他优惠", "0.00"));
        list.add(new Data("+ 店铺优惠券", "0.00"));
        list.add(new Data("+ 假日套餐补差价", "0.00"));
        list.add(new Data("+ 微生活代金券", "0.00"));
        list.add(new Data("+ 微生活积分抵扣", "0.00"));
        list.add(new Data("+ 微生活积储值卡", "0.00"));
        list.add(new Data("+ 微信银联支付", "0.00"));
        list.add(new Data("+ 外卖", "0.00"));
        list.add(new Data("+ 银联POS机", "0.00"));
        list.add(new Data("+ 交行活动", "0.00"));
        list.add(new Data("+ 招行活动", "0.00"));
        list.add(new Data("+ 商场活动", "0.00"));
        list.add(new Data("+ 美团闪惠", "0.00"));
        return list;
    }

    public List<Data> buildStat4() {
        List<Data> list = new ArrayList<>();
        list.add(new Data("+ 套餐后台价格", "0.00"));
        list.add(new Data("+ 美团后台价格", "0.00"));
        list.add(new Data("+ 口碑后台价格", "0.00"));
        list.add(new Data("+ 店铺优惠后台价值", "0.00"));
        list.add(new Data("+ 其他后台价值", "0.00"));
        list.add(new Data("+ 补差价后台价值", "0.00"));
        list.add(new Data("+ 公众号实际价值", "0.00"));
        list.add(new Data("+ 微生活代金券实际价值", "0.00"));
        list.add(new Data("+ 微生活积分抵扣实际价值", "0.00"));
        list.add(new Data("+ 微生活储值卡实际价值", "0.00"));
        list.add(new Data("+ 外卖实际价值", "0.00"));
        list.add(new Data("+ 交行活动实际价值", "0.00"));
        list.add(new Data("+ 招行活动实际价值", "0.00"));
        list.add(new Data("+ 商场活动实际价值", "0.00"));
        list.add(new Data("+ 美团闪惠实际价值", "0.00"));
        return list;
    }

    private Canvas printCanvas(List<Data> dataList, double width) {
        Canvas canvas = new Canvas();
        canvas.setWidth(width);
        canvas.setHeight(1300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < dataList.size(); i++) {
            Data data = dataList.get(i);
            gc.fillText(data.getTitle() + ":", 10, 23 * i + 23);
            gc.fillText(data.getValue(), width / 3 * 2, 23 * i + 23);
        }
        gc.restore();
        return canvas;
    }

    @lombok.Data
    public static class Data {
        public Data(String title, String value) {
            this.title = title;
            this.value = value;
        }

        String title;
        String value;
    }
}
