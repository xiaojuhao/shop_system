package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumPrinterStatus;
import com.xjh.common.enumeration.EnumPrinterType;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.service.domain.PrinterService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class PrinterManageListView extends MediumForm implements Initializable {
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
        List<PrinterDO> list = printerService.query(new PrinterDO()).getData();
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(dd -> {
                BO bo = new BO();
                bo.setPrinterId(dd.getPrinterId());
                bo.setPrinterName(dd.getPrinterName());
                bo.setPrinterIp(dd.getPrinterIp());
                bo.setPrinterPort(dd.getPrinterPort());
                bo.setPrinterInfo(dd.getPrinterInfo());
                bo.setPrinterStatus(EnumPrinterStatus.of(dd.getPrinterStatus()).name);
                bo.setAddTime(dd.getAddTime());
                bo.setPrinterType(EnumPrinterType.of(dd.getPrinterType()).name);
                bo.getOperations().add(new OperationButton("??????", () -> openEditor(dd)));
                bo.getOperations().add(new OperationButton("??????", () -> {
                    OkCancelDialog dialog = new OkCancelDialog(
                            "???????????????", "????????????????????????");
                    Optional<ButtonType> rs = dialog.showAndWait();
                    if (rs.isPresent() && rs.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        Result<Integer> deleteRs = printerService.deleteById(dd.getPrinterId());
                        if (deleteRs.isSuccess()) {
                            AlertBuilder.INFO("????????????");
                        } else {
                            AlertBuilder.ERROR(deleteRs.getMsg());
                        }
                        loadData();
                    }
                }));
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }


    private void buildContent(double height) {

        tableView.getColumns().addAll(
                newCol("ID", "printerId", 100),
                newCol("??????", "printerName", 100),
                newCol("IP", "printerIp", 120),
                newCol("??????", "printerPort", 80),
                newCol("??????", "printerInfo", 100),
                newCol("??????", "printerType", 100),
                newCol("??????", "printerStatus", 100),
                newCol("??????", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        Button addNew = new Button("??? ???");
        addNew.setOnAction(e -> openEditor(new PrinterDO()));
        HBox line = newCenterLine(addNew);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(PrinterDO printer) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "???????????????");
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
        String printerType;
        String printerStatus;
        Long addTime;
        Operations operations = new Operations();
    }
}
