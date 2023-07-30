package com.xjh.startup.view.ordermanage;


import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.query.PageQueryOrderReq;
import com.xjh.service.domain.AccountService;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.model.DishesSaleStatReq;
import com.xjh.service.domain.model.DishesTypeSaleStatModel;
import com.xjh.startup.foundation.ioc.GuiceContainer;
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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import lombok.Data;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.common.utils.TableViewUtils.rowIndex;

public class OrderDishesTypeSaleStatisticsView extends SimpleForm implements Initializable {
    AccountService accountService = GuiceContainer.getInstance(AccountService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);

    ObjectProperty<PageQueryOrderReq> cond = new SimpleObjectProperty<>(new PageQueryOrderReq());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();
    Map<Integer, Account> accountMap = new HashMap<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        accountService.listAll().forEach(account -> accountMap.put(account.getAccountId(), account));
        //
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        Platform.runLater(() -> {
            Result<List<DishesTypeSaleStatModel>> rs = orderDishesService.statSalesType(new DishesSaleStatReq());
            if (!rs.isSuccess()) {
                AlertBuilder.ERROR(rs.getMsg());
                return;
            }
            List<DishesTypeSaleStatModel> list = rs.getData();
            items.clear();
            items.addAll(BO.convertListToBO(list));
            tableView.refresh();
        });
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
        dateRangeBlock.getChildren().add(newCenterLine(dateRangeLabel, datePickerStart, new Label("至"), datePickerEnd));
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

        HBox line = newCenterLine(nameCondBlock, deskCondBlock, dateRangeBlock, queryBtn, new Separator(Orientation.VERTICAL), exportExcel, showBill);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        // "编号", "菜品名称", "销售份数", "菜品总金额"
        tableView.getColumns().addAll(newCol("编号", rowIndex(), 30), // 编号
                newCol("菜品分类名称", BO::getDishesTypeName, 100), // 分类名称
                newCol("销售份数", BO::getCount, 30), // 份数
                newCol("菜品总金额", BO::getAllPrice, 60) // 金额
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
        chooser.getExtensionFilters().addAll(new ExtensionFilter("XLS", "*.xls"), new ExtensionFilter("XLSX", "*.xlsx"));
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        exportFile(req, file);
        AlertBuilder.INFO("导出文件成功");
    }

    private void exportFile(PageQueryOrderReq req, File file) {
        PageQueryOrderReq cond = CopyUtils.deepClone(req);
        cond.setPageSize(10000000);
        Result<List<DishesTypeSaleStatModel>> queryRs = orderDishesService.statSalesType(new DishesSaleStatReq());
        if (!queryRs.isSuccess()) {
            AlertBuilder.ERROR(queryRs.getMsg());
            return;
        }
        List<DishesTypeSaleStatModel> list = queryRs.getData();
        if (CommonUtils.isEmpty(list)) {
            AlertBuilder.ERROR("没有待导出的数据");
            return;
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("订单列表");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("编号");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("菜品分类名称");
        cell.setCellStyle(style);
        cell = row.createCell(2);
        cell.setCellValue("销售份数");
        cell.setCellStyle(style);
        cell = row.createCell(3);
        cell.setCellValue("菜品总金额");
        cell.setCellStyle(style);

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            DishesTypeSaleStatModel data = list.get(i);
            BO bo = BO.convertToBO(data);
            bo.setSno(i + 1);
            int col = -1;
            row.createCell(++col).setCellValue(bo.getSno());
            row.createCell(++col).setCellValue(bo.getDishesTypeName());
            row.createCell(++col).setCellValue(bo.getCount());
            row.createCell(++col).setCellValue(bo.getAllPrice().toString());
        }
        // 第六步，将文件存到指定位置
        try (FileOutputStream fout = new FileOutputStream(file)) {
            wb.write(fout);
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertBuilder.ERROR(ex.getMessage());
        }
    }

    private ComboBox<IntStringPair> buildDeskCombo() {
        List<Desk> deskList = deskService.getAllDesks();
        Desk noDesk = new Desk();
        noDesk.setDeskName("全部");
        deskList.add(0, noDesk);
        ObservableList<IntStringPair> desksOptions = FXCollections.observableArrayList(deskList.stream().map(it -> new IntStringPair(it.getDeskId(), it.getDeskName())).collect(Collectors.toList()));
        ComboBox<IntStringPair> deskCombo = new ComboBox<>(desksOptions);
        deskCombo.getSelectionModel().selectFirst();
        return deskCombo;
    }

    private ComboBox<IntStringPair> buildAccountComBox() {
        Account noAccount = new Account();
        noAccount.setAccountNickName("全部");
        List<Account> accountList = accountService.listAll();
        accountList.add(0, noAccount);

        ObservableList<IntStringPair> accountOptions = FXCollections.observableArrayList(accountList.stream().map(it -> new IntStringPair(it.getAccountId(), it.getAccountNickName())).collect(Collectors.toList()));
        ComboBox<IntStringPair> accountSelect = new ComboBox<>(accountOptions);
        accountSelect.getSelectionModel().selectFirst();
        return accountSelect;
    }

    @Data
    public static class BO {
        // "编号", "菜品名称", "销售份数", "菜品总金额"
        Integer sno;
        String dishesTypeName;
        Integer count;
        Money allPrice = new Money(0D);

        public static BO convertToBO(DishesTypeSaleStatModel m) {
            BO bo = new BO();
            bo.setDishesTypeName(m.getDishesTypeId().toString());
            bo.setCount(m.getCount());
            bo.setAllPrice(new Money(m.getAllPrice()));
            return bo;
        }

        public static List<BO> convertListToBO(List<DishesTypeSaleStatModel> list) {
            List<BO> rlist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                BO bo = convertToBO(list.get(i));
                bo.setSno(i + 1);
                rlist.add(bo);
            }
            return rlist;
        }
    }
}
