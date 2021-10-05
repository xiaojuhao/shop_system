package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.dao.dataobject.Printer;
import com.xjh.service.domain.PrinterService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class PrinterManageListView extends SimpleForm implements Initializable {
    PrinterService printerService = GuiceContainer.getInstance(PrinterService.class);

    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        List<Printer> list = printerService.query(new Printer()).getData();
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(dd -> {
                BO bo = new BO();
                bo.setPrinterId(dd.getPrinterId());
                bo.setPrinterName(dd.getPrinterName());
                bo.setPrinterIp(dd.getPrinterIp());
                bo.setPrinterPort(dd.getPrinterPort());
                bo.setPrinterStatus(dd.getPrinterStatus());
                bo.setAddTime(dd.getAddTime());
                bo.setPrinterType(dd.getPrinterType());
                bo.getOperations().add(new OperationButton("编辑", () -> openEditor(dd)));
                bo.getOperations().add(new OperationButton("删除", () -> {

                }));
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }


    private void buildContent(double height) {

        tableView.getColumns().addAll(
                newCol("ID", "printerId", 100),
                newCol("名称", "printerName", 200),
                newCol("IP", "printerIp", 200),
                newCol("端口", "printerPort", 80),
                newCol("备注", "printerInfo", 100),
                newCol("类型", "printerType", 100),
                newCol("状态", "printerStatus", 100),
                newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        Button addNew = new Button("增 加");
        addNew.setOnAction(e -> openEditor(new Printer()));
        HBox line = newLine(addNew);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(Printer printer) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑打印机");
        PrinterEditView view = new PrinterEditView(printer);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    @Data
    public static class BO {
        Integer printerId;
        String printerName;
        String printerIp;
        Integer printerPort;
        String printerInfo;
        // 1:80mm;  0:58mm
        Integer printerType;
        Integer printerStatus;
        Long addTime;
        Operations operations = new Operations();
    }
}
