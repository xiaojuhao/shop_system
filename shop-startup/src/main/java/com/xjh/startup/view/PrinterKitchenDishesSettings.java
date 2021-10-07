package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.dataobject.PrinterDishDO;
import com.xjh.dao.mapper.PrinterDishDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import lombok.Data;

public class PrinterKitchenDishesSettings extends SimpleForm {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    PrinterDishDAO printerDishDAO = GuiceContainer.getInstance(PrinterDishDAO.class);

    ObservableList<BO> leftList = FXCollections.observableArrayList();
    ObservableList<BO> rightList = FXCollections.observableArrayList();

    public PrinterKitchenDishesSettings(PrinterDO printer) {
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        //
        List<Dishes> allDishes = dishesService.getAllDishes();
        List<PrinterDishDO> printerDishList = printerDishDAO.selectList(new PrinterDishDO());
        Set<Integer> printerDishIds = CommonUtils.collectSet(printerDishList, it -> {
            if (it.getPrinterId().equals(printer.getPrinterId())) {
                return it.getDishId();
            } else {
                return null;
            }
        });
        Set<Integer> allPrinterDishIds = CommonUtils.collectSet(printerDishList, PrinterDishDO::getDishId);
        CommonUtils.forEach(allDishes, d -> {
            if (allPrinterDishIds.contains(d.getDishesId())) {
                if (printerDishIds.contains(d.getDishesId()))
                    leftList.add(new BO(d.getDishesId(), d.getDishesName()));
            } else {
                rightList.add(new BO(d.getDishesId(), d.getDishesName()));
            }
        });
        this.getChildren().add(new Label("关联菜单:" + printer.getPrinterName()));
        ListView<BO> left = new ListView<>();
        left.setItems(leftList);
        left.setPrefHeight(500);
        left.setPrefWidth(300);
        left.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<BO> right = new ListView<>();
        right.setPrefHeight(500);
        right.setPrefWidth(300);
        right.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        right.setItems(rightList);

        VBox operations = new VBox();
        operations.setAlignment(Pos.CENTER);
        operations.setSpacing(30);
        Button addTo = new Button(" <-- ");
        Button removeFrom = new Button(" --> ");
        operations.getChildren().addAll(addTo, removeFrom);

        removeFrom.setOnAction(evt -> {
            ObservableList<BO> selected = left.getSelectionModel().getSelectedItems();
            for (BO bo : selected) {
                right.getItems().add(new BO(bo.getDishesId(), bo.getDishesName()));
            }
            left.getItems().removeAll(selected);
            right.refresh();
            left.refresh();
        });

        addTo.setOnAction(evt -> {
            ObservableList<BO> selected = right.getSelectionModel().getSelectedItems();
            for (BO bo : selected) {
                left.getItems().add(new BO(bo.getDishesId(), bo.getDishesName()));
            }
            right.getItems().removeAll(selected);
            right.refresh();
            left.refresh();
        });


        VBox leftBox = new VBox();
        VBox rightBox = new VBox();
        leftBox.getChildren().addAll(new Label("已关联的菜品"), left);
        rightBox.getChildren().addAll(new Label("未关联的菜品"), right);
        addLine(newCenterLine(leftBox, operations, rightBox));

        // 保存
        Button save = new Button("保存配置");
        save.setOnAction(evt -> {
            Result<Integer> deletedNum = printerDishDAO.deleteByPrinterId(printer.getPrinterId());

            List<PrinterDishDO> newList = new ArrayList<>();
            List<BO> items = left.getItems();
            for (BO bo : items) {
                PrinterDishDO dd = new PrinterDishDO();
                dd.setPrinterId(printer.getPrinterId());
                dd.setDishId(bo.getDishesId());
                dd.setCreateTime(DateBuilder.now().mills());
                dd.setModTime(DateBuilder.now().mills());
                newList.add(dd);
            }
            try {
                for (PrinterDishDO dd : newList) {
                    printerDishDAO.insert(dd);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                AlertBuilder.ERROR("保存失败:" + ex.getMessage());
            }
        });
        addLine(newCenterLine(save));
    }

    @Data
    public static class BO {
        public BO(Integer dishesId, String dishesName) {
            this.dishesId = dishesId;
            this.dishesName = dishesName;
        }

        Integer dishesId;
        String dishesName;

        public String toString() {
            return dishesName;
        }
    }
}
