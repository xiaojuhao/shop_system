package com.xjh.startup.view.ordermanage;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.xjh.common.anno.TableItemMark;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.query.DishesQuery;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.DishesEditView;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class OrderManageListView extends SimpleForm implements Initializable {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    ObjectProperty<DishesQuery> cond = new SimpleObjectProperty<>(new DishesQuery());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        PageQueryOrderReq req = new PageQueryOrderReq();
        List<Order> orderList = orderService.pageQuery(req);
        AtomicInteger sno = new AtomicInteger(0);

        Platform.runLater(() -> {
            items.clear();
            items.addAll(orderList.stream().map(this::orderToBO)
                    .peek(it -> it.setSno(sno.incrementAndGet()))
                    .collect(Collectors.toList()));
            tableView.refresh();
        });
    }

    private BO orderToBO(Order order) {
        // "编号", "订单号", "桌号", "下单员工", "就餐人数", "下单时间",
        // "应付款", "菜品总额", "折扣金额", "抹零金额", "退菜金额", "已付金额",
        // "店长减免", "已退现金", "支付状态"
        BO bo = new BO();
        bo.setOrderId(order.getOrderId());
        bo.setDeskName(order.getDeskId() + "");
        bo.setAccountNickname("管理员");
        bo.setOrderCustomerNums(order.getOrderCustomerNums());
        bo.setOrderTime(DateBuilder.base(order.getCreateTime()).timeStr());
        List<OrderDishes> dishesList = orderDishesService.selectByOrderId(order.getOrderId());
        OrderOverviewVO overview = orderService.buildOrderOverview(order, dishesList, null).getData();
        if (overview != null) {
            bo.setDeskName(overview.getDeskName());
            bo.setNeedPayAmt(new Money(overview.getOrderNeedPay()));
            bo.setTotalPayAmt(new Money(overview.getTotalPrice()));
            bo.setReductionAmt(new Money(overview.getOrderReduction()));
            bo.setReturnAmt(new Money(overview.getReturnAmount()));
            bo.setDiscountAmt(new Money(overview.getDiscountAmount()));
            bo.setEraseAmt(new Money(overview.getOrderErase()));
            bo.setPaidAmt(new Money(overview.getOrderHadpaid()));
            bo.setPaymentStatus(overview.getPayStatusName());
        }
        return bo;
    }

    private void buildCond() {
        // name
        HBox nameCondBlock = new HBox();
        Label nameLabel = new Label("名称:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(130);
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, nameInput));

        // status
        HBox statusCondBlock = new HBox();
        Label statusLabel = new Label("状态:");
        ObservableList<String> options = FXCollections.observableArrayList("全部", "上架", "下架");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.getSelectionModel().selectFirst();
        statusCondBlock.getChildren().add(newCenterLine(statusLabel, modelSelect));

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            DishesQuery q = cond.get().newVersion();
            q.setDishesName(CommonUtils.trim(nameInput.getText()));
            String selectedStatus = modelSelect.getSelectionModel().getSelectedItem();
            if (CommonUtils.eq(selectedStatus, "上架")) q.setStatus(1);
            if (CommonUtils.eq(selectedStatus, "下架")) q.setStatus(0);
            cond.set(q);
        });

        Button addNew = new Button("新增菜品");
        addNew.setOnAction(evt -> openEditor(new Dishes()));
        HBox line = newCenterLine(nameCondBlock, statusCondBlock,
                queryBtn,
                new Separator(Orientation.VERTICAL),
                addNew);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        // "编号", "订单号", "桌号", "下单员工", "就餐人数", "下单时间",
        // "应付款", "菜品总额", "折扣金额", "抹零金额", "退菜金额", "已付金额", "店长减免", "已退现金", "支付状态"
        tableView.getColumns().addAll(
                newCol("编号", "sno", 50),
                newCol("订单号", "orderId", 100),
                newCol("桌号", "deskName", 50),
                newCol("下单员工", "accountNickname", 100),
                newCol("就餐人数", "orderCustomerNums", 50),
                newCol("下单时间", "orderTime", 100),
                newCol("应付款", "needPayAmt", 50),
                newCol("菜品总额", "totalPayAmt", 50),
                newCol("折扣金额", "discountAmt", 50),
                newCol("抹零金额", "eraseAmt", 50),
                newCol("退菜金额", "returnAmt", 50),
                newCol("已付金额", "paidAmt", 50),
                newCol("店长减免", "reductionAmt", 50),
                newCol("已退现金", "returnedAmt", 50),
                newCol("支付状态", "paymentStatus", 50),
                newCol("操作", "operations", 100)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        cond.addListener((ob, o, n) -> {
            loadData();
        });
        Button prev = new Button("上一页");
        prev.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            int pageNo = c.getPageNo();
            if (pageNo <= 1) {
                c.setPageNo(1);
            } else {
                c.setPageNo(pageNo - 1);
            }
            cond.set(c);
        });
        Button next = new Button("下一页");
        next.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            c.setPageNo(c.getPageNo() + 1);
            cond.set(c);
        });
        HBox line = newCenterLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑菜品");
        DishesEditView view = new DishesEditView(dishes);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    @Data
    public static class BO {
        // "编号", "订单号", "桌号", "下单员工", "就餐人数", "下单时间",
        // "应付款", "菜品总额", "折扣金额", "抹零金额", "退菜金额", "已付金额", "店长减免", "已退现金", "支付状态"
        @TableItemMark(name = "编号", order = 1, width = 30)
        Integer sno;
        @TableItemMark(name = "订单号", order = 2, width = 100)
        Integer orderId;
        String deskName;
        String accountNickname;
        Integer orderCustomerNums;
        String orderTime;
        Money needPayAmt;
        Money totalPayAmt;
        Money discountAmt;
        Money eraseAmt;
        Money returnAmt;
        Money paidAmt;
        Money reductionAmt;
        Money returnedAmt;
        String paymentStatus;
    }
}
