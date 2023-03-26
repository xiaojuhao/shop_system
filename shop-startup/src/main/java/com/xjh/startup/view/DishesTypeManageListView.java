package com.xjh.startup.view;


import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.IntStringPair;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.util.List;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class DishesTypeManageListView extends SimpleForm implements Initializable {
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    TableView<DishesType> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        List<DishesType> types = dishesTypeService.selectList(new DishesType());
        tableView.getItems().clear();
        tableView.getItems().addAll(types);
        tableView.refresh();
    }

    private void buildContent(double height) {
        tableView.getColumns().addAll(
                newCol("typeId", DishesType::getTypeId, 30),
                newCol("分类名称", DishesType::getTypeName, 200),
                newCol("是否可以反结账", this::transferIfRefund, 80),
                newCol("状态", this::transferStatus, 80),
                newCol("创建时间", it -> DateBuilder.base(it.getCreatTime()).timeStr(), 100),
                newCol("排序顺序", DishesType::getSortby, 100),
                newCol("H5是否显示", this::transferH5Status, 100),
                newCol("操作", this::operations, 200)
        );
        tableView.setPrefHeight(height);
        addLine(tableView);
        //
        loadData();
    }

    private void buildFoot() {
        Button button = new Button("增加分类");
        button.setOnAction(evt -> openEditor(new DishesType()));
        addLine(newCenterLine(button));
    }

    private void openEditor(DishesType type) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑菜品分类");
        mw.setWidth(400);
        mw.setHeight(250);
        SimpleForm form = new SimpleForm();
        form.setPadding(new Insets(20, 5, 0, 5));
        form.setSpacing(6);
        // 分类名称
        TextField typeName = new TextField(type.getTypeName());
        form.addLine(newLine(createDefLabel("分类名称:"), typeName));
        // 反结账
        ComboBox<IntStringPair> ifRefund = new ComboBox<>(FXCollections.observableArrayList(
                new IntStringPair(0, "否"), new IntStringPair(1, "是")));
        IntStringPair.select(ifRefund, type.getIfRefund(), 1);
        form.addLine(newLine(createDefLabel("是否可以反结账:"), ifRefund));
        // 顺序
        TextField sortBy = new TextField(OrElse.orGet(type.getSortby(), 0).toString());
        form.addLine(newLine(createDefLabel("顺序:"), sortBy));
        // H5是否可显示
        ComboBox<IntStringPair> h5hidden = new ComboBox<>(FXCollections.observableArrayList(
                new IntStringPair(1, "否"), new IntStringPair(0, "是")));
        IntStringPair.select(h5hidden, type.getHiddenH5(), 0);
        form.addLine(newLine(createDefLabel("h5是否可以显示:"), h5hidden));
        // 提交
        Button submit = new Button("提交");
        submit.setOnAction(evt -> {
            DishesType up = new DishesType();
            up.setTypeId(type.getTypeId());
            up.setTypeName(typeName.getText());
            if (ifRefund.getSelectionModel().getSelectedItem() != null) {
                up.setIfRefund(ifRefund.getSelectionModel().getSelectedItem().getKey());
            } else {
                up.setIfRefund(1);
            }
            up.setSortby(CommonUtils.parseInt(sortBy.getText(), 0));
            if (h5hidden.getSelectionModel().getSelectedItem() != null) {
                up.setHiddenH5(h5hidden.getSelectionModel().getSelectedItem().getKey());
            } else {
                up.setHiddenH5(0);
            }
            if (up.getTypeId() == null) {
                up.setHiddenFlat(0);
                up.setTypeStatus(1);
                up.setCreatTime(DateBuilder.now().mills());
                dishesTypeService.insert(up);
            } else {
                dishesTypeService.updateByPK(up);
            }
            mw.close();
        });
        form.addLine(newCenterLine(submit));
        mw.setScene(new Scene(form));
        mw.showAndWait();
        loadData();
    }

    private Label createDefLabel(String title) {
        Label l = new Label(title);
        l.setPrefWidth(120);
        return l;
    }

    private String transferIfRefund(DishesType type) {
        if (type.getIfRefund() != null && type.getIfRefund() == 1) {
            return "是";
        } else {
            return "否";
        }
    }

    private RichText transferStatus(DishesType type) {
        if (type.getTypeStatus() == null || type.getTypeStatus() == 1) {
            return RichText.create("使用中").with(Color.BLUE);
        } else {
            return RichText.create("已停用").with(Color.RED);
        }
    }

    private String transferH5Status(DishesType type) {
        if (type.getHiddenH5() != null && type.getHiddenH5() == 0) {
            return "是";
        } else {
            return "否";
        }
    }

    private Operations operations(DishesType type) {
        Operations operations = new Operations();
        // 编辑
        OperationButton edit = new OperationButton("编辑", () -> openEditor(type));
        operations.add(edit);
        // 停用/启用
        RichText stopOrUse = isInUse(type) ?
                RichText.create("停用").with(Color.RED) :
                RichText.create("启用").with(Color.BLUE);
        OperationButton stopBtn = new OperationButton(stopOrUse, () -> {
            DishesType u = new DishesType();
            u.setTypeId(type.getTypeId());
            u.setTypeStatus(isInUse(type) ? 0 : 1);
            dishesTypeService.updateByPK(u);
            loadData();
        });
        operations.add(stopBtn);

        // 删除
        OperationButton del = new OperationButton("删除", () -> {
            Dishes cond = new Dishes();
            cond.setDishesTypeId(type.getTypeId());
            List<Dishes> dishesList = dishesService.selectList(cond);
            if (dishesList.size() > 0) {
                AlertBuilder.ERROR("该类型下有" + dishesList.size() + "个菜品,无法删除");
                return;
            }
            dishesTypeService.deleteByPK(type.getTypeId());
            loadData();
        });
        operations.add(del);
        return operations;
    }

    private boolean isInUse(DishesType type) {
        return type.getTypeStatus() != null && type.getTypeStatus() == 1;
    }

}
