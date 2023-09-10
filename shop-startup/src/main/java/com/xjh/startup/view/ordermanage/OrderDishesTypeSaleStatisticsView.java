package com.xjh.startup.view.ordermanage;


import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.dao.dataobject.Account;
import com.xjh.service.domain.AccountService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.model.DishesSaleStatReq;
import com.xjh.service.domain.model.DishesTypeSaleStatModel;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.common.utils.TableViewUtils.rowIndex;

public class OrderDishesTypeSaleStatisticsView extends SimpleForm implements Initializable {
    AccountService accountService = GuiceContainer.getInstance(AccountService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    ObjectProperty<DishesSaleStatReq> cond = new SimpleObjectProperty<>(new DishesSaleStatReq());
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
            Result<List<DishesTypeSaleStatModel>> rs = orderDishesService.statSalesType(cond.get());
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
        // 时间选择
        HBox dateRangeBlock = new HBox();
        Label dateRangeLabel = new Label("订单日期:");
        DatePicker datePickerStart = new DatePicker(LocalDate.now());
        datePickerStart.setPrefWidth(160);
        DatePicker datePickerEnd = new DatePicker(LocalDate.now());
        datePickerEnd.setPrefWidth(160);
        dateRangeBlock.getChildren().add(newCenterLine(dateRangeLabel, datePickerStart, new Label("至"), datePickerEnd));
        cond.get().setStartDate(LocalDate.now());
        cond.get().setEndDate(LocalDate.now());

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            DishesSaleStatReq q = cond.get().newVer();
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

        HBox line = newCenterLine(dateRangeBlock, queryBtn, new Separator(Orientation.VERTICAL), exportExcel);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        // "编号", "菜品名称", "销售份数", "菜品总金额"
        tableView.getColumns().addAll(newCol("编号", rowIndex(), 160), // 编号
                newCol("菜品分类名称", BO::getDishesTypeName, 360), // 分类名称
                newCol("销售份数", BO::getCount, 150), // 份数
                newCol("菜品总金额", BO::getAllPrice, 150) // 金额
        );

        tableView.setItems(items);
        tableView.setPrefHeight(height);

        addLine(tableView);
    }

    private void buildFoot() {

    }

    public void exportExcel(DishesSaleStatReq req) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择Excel文件");
        chooser.setInitialFileName("菜品类型销售统计.xls");
        chooser.getExtensionFilters().addAll(new ExtensionFilter("XLS", "*.xls"), new ExtensionFilter("XLSX", "*.xlsx"));
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        Result<String> exportRs = exportFile(req, file);
        if (exportRs.isSuccess()) {
            AlertBuilder.INFO("导出文件成功");
        } else {
            AlertBuilder.ERROR(exportRs.getMsg());
        }
    }

    private Result<String> exportFile(DishesSaleStatReq req, File file) {
        DishesSaleStatReq cond = CopyUtils.deepClone(req);
        cond.setPageSize(10000000);
        Result<List<DishesTypeSaleStatModel>> queryRs = orderDishesService.statSalesType(cond);
        if (!queryRs.isSuccess()) {
            return Result.fail(queryRs.getMsg());
        }
        List<DishesTypeSaleStatModel> list = queryRs.getData();
        if (CommonUtils.isEmpty(list)) {
            return Result.fail("没有待导出的数据");
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
            return Result.fail("导出数据异常:" + ex.getMessage());
        }
        return Result.success("");
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
            bo.setDishesTypeName(m.getDishesTypeName());
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
