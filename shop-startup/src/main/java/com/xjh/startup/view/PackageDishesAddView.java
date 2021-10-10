package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumDishesStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.query.DishesQuery;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.IntStringPair;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import lombok.Data;

public class PackageDishesAddView extends SimpleForm implements Initializable {
    static RichText STATUS_ON = RichText.create("上架").with(Color.BLUE);
    static RichText STATUS_OFF = RichText.create("下架").with(Color.RED);

    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    DishesPackageDishesDAO dishesPackageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObjectProperty<DishesQuery> cond = new SimpleObjectProperty<>(new DishesQuery());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();
    DishesPackageType dishesPackageType;

    public PackageDishesAddView(DishesPackageType dishesPackageType) {
        this.dishesPackageType = dishesPackageType;
    }

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        cond.get().setStatus(EnumDishesStatus.ON.status);
        cond.addListener((x, a, b) -> loadData());
        loadData();
    }

    private void loadData() {
        items.clear();
        // 套餐选项中的菜品记录
        List<DishesPackageDishes> packageDishesList = dishesPackageDishesDAO.getByDishesPackageTypeId(dishesPackageType.getDishesPackageTypeId());
        Set<Integer> added = CommonUtils.collectSet(packageDishesList, DishesPackageDishes::getDishesId);
        List<Dishes> dishesList = dishesService.pageQuery(cond.get());
        items.addAll(dishesList.stream().map(it -> {
            if (added.contains(it.getDishesId())) {
                return null;
            }
            BO bo = new BO();
            bo.setDishesId(it.getDishesId());
            bo.setDishesName(it.getDishesName());
            bo.setDishesPrice(new Money(it.getDishesPrice()));
            ImageHelper.resolveImgs(it.getDishesImgs()).forEach(x -> {
                bo.setDishesImg(new ImageSrc(x.getImageSrc(), 100, 60));
            });
            EnumDishesStatus s = EnumDishesStatus.of(it.getDishesStatus());
            bo.getStatus().set(s == EnumDishesStatus.ON ? STATUS_ON : STATUS_OFF);
            return bo;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        tableView.refresh();
    }

    private void buildCond() {
        // name
        HBox nameCondBlock = new HBox();
        Label nameLabel = new Label("菜品名称:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(130);
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, nameInput));

        // status
        HBox statusCondBlock = new HBox();
        Label statusLabel = new Label("状态:");
        ObservableList<IntStringPair> options = FXCollections.observableArrayList(
                new IntStringPair(null, "全部"),
                new IntStringPair(EnumDishesStatus.ON.status, EnumDishesStatus.ON.remark),
                new IntStringPair(EnumDishesStatus.OFF.status, EnumDishesStatus.OFF.remark)
        );
        ComboBox<IntStringPair> modelSelect = new ComboBox<>(options);
        IntStringPair.select(modelSelect, EnumDishesStatus.ON.status, null);
        statusCondBlock.getChildren().add(newCenterLine(statusLabel, modelSelect));

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            DishesQuery q = cond.get().newVersion();
            q.setDishesName(CommonUtils.trim(nameInput.getText()));
            if (modelSelect.getSelectionModel().getSelectedItem() != null) {
                q.setStatus(modelSelect.getSelectionModel().getSelectedItem().getKey());
            } else {
                q.setStatus(null);
            }
            cond.set(q);
        });

        HBox line = newCenterLine(nameCondBlock, statusCondBlock,
                queryBtn,
                new Separator(Orientation.VERTICAL));
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {
        tableView.getColumns().addAll(
                newCol("ID", "dishesId", 80),
                newCol("菜品名称", "dishesName", 200),
                newCol("菜品图像", "dishesImg", 200),
                newCol("菜品价格", "dishesPrice", 80),
                newCol("菜品状态", "status", 80)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        Label remark = new Label("备注:双击加入套餐");
        remark.setTextFill(Color.RED);
        addLine(newCenterLine(remark));
    }

    @Data
    public static class BO {
        Integer dishesId;
        String dishesName;
        Money dishesPrice;
        ImageSrc dishesImg;
        ObjectProperty<RichText> status = new SimpleObjectProperty<>();
        Long creatTime;
        Operations operations;
    }
}
