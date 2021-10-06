package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Data;

public class PrinterOrderDishesSettings extends SimpleForm implements Initializable {
    PrinterService printerService = GuiceContainer.getInstance(PrinterService.class);
    PrinterTaskDAO printerTaskDAO = GuiceContainer.getInstance(PrinterTaskDAO.class);

    @Override
    public void initialize() {
        this.getChildren().clear();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));
        // 打印张数
        TextField printNumInput = new TextField();
        addLine(newLine(new Label("打印张数:"), printNumInput));
        // 打印机策略
        addLine(new Label("打印机策略:"));

        ObservableList<BO> deskTypeList = FXCollections.observableArrayList(
                new BO(1, "小句号日料", loadPrinterOptions()),
                new BO(2, "借用", loadPrinterOptions()),
                new BO(3, "预约", loadPrinterOptions())
        );
        TableView<BO> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("ID", "deskTypeId", 100),
                newCol("餐桌类型", "deskTypeName", 150),
                newCol("指定打印机", "printer", 250)
        );
        tv.setItems(deskTypeList);
        tv.refresh();
        addLine(tv);
        // 保存
        Button button = new Button("保存配置");
        button.setOnAction(evt -> {
            JSONArray contents = new JSONArray();
            tv.getItems().forEach(it -> {
                IntStringPair selected = it.getPrinter().getSelectionModel().getSelectedItem();
                if (selected != null) {
                    JSONObject content = new JSONObject();
                    content.put("deskTypeId", it.getDeskTypeId());
                    content.put("deskTypeName", it.getDeskTypeName());
                    content.put("printerId", selected.getKey());
                    content.put("printerName", selected.getValue());
                    contents.add(content);
                }
            });
            PrinterTaskDO cond = new PrinterTaskDO();
            cond.setPrintTaskName("api.print.task.PrintTaskOrderSample");
            PrinterTaskDO task = printerTaskDAO.selectList(cond)
                    .stream().findFirst().orElse(null);
            if (task == null) {
                AlertBuilder.ERROR("打印任务不存在:api.print.task.PrintTaskOrderSample");
                return;
            }
            System.out.println(contents);
            // 内容
            JSONObject taskContent = new JSONObject();
            taskContent.put("printerSelectStrategy", contents.toJSONString());
            task.setPrintTaskContent(Base64.encode(taskContent.toJSONString()));
            printerTaskDAO.updateById(task);
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
