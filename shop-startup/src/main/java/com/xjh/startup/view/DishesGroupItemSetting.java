package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.*;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesGroup;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.dataobject.PrinterDishDO;
import com.xjh.dao.mapper.DishesGroupDAO;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xjh.common.utils.CommonUtils.tryDecodeBase64;
import static com.xjh.common.utils.Const.KEEP_BASE64;

public class DishesGroupItemSetting extends SimpleForm {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesGroupDAO dishesGroupDAO = GuiceContainer.getInstance(DishesGroupDAO.class);

    ObservableList<BO> leftList = FXCollections.observableArrayList();
    ObservableList<BO> rightList = FXCollections.observableArrayList();

    public DishesGroupItemSetting(DishesGroupManageListView.BO input) {
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        //
        Set<Integer> addedDishesId = new HashSet<>();
        List<Dishes> allDishes = dishesService.getAllDishes();
        DishesGroup dishesGroup = OrElse.orGet(dishesGroupDAO.selectByDishesGroupName(input.getDishesGroupName()).getData(), new DishesGroup());
        if (CommonUtils.isNotBlank(dishesGroup.getDishesGroupContent())) {
            JSONArray allPrinterDishIds = JSONArray.parseArray(tryDecodeBase64(dishesGroup.getDishesGroupContent()));
            for (Object allPrinterDishId : allPrinterDishIds) {
                addedDishesId.add(CommonUtils.parseInt(allPrinterDishId, 0));
            }
        }


        CommonUtils.forEach(allDishes, d -> {
            if (addedDishesId.contains(d.getDishesId())) {
                leftList.add(new BO(d.getDishesId(), d.getDishesName()));
            } else {
                rightList.add(new BO(d.getDishesId(), d.getDishesName()));
            }
        });
        this.getChildren().add(new Label("关联集合菜品:" + input.getDishesGroupName()));
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
            TimeRecord timeRecord = TimeRecord.start();
            System.out.println("---------- 菜品集合配置 --------------");
            List<BO> items = left.getItems();
            Set<Integer> selectedDishesId = new HashSet<>();
            for (BO bo : items) {
                selectedDishesId.add(bo.getDishesId());
            }
            Logger.info("打折集合ID：" + selectedDishesId);
            try {
                String content = selectedDishesId.toString();
                dishesGroup.setDishesGroupContent(KEEP_BASE64 ? Base64.encode(content) : content);
                dishesGroupDAO.saveDishesGroup(dishesGroup);
                AlertBuilder.INFO("保存成功");
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
