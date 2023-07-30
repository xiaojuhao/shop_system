package com.xjh.startup.view.ordermanage;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.ReturnReasonDO;
import com.xjh.dao.mapper.ReturnReasonDAO;
import com.xjh.dao.query.ReturnReasonQuery;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class OrderManageReturnedListView extends SimpleForm implements Initializable {
    ReturnReasonDAO returnReasonDAO = GuiceContainer.getInstance(ReturnReasonDAO.class);

    ObjectProperty<ReturnReasonQuery> cond = new SimpleObjectProperty<>(new ReturnReasonQuery());
    TableView<ReturnReasonDO> tableView = new TableView<>();

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
            List<ReturnReasonDO> orderList = returnReasonDAO.selectList(cond.get()).getData();
            tableView.getItems().clear();
            tableView.getItems().addAll(orderList);
            tableView.refresh();
        });
    }

    private void buildCond() {
        cond.addListener((ob, o, n) -> loadData());
        // 时间选择
        HBox dateRangeBlock = new HBox();
        Label dateRangeLabel = new Label("日期:");
        DatePicker datePickerStart = new DatePicker(LocalDate.now());
        datePickerStart.setPrefWidth(150);
        DatePicker datePickerEnd = new DatePicker(LocalDate.now());
        datePickerEnd.setPrefWidth(150);
        dateRangeBlock.getChildren().add(newCenterLine(dateRangeLabel,
                datePickerStart,
                new Label("至"),
                datePickerEnd));
        cond.get().setStartDate(LocalDate.now());
        cond.get().setEndDate(LocalDate.now());

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            ReturnReasonQuery q = cond.get().newVer();
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

        HBox line = newCenterLine(
                dateRangeBlock,
                queryBtn,
                new Separator(Orientation.VERTICAL),
                exportExcel);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        tableView.getColumns().addAll(
                newCol("桌号", ReturnReasonDO::getDeskName, 30),
                newCol("菜品名称", ReturnReasonDO::getDishesName, 250),
                newCol("退菜原因", ReturnReasonDO::getReturnReason, 200),
                newCol("退菜时间", it -> DateBuilder.base(it.getAddtime()).timeStr(), 200)
        );
        tableView.setPrefHeight(height);
        addLine(tableView);
        loadData();
    }

    private void buildFoot() {
        Button prev = new Button("上一页");
        prev.setOnAction(e -> {
            ReturnReasonQuery c = cond.get().newVer();
            c.decreasePageNo();
            cond.set(c);
        });
        Button next = new Button("下一页");
        next.setOnAction(e -> {
            ReturnReasonQuery c = cond.get().newVer();
            c.increasePageNo();
            cond.set(c);
        });
        HBox line = newCenterLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    public void exportExcel(ReturnReasonQuery req) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择Excel文件");
        chooser.setInitialFileName("return_reasons.xls");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("XLS", "*.xls"),
                new ExtensionFilter("XLSX", "*.xlsx")
        );
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        exportFile(req, file);

        AlertBuilder.INFO("导出文件成功");
    }

    private void exportFile(ReturnReasonQuery req, File file) {
        ReturnReasonQuery cond = CopyUtils.deepClone(req);
        cond.setPageSize(10000000);
        List<ReturnReasonDO> list = returnReasonDAO.selectList(cond).getData();

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
        cell.setCellValue("菜品名称");
        cell.setCellStyle(style);
        cell = row.createCell(3);
        cell.setCellValue("退菜原因");
        cell.setCellStyle(style);
        cell = row.createCell(4);
        cell.setCellValue("退菜时间");
        cell.setCellStyle(style);

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow((int) i + 1);
            ReturnReasonDO dd = list.get(i);
            int col = -1;
            row.createCell(++col).setCellValue(dd.getOrderId());
            row.createCell(++col).setCellValue(dd.getDeskName());
            row.createCell(++col).setCellValue(dd.getDishesName());
            row.createCell(++col).setCellValue(dd.getReturnReason());
            row.createCell(++col).setCellValue(DateBuilder.base(dd.getAddtime()).timeStr());
        }
        // 第六步，将文件存到指定位置
        try (FileOutputStream fout = new FileOutputStream(file)) {
            wb.write(fout);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
