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
    RichText ALL_CHOOSE = RichText.create("????????????").with(Color.color(0, (double) 195 / 255, 0));
    // type = 2
    RichText MAX_CHOOSE = RichText.create("????????????").with(Color.color((double) 195 / 255, 0, 0));
    // type = 0
    RichText EXACT_CHOOSE = RichText.create("????????????").with(Color.color(0, 0, (double) 195 / 255));

    TableView<BO> tableView = newTableView(600, 200);

    public PackageDishesTypeMangeView(DishesPackage param) {
        if (param.getDishesPackageId() == null) {
            AlertBuilder.ERROR("??????ID????????????");
            return;
        }
        dishesPackage = param;
        loadData();

        tableView.getColumns().addAll(
                newCol("??????", "dishesPackageTypeId", 60),
                newCol("??????", "dishesPackageTypeName", 200),
                newCol("????????????", "ifRequired", 120),
                newCol("????????????", "chooseNums", 60),
                newCol("??????????????????", "dishesNum", 60)
        );
        tableView.setItems(packageTypes);
        addLine("", tableView);

        tableView.refresh();

        /* ************************************************************** *\
         *    ????????????
        \* ************************************************************** */
        Button add = new Button("????????????");
        add.setOnAction(evt -> addNewItem());
        Button edit = new Button("????????????");
        edit.setOnAction(evt -> editItem());
        Button del = new Button("????????????");
        del.setOnAction(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("????????????");
            alert.setContentText("???????????????????????????????");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(null) != ButtonType.OK) {
                return;
            }
            deleteItem();
        });
        Button viewDishes = new Button("????????????");
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
        // ??????
        List<DishesPackageType> list =
                dishesPackageTypeDAO.getByDishesPackageId(dishesPackage.getDishesPackageId());
        list.sort(Comparator.comparing(DishesPackageType::getDishesPackageTypeId));
        // ??????
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
        ModelWindow mw = new ModelWindow(window, "??????????????????");
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
        ModelWindow mw = new ModelWindow(window, "??????????????????");
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
        // ????????????
        Result<Integer> rs1 = dishesPackageDishesDAO.deleteByDishesPackageTypeId(bo.getDishesPackageTypeId());
        if (!rs1.isSuccess()) {
            AlertBuilder.ERROR(rs1.getMsg());
            return;
        }
        // ????????????
        Result<Integer> rs2 = dishesPackageTypeDAO.deleteById(bo.getDishesPackageTypeId());
        if (!rs2.isSuccess()) {
            AlertBuilder.ERROR(rs2.getMsg());
            return;
        }
        AlertBuilder.INFO("????????????");
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
        ModelWindow mw = new ModelWindow(window, "??????????????????");
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
            return Result.fail("?????????????????????!");
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
