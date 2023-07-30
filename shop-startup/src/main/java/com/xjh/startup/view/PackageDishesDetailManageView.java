package com.xjh.startup.view;


import com.xjh.common.enumeration.EnumDishesStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class PackageDishesDetailManageView extends SimpleForm implements Initializable {
    static RichText STATUS_ON = RichText.create("上架").with(Color.BLUE);
    static RichText STATUS_OFF = RichText.create("下架").with(Color.RED);

    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    DishesPackageDishesDAO dishesPackageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();
    DishesPackageType dishesPackageType;

    public PackageDishesDetailManageView(DishesPackage dishesPackage, DishesPackageType dishesPackageType) {
        this.dishesPackageType = dishesPackageType;
    }

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        items.clear();
        // 套餐选项中的菜品记录
        List<DishesPackageDishes> dishesList = dishesPackageDishesDAO.getByDishesPackageTypeId(
                dishesPackageType.getDishesPackageId(), dishesPackageType.getDishesPackageTypeId());
        List<Integer> dishesIds = CommonUtils.collect(dishesList, DishesPackageDishes::getDishesId);
        Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIds);
        items.addAll(dishesList.stream().map(it -> {
            Dishes dishes = dishesMap.get(it.getDishesId());
            if (dishes == null) {
                return null;
            }
            BO bo = new BO();
            bo.setDishesPackageDishesId(it.getDishesPackageDishesId());
            bo.setDishesName(dishes.getDishesName());
            bo.setDishesPrice(new Money(dishes.getDishesPrice()));
            ImageHelper.resolveImgs(dishes.getDishesImgs()).forEach(x -> {
                bo.setDishesImg(new ImageSrc(x.getImageSrc(), 100, 60));
            });
            EnumDishesStatus s = EnumDishesStatus.of(dishes.getDishesStatus());
            bo.getStatus().set(s == EnumDishesStatus.ON ? STATUS_ON : STATUS_OFF);
            return bo;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        tableView.refresh();
    }

    private void buildCond() {

    }

    private void buildContent(double height) {
        tableView.getColumns().addAll(
                newCol("ID", "dishesPackageDishesId", 100),
                newCol("菜品名称", "dishesName", 200),
                newCol("菜品图像", "dishesImg", 200),
                newCol("菜品价格", "dishesPrice", 80),
                newCol("菜品状态", "status", 80),
                newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        Button addNew = new Button("添加菜品");
        addNew.setOnAction(evt -> addDishes());
        Button remove = new Button("移除所选菜品");
        remove.setOnAction(evt -> removeSelectedDishes());
        addLine(newCenterLine(addNew, remove));
    }

    private void addDishes() {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "添加套餐菜品");
        PackageDishesAddView view = new PackageDishesAddView(dishesPackageType);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        view.initialize();
        mw.showAndWait();
        loadData();
    }

    private void removeSelectedDishes() {
        List<BO> selectedList = tableView.getSelectionModel().getSelectedItems();
        if (CommonUtils.isEmpty(selectedList)) {
            AlertBuilder.ERROR("请选择删除记录");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除菜品");
        alert.setContentText("确定要删除选中的菜品吗?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) != ButtonType.OK) {
            return;
        }
        List<Integer> ids = CommonUtils.collect(selectedList, BO::getDishesPackageDishesId);
        Result<Integer> rs = dishesPackageDishesDAO.deleteByIds(ids);
        if (rs.isSuccess()) {
            loadData();
            AlertBuilder.INFO("删除成功");
        } else {
            AlertBuilder.ERROR(rs.getMsg());
        }
    }

    @Data
    public static class BO {
        Integer dishesPackageDishesId;
        Integer dishesId;
        String dishesName;
        Money dishesPrice;
        ImageSrc dishesImg;
        ObjectProperty<RichText> status = new SimpleObjectProperty<>();
        Long creatTime;
        Operations operations;
    }
}
