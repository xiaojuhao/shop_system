package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Printer;
import com.xjh.service.domain.PrinterService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleGridForm;
import com.xjh.startup.view.model.IntStringPair;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrinterEditView extends SimpleGridForm {
    PrinterService printerService = GuiceContainer.getInstance(PrinterService.class);

    Printer printer;
    List<Runnable> collectData = new ArrayList<>();

    public PrinterEditView(Printer param) {
        printer = printerService.getById(param.getPrinterId());
        if (printer == null) {
            printer = new Printer();
        }
        double labelWidth = 120;
        Label nameLabel = createLabel("名称:", labelWidth);
        TextField nameInput = createTextField("名称", 300);
        nameInput.setText(printer.getPrinterName());
        addLine(nameLabel, hbox(nameInput));
        collectData.add(() -> printer.setPrinterName(nameInput.getText()));

        Label ipLabel = createLabel("IP地址:", labelWidth);
        TextField ipInput = new TextField();
        ipInput.setText(printer.getPrinterIp());
        addLine(ipLabel, ipInput);
        collectData.add(() -> printer.setPrinterIp(CommonUtils.trim(ipInput.getText())));

        Label portLabel = createLabel("端口:", labelWidth);
        TextField portInput = new TextField();
        portInput.setText(CommonUtils.stringify(printer.getPrinterPort()));
        addLine(portLabel, portInput);
        collectData.add(() ->
                printer.setPrinterPort(CommonUtils.parseInt(portInput.getText(), null)));

        Label typeLabel = createLabel("类型:", labelWidth);
        ComboBox<IntStringPair> typeInput = printerTypeOptions(printer.getPrinterType());
        addLine(typeLabel, typeInput);
        collectData.add(() -> printer.setPrinterType(getComboValue(typeInput)));

        Label statusLabel = createLabel("状态:", labelWidth);
        ComboBox<IntStringPair> statusInput = printerStatusOptions(printer.getPrinterStatus());
        addLine(statusLabel, statusInput);
        collectData.add(() -> printer.setPrinterStatus(getComboValue(statusInput)));

        Label descLabel = createLabel("打印机描述:", labelWidth);
        TextArea descInput = new TextArea();
        descInput.setText(printer.getPrinterInfo());
        descInput.setPrefHeight(100);
        addLine(descLabel, descInput);
        collectData.add(() -> printer.setPrinterInfo(descInput.getText()));


        /* ************************************************************** *\
         *    保存数据
        \* ************************************************************** */
        Button save = new Button("保 存");
        save.setOnAction(evt -> {
            CommonUtils.safeRun(collectData);
            System.out.println(JSON.toJSONString(printer, true));
            Result<Integer> rs = printerService.save(printer);
            if (rs.isSuccess()) {
                AlertBuilder.INFO("保存成功");
            } else {
                AlertBuilder.ERROR("保存失败," + rs.getMsg());
            }
            this.getScene().getWindow().hide();
        });
        addLine((Node) null, save);
    }

    private ComboBox<IntStringPair> printerTypeOptions(Integer selected) {
        ObservableList<IntStringPair> options = FXCollections.observableArrayList(
                new IntStringPair(0, "58毫米"),
                new IntStringPair(1, "80毫米")
        );
        ComboBox<IntStringPair> combo = new ComboBox<>(options);
        // 默认选中项
        IntStringPair.select(combo, selected, 0);
        return combo;
    }

    private ComboBox<IntStringPair> printerStatusOptions(Integer selected) {
        ObservableList<IntStringPair> options = FXCollections.observableArrayList(
                new IntStringPair(0, "关闭"),
                new IntStringPair(1, "正常")
        );
        ComboBox<IntStringPair> combo = new ComboBox<>(options);
        // 默认选中项
        IntStringPair.select(combo, selected, 1);
        return combo;
    }

    private Integer getComboValue(ComboBox<IntStringPair> combo) {
        if (combo.getSelectionModel().getSelectedItem() == null) {
            return null;
        }
        return combo.getSelectionModel().getSelectedItem().getKey();
    }

}
