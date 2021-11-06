package com.xjh.startup.view;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.dataobject.PrinterTaskDO;
import com.xjh.dao.mapper.PrinterTaskDAO;
import com.xjh.service.domain.PrinterService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.IntStringPair;

import cn.hutool.core.codec.Base64;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import lombok.Data;

public class PrinterCheckOutSettings extends SimpleForm implements Initializable {
    PrinterService printerService = GuiceContainer.getInstance(PrinterService.class);
    PrinterTaskDAO printerTaskDAO = GuiceContainer.getInstance(PrinterTaskDAO.class);

    String taskName = "api.print.task.PrintTaskCheckOutSample";

    @Override
    public void initialize() {
        this.getChildren().clear();
        this.setAlignment(Pos.TOP_LEFT);
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));

        // 初始化
        PrinterTaskDO cond = new PrinterTaskDO();
        cond.setPrintTaskName(taskName);
        PrinterTaskDO initData = printerTaskDAO.selectList(cond).stream().findFirst().orElse(null);
        if (initData == null) {
            AlertBuilder.ERROR("打印任务不存在:api.print.task.PrintTaskCheckOutSample");
            return;
        }
        JSONObject initStrategy = JSONObject.parseObject(Base64.decodeStr(initData.getPrintTaskContent()));
        JSONObject strategy = JSONObject.parseObject(initStrategy.getString("printerSelectStrategy"));
        Integer selectedPrinterId = strategy.getInteger("printerId");
        // 选择打印机
        ComboBox<IntStringPair> printerCombo = loadPrinterOptions();
        IntStringPair.select(printerCombo, selectedPrinterId, null);

        addLine(newCenterLine(new Label("选择打印机:"), printerCombo));
        // 保存
        Button button = new Button("保存配置");
        button.setOnAction(evt -> {
            IntStringPair selected = printerCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertBuilder.ERROR("请选择打印机");
                return;
            }

            JSONObject content = new JSONObject();
            content.put("printerId", selected.getKey());
            content.put("printerName", selected.getValue());
            System.out.println(content);
            // 内容
            JSONObject taskContent = new JSONObject();
            taskContent.put("printerSelectStrategy", content.toJSONString());
            initData.setPrintTaskContent(Base64.encode(taskContent.toJSONString()));
            printerTaskDAO.updateById(initData);
            AlertBuilder.INFO("保存成功");
        });
        addLine(button);
    }

    @Data
    public static class BO {
        BO(Integer deskTypeId, String deskTypeName, ComboBox<IntStringPair> combo) {
            this.deskTypeId = deskTypeId;
            this.deskTypeName = deskTypeName;
            this.printer = combo;
        }

        Integer deskTypeId;
        String deskTypeName;
        ComboBox<IntStringPair> printer;
    }

    private ComboBox<IntStringPair> loadPrinterOptions() {
        List<PrinterDO> printers = printerService.query(new PrinterDO()).getData();
        ObservableList<IntStringPair> options = FXCollections.observableArrayList(printers.stream()
                .map(it -> {
                    IntStringPair pair = new IntStringPair();
                    pair.setKey(it.getPrinterId());
                    pair.setValue(String.format("%s(%s:%s)",
                            it.getPrinterName(), it.getPrinterIp(), it.getPrinterPort()));
                    return pair;
                }).collect(Collectors.toSet()));

        ComboBox<IntStringPair> combo = new ComboBox<>(options);
        IntStringPair.select(combo, null, options.get(0).getKey());
        return combo;
    }
}
