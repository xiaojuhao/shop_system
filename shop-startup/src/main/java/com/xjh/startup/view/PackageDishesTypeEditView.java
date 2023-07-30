package com.xjh.startup.view;

import com.alibaba.fastjson.JSON;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleGridForm;
import com.xjh.startup.view.model.IntStringPair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class PackageDishesTypeEditView extends SimpleGridForm {
    DishesPackageTypeDAO dishesPackageTypeDAO = GuiceContainer.getInstance(DishesPackageTypeDAO.class);

    DishesPackageType dishesPackageType;
    List<Runnable> collectData = new ArrayList<>();

    public PackageDishesTypeEditView(DishesPackage dishesPackage, DishesPackageType param) {
        dishesPackageType = dishesPackageTypeDAO.getById(param.getDishesPackageTypeId());
        if (dishesPackageType == null) {
            dishesPackageType = new DishesPackageType();
            dishesPackageType.setDishesPackageId(dishesPackage.getDishesPackageId());
        }
        double labelWidth = 120;
        Label nameLabel = createLabel("类型名称:", labelWidth);
        TextField nameInput = createTextField("类型名称", 300);
        nameInput.setText(dishesPackageType.getDishesPackageTypeName());
        addLine(nameLabel, hbox(nameInput));
        collectData.add(() -> dishesPackageType.setDishesPackageTypeName(nameInput.getText()));

        Label dishesTypeLabel = createLabel("选择类型:", labelWidth);
        ObservableList<IntStringPair> typeOptions = FXCollections.observableArrayList(
                new IntStringPair(1, "全部必选"),
                new IntStringPair(0, "精确选择"),
                new IntStringPair(2, "最多可选")
        );
        ComboBox<IntStringPair> dishesTypeInput = new ComboBox<>(typeOptions);
        IntStringPair.select(dishesTypeInput, dishesPackageType.getIfRequired(), 1);
        addLine(dishesTypeLabel, hbox(dishesTypeInput));
        collectData.add(() -> {
            dishesPackageType.setIfRequired(dishesTypeInput.getSelectionModel().getSelectedItem().getKey());
        });

        Label chooseNumLabel = new Label("可选数量:");
        chooseNumLabel.setPadding(new Insets(0, 5, 0, 100));
        TextField chooseNumInput = new TextField();
        chooseNumInput.setText(dishesPackageType.getChooseNums() + "");
        chooseNumInput.setPrefWidth(60);
        addLine(chooseNumLabel, hbox(chooseNumInput));
        collectData.add(() -> {
            dishesPackageType.setChooseNums(CommonUtils.parseInt(chooseNumInput.getText(), 0));
        });

        /* ************************************************************** *\
         *    保存数据
        \* ************************************************************** */
        Button save = new Button("保 存");
        save.setOnAction(evt -> {
            CommonUtils.safeRun(collectData);
            System.out.println(JSON.toJSONString(dishesPackageType, true));
            Result<Integer> rs;
            if (dishesPackageType.getDishesPackageTypeId() == null) {
                rs = dishesPackageTypeDAO.insert(dishesPackageType);
            } else {
                rs = dishesPackageTypeDAO.updateById(dishesPackageType);
            }
            if (rs.isSuccess()) {
                AlertBuilder.INFO("保存成功");
                this.getScene().getWindow().hide();
            } else {
                AlertBuilder.ERROR("保存失败," + rs.getMsg());
            }
        });
        addLine((Node) null, save);
    }
}
