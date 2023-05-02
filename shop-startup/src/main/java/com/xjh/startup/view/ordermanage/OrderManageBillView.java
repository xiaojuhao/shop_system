package com.xjh.startup.view.ordermanage;

import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.ReflectionUtils;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.OrderPayDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.service.jobs.BillListJob;
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

import java.util.ArrayList;
import java.util.List;

import static com.xjh.service.jobs.BillListJob.getSumActualPricePD;
import static com.xjh.service.jobs.BillListJob.getSumTotalPricePD;

public class OrderManageBillView extends SimpleForm {
    static String YYYYMMDD = "yyyy-MM-dd";
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    SubOrderDAO subOrderDAO = GuiceContainer.getInstance(SubOrderDAO.class);
    OrderDishesDAO orderDishesDAO = GuiceContainer.getInstance(OrderDishesDAO.class);
    OrderPayDAO orderPayDAO = GuiceContainer.getInstance(OrderPayDAO.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);
    BillListJob billListJob = GuiceContainer.getInstance(BillListJob.class);
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

        BillListDO bo = new BillListDO();
        BillListNoonDO noon = new BillListNoonDO();
        BillListNightDO night = new BillListNightDO();
        BillListSupperDO supper = new BillListSupperDO();
        billListJob.sumOrderBill(bo, noon, night, supper, orderList);
        HBox line = new HBox();
        line.getChildren().addAll(
                printCanvas(buildStat1(bo, noon, night, supper), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat2(bo), Math.max(quartWidth, 200)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat3(bo), Math.max(quartWidth, 220)),
                new Separator(Orientation.VERTICAL),
                printCanvas(buildStat4(bo), Math.max(quartWidth, 300)));
        addLine(line);
    }


    public List<BillItem> buildStat1(BillListDO bo, BillListNoonDO noon, BillListNightDO night, BillListSupperDO supper) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("H5点单数", bo.getH5OrderNums()));
        list.add(new BillItem("成功交易桌数", bo.getOrderNums()));
        list.add(new BillItem("平均每桌单价", bo.avgPrice()));
        list.add(new BillItem("来客数量", bo.getCustomerNums()));
        list.add(new BillItem("平均每客单价", bo.custAvgPrice()));
        list.add(new BillItem("午市每客单价", noon.custAvgPrice()));
        list.add(new BillItem("晚市每客单价", night.custAvgPrice()));
        list.add(new BillItem("夜宵每客单价", supper.custAvgPrice()));
        return list;
    }

    public List<BillItem> buildStat2(BillListDO bo) {
        List<BillItem> list = new ArrayList<>();
        list.add(new BillItem("- 抹零", new Money(bo.getTotalErasePrice())));
        list.add(new BillItem("- 店长减免", new Money(bo.getTotalReductionPrice())));
        list.add(new BillItem("- 打折", new Money(bo.getTotalDiscountPrice())));
        list.add(new BillItem("- 退菜", new Money(bo.getTotalReturnPrice())));
        list.add(new BillItem("- 反结账", new Money(bo.getTotalRefundPrice())));
        list.add(new BillItem("- 免单金额", new Money(bo.getTotalFreePrice())));
        list.add(new BillItem("- 逃单金额", new Money(bo.getTotalEscapePrice())));
        list.add(new BillItem("- 未结账", new Money(bo.getTotalUnpaidPrice())));
        list.add(new BillItem("- 自助差价补齐", "0.00"));
        return list;
    }

    public List<BillItem> buildStat3(BillListDO bo) {
        List<BillItem> list = new ArrayList<>();
        for (EnumPayMethod payMethod : EnumPayMethod.values()) {
            Double totalPrice = -1D;
            ReflectionUtils.PropertyDescriptor totalPricePD = getSumTotalPricePD(BillListDO.class, payMethod);
            if (totalPricePD != null) {
                totalPrice = CommonUtils.parseDouble(totalPricePD.readValue(bo), 0D);
            }
            list.add(new BillItem("+ " + payMethod.name, new Money(totalPrice)));
        }
        return list;
    }

    public List<BillItem> buildStat4(BillListDO bo) {
        List<BillItem> list = new ArrayList<>();
        for (EnumPayMethod payMethod : EnumPayMethod.values()) {
            if (payMethod.showActual) {
                Double actualPrice = -1D;
                ReflectionUtils.PropertyDescriptor actualPricePD = getSumActualPricePD(BillListDO.class, payMethod);
                if (actualPricePD != null) {
                    actualPrice = CommonUtils.parseDouble(actualPricePD.readValue(bo), 0D);
                }
                list.add(new BillItem("+ " + payMethod.name + "后台价值", new Money(actualPrice)));
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
}
