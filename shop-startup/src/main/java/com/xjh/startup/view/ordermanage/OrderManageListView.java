package com.xjh.startup.view.ordermanage;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.xjh.common.anno.TableItemMark;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.AccountService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.DishesEditView;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.IntStringPair;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class OrderManageListView extends SimpleForm implements Initializable {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    AccountService accountService = GuiceContainer.getInstance(AccountService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

    ObjectProperty<PageQueryOrderReq> cond = new SimpleObjectProperty<>(new PageQueryOrderReq());
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
        List<Order> orderList = orderService.pageQuery(cond.get());
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
            bo.setReturnDishesPrice(new Money(overview.getReturnDishesPrice()));
            bo.setDiscountAmt(new Money(overview.getDiscountAmount()));
            bo.setEraseAmt(new Money(overview.getOrderErase()));
            bo.setPaidAmt(new Money(overview.getOrderHadpaid()));
            bo.setPaymentStatus(overview.getPayStatusName());
        }
        return bo;
    }

    private void buildCond() {
        cond.addListener((ob, o, n) -> loadData());
        // name
        Account noAccount = new Account();
        noAccount.setAccountNickName("全部");
        List<Account> accountList = accountService.listAll();
        accountList.add(0, noAccount);
        HBox nameCondBlock = new HBox();
        ObservableList<IntStringPair> accountOptions = FXCollections.observableArrayList(
                accountList.stream().map(it -> new IntStringPair(it.getAccountId(), it.getAccountNickName()))
                        .collect(Collectors.toList())
        );
        ComboBox<IntStringPair> accountSelect = new ComboBox<>(accountOptions);
        accountSelect.getSelectionModel().selectFirst();
        Label nameLabel = new Label("业务员:");
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, accountSelect));
        // desk列表
        HBox deskCondBlock = new HBox();
        List<Desk> deskList = deskService.getAllDesks();
        Desk noDesk = new Desk();
        noDesk.setDeskName("全部");
        deskList.add(0, noDesk);
        ObservableList<IntStringPair> desksOptions = FXCollections.observableArrayList(
                deskList.stream().map(it -> new IntStringPair(it.getDeskId(), it.getDeskName())).collect(Collectors.toList())
        );
        ComboBox<IntStringPair> deskCombo = new ComboBox<>(desksOptions);
        deskCombo.getSelectionModel().selectFirst();
        Label deskLabel = new Label("桌号:");
        deskCondBlock.getChildren().add(newCenterLine(deskLabel, deskCombo));
        // 时间选择
        HBox dateRangeBlock = new HBox();
        Label dateRangeLabel = new Label("订单日期:");
        DatePicker datePickerStart = new DatePicker(LocalDate.now());
        datePickerStart.setPrefWidth(120);
        DatePicker datePickerEnd = new DatePicker(LocalDate.now());
        datePickerEnd.setPrefWidth(120);
        dateRangeBlock.getChildren().add(newCenterLine(dateRangeLabel,
                datePickerStart,
                new Label("至"),
                datePickerEnd));
        cond.get().setStartDate(LocalDate.now());
        cond.get().setEndDate(LocalDate.now());

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            PageQueryOrderReq q = cond.get().newVer();
            IntStringPair selectedAccount = accountSelect.getSelectionModel().getSelectedItem();
            if (selectedAccount != null) {
                q.setAccountId(selectedAccount.getKey());
            } else {
                q.setAccountId(null);
            }
            IntStringPair selectedDesk = deskCombo.getSelectionModel().getSelectedItem();
            if (selectedDesk != null) {
                q.setDeskId(selectedDesk.getKey());
            } else {
                q.setDeskId(null);
            }
            if (datePickerStart.getValue() != null) {
                q.setStartDate(datePickerStart.getValue());
            }
            if (datePickerEnd.getValue() != null) {
                q.setEndDate(datePickerEnd.getValue());
            }
            cond.set(q);
        });

        Button addNew = new Button("新增菜品");
        addNew.setOnAction(evt -> openEditor(new Dishes()));
        HBox line = newCenterLine(
                nameCondBlock,
                deskCondBlock,
                dateRangeBlock,
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
                newCol("下单员工", "accountNickname", 60),
                newCol("就餐人数", "orderCustomerNums", 50),
                newCol("下单时间", "orderTime", 150),
                newCol("应付款", "needPayAmt", 50),
                newCol("菜品总额", "totalPayAmt", 50),
                newCol("折扣金额", "discountAmt", 50),
                newCol("抹零金额", "eraseAmt", 50),
                newCol("退菜金额", "returnDishesPrice", 50),
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
        Button prev = new Button("上一页");
        prev.setOnMouseClicked(e -> {
            PageQueryOrderReq c = cond.get().newVer();
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
            PageQueryOrderReq c = cond.get().newVer();
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
        Money returnDishesPrice;
        Money paidAmt;
        Money reductionAmt;
        Money returnedAmt;
        String paymentStatus;
    }
}
