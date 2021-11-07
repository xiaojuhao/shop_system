package com.xjh.startup.view.ordermanage;


import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.common.utils.TableViewUtils.rowIndex;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.AccountService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.OkCancelDialog;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        Platform.runLater(() -> {
            List<Order> orderList = orderService.pageQuery(cond.get());
            items.clear();
            items.addAll(CommonUtils.collect(orderList, this::orderToBO));
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
            bo.setTotalPrice(new Money(overview.getTotalPrice()));
            bo.setReductionAmt(new Money(overview.getOrderReduction()));
            bo.setReturnDishesPrice(new Money(overview.getReturnDishesPrice()));
            bo.setDiscountAmt(new Money(overview.getDiscountAmount()));
            bo.setEraseAmt(new Money(overview.getOrderErase()));
            bo.setPaidAmt(new Money(overview.getOrderHadpaid()));
            // bo.setPaymentStatus(overview.getPayStatusName());
            bo.setPaymentStatus(EnumOrderStatus.of(order.getOrderStatus()).remark);
            bo.setReturnedCash(new Money(overview.getReturnedCash()));
        }
        return bo;
    }

    private void buildCond() {
        cond.addListener((ob, o, n) -> loadData());
        // name
        HBox nameCondBlock = new HBox();
        Label nameLabel = new Label("业务员:");
        ComboBox<IntStringPair> accountComBox = buildAccountComBox();
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, accountComBox));
        // desk列表
        HBox deskCondBlock = new HBox();
        Label deskLabel = new Label("桌号:");
        ComboBox<IntStringPair> deskCombo = buildDeskCombo();
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
            IntStringPair selectedAccount = accountComBox.getSelectionModel().getSelectedItem();
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

        Button exportExcel = new Button("导出EXCEL");
        exportExcel.setOnAction(evt -> exportExcel(cond.get()));

        Button showBill = new Button("查看报表");
        showBill.setOnAction(evt -> showBill(cond.get()));

        Button modifyCustNums = new Button("修改就餐人数");
        modifyCustNums.setOnAction(evt -> doModifyOrderCustNum());

        Button specialOperations = new Button("特殊操作");
        specialOperations.setOnAction(evt -> showSpecialOperations());
        HBox line = newCenterLine(
                nameCondBlock,
                deskCondBlock,
                dateRangeBlock,
                queryBtn,
                new Separator(Orientation.VERTICAL),
                exportExcel,
                showBill,
                modifyCustNums,
                specialOperations);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        // "编号", "订单号", "桌号", "下单员工", "就餐人数", "下单时间",
        // "应付款", "菜品总额", "折扣金额", "抹零金额", "退菜金额", "已付金额", "店长减免", "已退现金", "支付状态"
        tableView.getColumns().addAll(
                newCol("编号", rowIndex(), 30),
                newCol("订单号", BO::getOrderId, 100),
                newCol("桌号", BO::getDeskName, 30),
                newCol("下单员工", BO::getAccountNickname, 60),
                newCol("就餐人数", BO::getOrderCustomerNums, 30),
                newCol("下单时间", BO::getOrderTime, 150),
                newCol("应付款", BO::getNeedPayAmt, 50),
                newCol("菜品总额", BO::getTotalPrice, 50),
                newCol("折扣金额", BO::getDiscountAmt, 50),
                newCol("抹零金额", BO::getEraseAmt, 50),
                newCol("退菜金额", BO::getReturnDishesPrice, 50),
                newCol("已付金额", BO::getPaidAmt, 50),
                newCol("店长减免", BO::getReductionAmt, 50),
                newCol("已退现金", BO::getReturnedCash, 50),
                newCol("支付状态", BO::getPaymentStatus, 50)
        );
        tableView.setRowFactory(tv -> {
            TableRow<BO> row = new TableRow<>();
            row.setOnMouseClicked(clickEvt -> {
                if (clickEvt.getClickCount() == 2 && !row.isEmpty()) {
                    Stage stage = new Stage();
                    stage.initOwner(this.getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.centerOnScreen();
                    stage.setWidth(this.getWidth() * 0.98);
                    stage.setHeight(this.getHeight() * 0.98);
                    stage.setTitle("明细");
                    stage.setScene(new Scene(new OrderManageDetailShowView(row.getItem())));
                    stage.showAndWait();
                }
            });
            return row;
        });
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
        Label remark = new Label("双击查看订单明细");
        remark.setPadding(new Insets(0, 0, 0, 150));
        remark.setTextFill(Color.RED);
        HBox line = newCenterLine(prev, next, remark);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    public void showBill(PageQueryOrderReq req) {
        ModelWindow mw = new ModelWindow(this.getScene().getWindow());
        mw.setHeight(1000);
        mw.setScene(new Scene(new OrderManageBillView(req, mw)));
        mw.showAndWait();
    }

    public void exportExcel(PageQueryOrderReq req) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择Excel文件");
        chooser.setInitialFileName("hello.xls");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("XLS", "*.xls"),
                new ExtensionFilter("XLSX", "*.xlsx")
        );
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        if (file.exists()) {
            OkCancelDialog dialog = new OkCancelDialog("文件选择", "文件已存在，是否覆盖当前文件？");
            Optional<ButtonType> rs = dialog.showAndWait();
            if (rs.isPresent() && rs.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                exportFile(req, file);
            }
        } else {
            exportFile(req, file);
        }
    }

    private void exportFile(PageQueryOrderReq req, File file) {
        PageQueryOrderReq cond = CopyUtils.deepClone(req);
        cond.setPageSize(10000000);
        List<Order> ordersAll = orderService.pageQuery(req);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("订单列表");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("订单号");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("桌号");
        cell.setCellStyle(style);
        cell = row.createCell(2);
        cell.setCellValue("下单员工");
        cell.setCellStyle(style);
        cell = row.createCell(3);
        cell.setCellValue("就餐人数");
        cell.setCellStyle(style);
        cell = row.createCell(4);
        cell.setCellValue("下单时间");
        cell.setCellStyle(style);
        cell = row.createCell(5);
        cell.setCellValue("应付款");
        cell.setCellStyle(style);
        cell = row.createCell(6);
        cell.setCellValue("菜品总额");
        cell.setCellStyle(style);
        cell = row.createCell(7);
        cell.setCellValue("折扣金额");
        cell.setCellStyle(style);
        cell = row.createCell(8);
        cell.setCellValue("抹零金额");
        cell.setCellStyle(style);
        cell = row.createCell(9);
        cell.setCellValue("退菜金额");
        cell.setCellStyle(style);
        cell = row.createCell(10);
        cell.setCellValue("已付金额");
        cell.setCellStyle(style);
        cell = row.createCell(11);
        cell.setCellValue("店长减免");
        cell.setCellStyle(style);
        cell = row.createCell(12);
        cell.setCellValue("支付状态");
        cell.setCellStyle(style);

        for (int i = 0; i < ordersAll.size(); i++) {
            row = sheet.createRow((int) i + 1);
            Order order = ordersAll.get(i);
            BO bo = this.orderToBO(order);
            int col = -1;
            row.createCell(++col).setCellValue(order.getOrderId());
            row.createCell(++col).setCellValue(bo.getDeskName());
            row.createCell(++col).setCellValue(bo.getAccountNickname());
            row.createCell(++col).setCellValue(order.getOrderCustomerNums());
            row.createCell(++col).setCellValue(DateBuilder.base(order.getCreateTime()).timeStr());
            row.createCell(++col).setCellValue(bo.getNeedPayAmt().toString());
            row.createCell(++col).setCellValue(bo.getTotalPrice().toString());
            row.createCell(++col).setCellValue(bo.getDiscountAmt().toString());
            row.createCell(++col).setCellValue(bo.getEraseAmt().toString());
            row.createCell(++col).setCellValue(bo.getReturnDishesPrice().toString());
            row.createCell(++col).setCellValue(bo.getPaidAmt().toString());
            row.createCell(++col).setCellValue(bo.getReductionAmt().toString());
            row.createCell(++col).setCellValue(EnumOrderStatus.of(order.getOrderStatus()).remark);
        }
        // 第六步，将文件存到指定位置
        try (FileOutputStream fout = new FileOutputStream(file)) {
            wb.write(fout);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doModifyOrderCustNum() {
        BO bo = tableView.getSelectionModel().getSelectedItem();
        if (bo == null) {
            AlertBuilder.ERROR("请选择操作订单");
            return;
        }
        ModelWindow mw = new ModelWindow(this.getScene().getWindow());
        mw.setHeight(200);
        mw.setWidth(300);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        form.setPadding(new Insets(20, 0, 0, 0));
        mw.setScene(new Scene(form));
        form.addLine(newCenterLine(
                new Label("订单号:"),
                new Label(bo.getOrderId().toString())
        ));
        TextField customerNum = createTextField("就餐人数");
        form.addLine(newCenterLine(
                new Label("就餐人数:"),
                customerNum
        ));
        Button button = new Button("修改");
        form.addLine(newCenterLine(button));
        button.setOnAction(evt -> {
            int custNum = CommonUtils.parseInt(customerNum.getText(), 0);
            if (custNum <= 0) {
                AlertBuilder.ERROR("请输入就餐人数");
                return;
            }
            Order order = new Order();
            order.setOrderId(bo.getOrderId());
            order.setOrderCustomerNums(custNum);
            orderService.updateByOrderId(order);
            mw.close();
            loadData();
            AlertBuilder.INFO("修改就餐人数", "修改成功");
        });
        mw.showAndWait();
    }

    public void showOrderStatusChangeWindow(Window parent) {
        BO bo = tableView.getSelectionModel().getSelectedItem();
        if (bo == null) {
            AlertBuilder.ERROR("请选择操作订单");
            return;
        }
        ModelWindow mw = new ModelWindow(parent);
        mw.setHeight(200);
        mw.setWidth(300);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        form.setPadding(new Insets(20, 0, 0, 0));
        mw.setScene(new Scene(form));
        form.addLine(newCenterLine(new Label("订单号:"), new Label(bo.getOrderId().toString())));
        TextField returnMoney = createTextField("退现金额");
        TextField returnReason = createTextField("退现原因");
        form.addLine(newCenterLine(new Label("退现金额:"), returnMoney));
        form.addLine(newCenterLine(new Label("退现原因:"), returnReason));
        Button button = new Button("提 交");
        form.addLine(newCenterLine(button));
        button.setOnAction(evt -> {
            String reasonText = returnReason.getText();
            Double returnAmount = CommonUtils.parseMoney(returnMoney.getText(), null);
            if (returnAmount == null || returnAmount <= 0.0001) {
                AlertBuilder.ERROR("请输入退现金额");
                return;
            }
            if (CommonUtils.isBlank(reasonText)) {
                AlertBuilder.ERROR("请输入退现原因");
                return;
            }
            Order order = orderService.getOrder(bo.getOrderId());
            double totalReturn = returnAmount;
            if (order.getOrderReturnCash() != null) {
                totalReturn += order.getOrderReturnCash();
            }
            // 更新数据库
            Order update = new Order();
            update.setOrderId(bo.getOrderId());
            update.setOrderReturnCash(new BigDecimal(totalReturn)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue());
            update.setReturnCashReason(reasonText);
            orderService.updateByOrderId(update);
            // reload
            loadData();
            // 关闭对话框
            AlertBuilder.INFO("订单退款", "设置退款成功");

            mw.close();
            parent.hide();
        });
        mw.showAndWait();
    }

    public void showSpecialOperations() {
        BO bo = tableView.getSelectionModel().getSelectedItem();
        if (bo == null) {
            AlertBuilder.ERROR("请选择操作订单");
            return;
        }
        ModelWindow mw = new ModelWindow(this.getScene().getWindow());
        mw.setHeight(200);
        mw.setWidth(300);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        form.setPadding(new Insets(20, 0, 0, 0));
        mw.setScene(new Scene(form));
        form.addLine(newCenterLine(new Label("订单号:"), new Label(bo.getOrderId().toString())));
        //
        Button escape = new Button("逃单");
        escape.setTextFill(Color.RED);
        escape.setOnAction(evt -> {
            orderService.escapeOrder(bo.getOrderId());
            loadData();
            AlertBuilder.INFO("设置逃单成功");
            mw.close();
        });
        Button free = new Button("免单");
        free.setTextFill(Color.RED);
        free.setOnAction(evt -> {
            orderService.freeOrder(bo.getOrderId());
            loadData();
            AlertBuilder.INFO("设置免单成功");
            mw.close();
        });
        Button changePayStatus = new Button("已付款");
        form.addLine(newCenterLine(escape, free, changePayStatus));
        changePayStatus.setOnAction(evt -> {
            orderService.changeOrderToPaid(bo.getOrderId());
            loadData();
            AlertBuilder.INFO("设置订单已付款成功");
            mw.close();
        });
        //
        Button returnMoney = new Button("退现金");
        returnMoney.setOnAction(evt -> showOrderStatusChangeWindow(mw));
        Button recoverOrderStatus = new Button("恢复已关台订单");
        recoverOrderStatus.setOnAction(evt -> {
            Result<Integer> rs = orderService.recoverOrder(bo.getOrderId());
            if (!rs.isSuccess()) {
                AlertBuilder.ERROR("错误提示", rs.getMsg());
            } else {
                AlertBuilder.INFO("恢复关台成功!");
                mw.close();
            }
        });

        form.addLine(newCenterLine(returnMoney, recoverOrderStatus));

        mw.showAndWait();
    }

    private ComboBox<IntStringPair> buildDeskCombo() {
        List<Desk> deskList = deskService.getAllDesks();
        Desk noDesk = new Desk();
        noDesk.setDeskName("全部");
        deskList.add(0, noDesk);
        ObservableList<IntStringPair> desksOptions = FXCollections.observableArrayList(
                deskList.stream().map(it -> new IntStringPair(it.getDeskId(), it.getDeskName())).collect(Collectors.toList())
        );
        ComboBox<IntStringPair> deskCombo = new ComboBox<>(desksOptions);
        deskCombo.getSelectionModel().selectFirst();
        return deskCombo;
    }

    private ComboBox<IntStringPair> buildAccountComBox() {
        Account noAccount = new Account();
        noAccount.setAccountNickName("全部");
        List<Account> accountList = accountService.listAll();
        accountList.add(0, noAccount);

        ObservableList<IntStringPair> accountOptions = FXCollections.observableArrayList(
                accountList.stream()
                        .map(it -> new IntStringPair(it.getAccountId(), it.getAccountNickName()))
                        .collect(Collectors.toList())
        );
        ComboBox<IntStringPair> accountSelect = new ComboBox<>(accountOptions);
        accountSelect.getSelectionModel().selectFirst();
        return accountSelect;
    }

    @Data
    public static class BO {
        // "编号", "订单号", "桌号", "下单员工", "就餐人数", "下单时间",
        // "应付款", "菜品总额", "折扣金额", "抹零金额", "退菜金额", "已付金额", "店长减免", "已退现金", "支付状态"
        Integer orderId;
        String deskName;
        String accountNickname;
        Integer orderCustomerNums = 0;
        String orderTime;
        Money needPayAmt = new Money(0D);
        Money totalPrice = new Money(0D);
        Money discountAmt = new Money(0D);
        Money eraseAmt = new Money(0D);
        Money returnDishesPrice = new Money(0D);
        Money paidAmt = new Money(0D);
        Money reductionAmt = new Money(0D);
        Money returnedCash = new Money(0D);
        String paymentStatus;


    }
}
