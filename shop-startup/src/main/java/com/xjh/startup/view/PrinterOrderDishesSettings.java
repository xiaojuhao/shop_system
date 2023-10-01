package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.model.IntStringPair;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.dataobject.PrinterTaskDO;
import com.xjh.dao.mapper.PrinterTaskDAO;
import com.xjh.service.domain.PrinterService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xjh.service.store.TableViewUtils.newCol;

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
        addLine(newRegularLine(new Label("打印张数:"), printNumInput));
        // 打印机策略
        addLine(new Label("打印机策略:"));

        Map<Integer, Integer> initMap = new HashMap<>();

        PrinterTaskDO initCond = new PrinterTaskDO();
        initCond.setPrintTaskName("api.print.task.PrintTaskOrderSample");
        PrinterTaskDO init = printerTaskDAO.selectList(initCond)
                .stream().findFirst().orElse(null);
        if (init != null) {
            JSONObject initContent = JSON.parseObject(Base64.decodeStr(init.getPrintTaskContent()));
            String printerSelectStrategy = initContent.getString("printerSelectStrategy");
            if (CommonUtils.isNotBlank(printerSelectStrategy)) {
                JSONArray strategy = JSON.parseArray(printerSelectStrategy);
                for (int i = 0; i < strategy.size(); i++) {
                    JSONObject json = strategy.getJSONObject(i);
                    initMap.put(json.getInteger("deskTypeId"), json.getInteger("printerId"));
                }
            }
        }
        ObservableList<BO> deskTypeList = FXCollections.observableArrayList(
                new BO(1, "小句号日料", loadPrinterOptions(initMap.get(1))),
                new BO(2, "借用", loadPrinterOptions(initMap.get(2))),
                new BO(3, "预约", loadPrinterOptions(initMap.get(3)))
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
            try {
                System.out.println("------------- 点餐打印 打印机策略 ----------------");
                JSONArray contents = new JSONArray();
                tv.getItems().forEach(it -> {
                    try{
                        IntStringPair selected = it.getPrinter().getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            JSONObject content = new JSONObject();
                            content.put("deskTypeId", it.getDeskTypeId());
                            content.put("deskTypeName", it.getDeskTypeName());
                            content.put("printerId", selected.getKey());
                            content.put("printerName", selected.getValue());
                            contents.add(content);
                        }
                    }catch (Exception ex){
                        System.out.println("异常:"+ ex.getMessage());
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

                // 内容
                JSONObject taskContent = new JSONObject();
                taskContent.put("printerSelectStrategy", contents.toJSONString());
                task.setPrintTaskContent(Base64.encode(taskContent.toJSONString()));
                System.out.println("保存订单打印配置:" + JSON.toJSONString(task));
                printerTaskDAO.updateById(task);
                AlertBuilder.INFO("保存成功");
            }catch (Exception ee){
                System.out.println("保存订单打印配置-异常:" + ee.getMessage());
            }
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

    private ComboBox<IntStringPair> loadPrinterOptions(Integer selected) {
        List<PrinterDO> printers = printerService.query(new PrinterDO()).getData();
        ObservableList<IntStringPair> options = FXCollections.observableArrayList(printers.stream()
                .map(it -> {
                    String show = String.format("%s(%s:%s)",
                            it.getPrinterName(), it.getPrinterIp(), it.getPrinterPort());
                    return new IntStringPair(it.getPrinterId(), show);
                }).collect(Collectors.toSet()));

        ComboBox<IntStringPair> combo = new ComboBox<>(options);
        IntStringPair.select(combo, selected, options.get(0).getKey());
        return combo;
    }
}
