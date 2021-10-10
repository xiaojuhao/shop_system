package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleGridForm;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import lombok.Data;

public class PackageDishesTypeMangeView extends SimpleGridForm {
    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesPackageTypeDAO dishesPackageTypeDAO = GuiceContainer.getInstance(DishesPackageTypeDAO.class);
    DishesPackageDishesDAO dishesPackageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);

    DishesPackage dishesPackage;
    List<Runnable> collectData = new ArrayList<>();
    ObservableList<BO> packageTypes = FXCollections.observableArrayList();

    // type = 1
    RichText ALL_CHOOSE = RichText.create("全部必选").with(Color.color(0, (double) 195 / 255, 0));
    // type = 2
    RichText MAX_CHOOSE = RichText.create("最多可选").with(Color.color((double) 195 / 255, 0, 0));
    // type = 0
    RichText EXACT_CHOOSE = RichText.create("精确选择").with(Color.color(0, 0, (double) 195 / 255));

    TableView<BO> tableView = newTableView(600, 200);

    public PackageDishesTypeMangeView(DishesPackage param) {
        if (param.getDishesPackageId() == null) {
            AlertBuilder.ERROR("套餐ID不能为空");
            return;
        }
        dishesPackage = param;
        loadData();

        tableView.getColumns().addAll(
                newCol("序号", "dishesPackageTypeId", 60),
                newCol("类型", "dishesPackageTypeName", 200),
                newCol("选择类型", "ifRequired", 120),
                newCol("可选数量", "chooseNums", 60),
                newCol("包含菜品数量", "dishesNum", 60)
        );
        tableView.setItems(packageTypes);
        addLine("", tableView);

        tableView.refresh();

        /* ************************************************************** *\
         *    保存数据
        \* ************************************************************** */
        Button add = new Button("添加类型");
        add.setOnAction(evt -> addNewItem());
        Button edit = new Button("编辑类型");
        edit.setOnAction(evt -> editItem());
        Button del = new Button("删除类型");
        del.setOnAction(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("删除类型");
            alert.setContentText("确定要删除这行记录吗?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(null) != ButtonType.OK) {
                return;
            }
            deleteItem();
        });
        Button viewDishes = new Button("查看菜品");
        viewDishes.setOnAction(evt -> openDishesView());

        HBox hbox = hbox(add, edit, del, viewDishes);
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(0, 0, 0, 60));
        addLine((Node) null, hbox);
    }

    private <T> TableView<T> newTableView(double width, double height) {
        TableView<T> tv = new TableView<>();
        tv.setPrefWidth(width);
        tv.setPrefHeight(height);
        return tv;
    }

    private void loadData() {
        packageTypes.clear();
        // 类型
        List<DishesPackageType> list =
                dishesPackageTypeDAO.getByDishesPackageId(dishesPackage.getDishesPackageId());
        list.sort(Comparator.comparing(DishesPackageType::getDishesPackageTypeId));
        // 菜品
        DishesPackageDishes cond = new DishesPackageDishes();
        cond.setDishesPackageId(dishesPackage.getDishesPackageId());
        List<DishesPackageDishes> packageDishesList = dishesPackageDishesDAO.selectList(cond);
        Map<Integer, Integer> dishesNumMap = new HashMap<>();
        for (DishesPackageDishes dd : packageDishesList) {
            if (dishesNumMap.containsKey(dd.getDishesPackageTypeId())) {
                dishesNumMap.put(dd.getDishesPackageTypeId(),
                        dishesNumMap.get(dd.getDishesPackageTypeId()) + 1);
            } else {
                dishesNumMap.put(dd.getDishesPackageTypeId(), 1);
            }
        }
        packageTypes.addAll(list.stream().map(it -> {
            BO bo = new BO();
            bo.setAttachment(it);
            bo.setDishesPackageId(it.getDishesPackageId());
            bo.setDishesPackageTypeId(it.getDishesPackageTypeId());
            bo.setDishesPackageTypeName(it.getDishesPackageTypeName());
            bo.setChooseNums(it.getChooseNums());
            if (CommonUtils.eq(1, it.getIfRequired())) {
                bo.getIfRequired().set(ALL_CHOOSE);
            } else if (CommonUtils.eq(2, it.getIfRequired())) {
                bo.getIfRequired().set(MAX_CHOOSE);
            } else {
                bo.getIfRequired().set(EXACT_CHOOSE);
            }
            bo.setDishesNum(CommonUtils.orElse(dishesNumMap.get(it.getDishesPackageTypeId()), 0));
            return bo;
        }).collect(Collectors.toList()));

        tableView.refresh();
    }

    private void addNewItem() {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "添加套餐类型");
        DishesPackageType newType = new DishesPackageType();
        PackageDishesTypeEditView view = new PackageDishesTypeEditView(dishesPackage, newType);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    private void editItem() {
        Result<BO> selected = getSelectedItem();
        if (!selected.isSuccess()) {
            AlertBuilder.ERROR(selected.getMsg());
            return;
        }
        DishesPackageType selectedItem = selected.getData().attachment;
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑套餐类型");
        PackageDishesTypeEditView view = new PackageDishesTypeEditView(dishesPackage, selectedItem);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    private void deleteItem() {
        Result<BO> selected = getSelectedItem();
        if (!selected.isSuccess()) {
            AlertBuilder.ERROR(selected.getMsg());
            return;
        }
        BO bo = selected.getData();
        // 删除菜品
        Result<Integer> rs1 = dishesPackageDishesDAO.deleteByDishesPackageTypeId(bo.getDishesPackageTypeId());
        if (!rs1.isSuccess()) {
            AlertBuilder.ERROR(rs1.getMsg());
            return;
        }
        // 删除类型
        Result<Integer> rs2 = dishesPackageTypeDAO.deleteById(bo.getDishesPackageTypeId());
        if (!rs2.isSuccess()) {
            AlertBuilder.ERROR(rs2.getMsg());
            return;
        }
        AlertBuilder.INFO("删除成功");
        loadData();
    }

    private void openDishesView() {
        Result<BO> selected = getSelectedItem();
        if (!selected.isSuccess()) {
            AlertBuilder.ERROR(selected.getMsg());
            return;
        }
        DishesPackageType type = selected.getData().getAttachment();

        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "套餐菜品管理");
        PackageDishesDetailManageView view = new PackageDishesDetailManageView(dishesPackage, type);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        view.initialize();
        mw.showAndWait();
        loadData();
    }

    private Result<BO> getSelectedItem() {
        BO bo = tableView.getSelectionModel().getSelectedItem();
        if (bo == null) {
            return Result.fail("请选择操作记录!");
        }
        return Result.success(bo);
    }

    @Data
    public static class BO {
        Integer dishesPackageTypeId;

        Integer dishesPackageId;

        String dishesPackageTypeName;

        ObjectProperty<RichText> ifRequired = new SimpleObjectProperty<>();

        Integer chooseNums = 0;

        Integer dishesNum = 0;

        DishesPackageType attachment;
    }
}
