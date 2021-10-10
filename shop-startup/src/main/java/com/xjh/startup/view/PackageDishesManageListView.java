package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.xjh.common.enumeration.EnumDishesStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.query.DishesPackageQuery;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
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

public class PackageDishesManageListView extends SimpleForm implements Initializable {
    static RichText IN_USE = RichText.create("已启用").with(Color.BLUE);
    static RichText NOT_USE = RichText.create("已停用").with(Color.RED);

    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    DishesPackageDishesDAO dishesPackageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObjectProperty<DishesPackageQuery> cond = new SimpleObjectProperty<>(new DishesPackageQuery());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
        cond.addListener((x, a, b) -> loadData());
    }

    private void loadData() {
        List<DishesPackage> dishesPackageList = dishesPackageService.pageQuery(cond.get());
        List<DishesPackageDishes> packageDishesList = dishesPackageDishesDAO.selectList(new DishesPackageDishes());
        Map<Integer, List<DishesPackageDishes>> packageDishesMap = CommonUtils.groupBy(packageDishesList,
                DishesPackageDishes::getDishesPackageId);
        Platform.runLater(() -> {
            items.clear();
            items.addAll(dishesPackageList.stream().map(dk -> {
                BO bo = new BO();
                bo.setDishesPackageId(dk.getDishesPackageId());
                bo.setDishesPackageName(dk.getDishesPackageName());
                bo.setDishesPackagePrice(new Money(dk.getDishesPackagePrice()));
                bo.setPackageDishesSize(CollectionUtils.size(packageDishesMap.get(dk.getDishesPackageId())));
                if (CommonUtils.eq(1, dk.getDishesPackageStatus())) {
                    bo.getStatus().set(IN_USE);
                } else {
                    bo.getStatus().set(NOT_USE);
                }
                Operations operations = new Operations();
                OperationButton edit = new OperationButton("编辑", () -> openEditor(dk));
                OperationButton typeEdit = new OperationButton("菜品维护", () -> openDishesSelectPage(dk));
                operations.add(edit, typeEdit);
                bo.setOperations(operations);
                ImageHelper.resolveImgs(dk.getDishesPackageImg()).stream()
                        .findFirst().ifPresent(x -> {
                    ImageSrc img = new ImageSrc(x.getImageSrc());
                    img.setWidth(100);
                    img.setHeight(60);
                    bo.setDishesPackageImg(img);
                });
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }

    private void buildCond() {
        // name
        HBox nameCondBlock = new HBox();
        Label nameLabel = new Label("套餐名称:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(130);
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, nameInput));

        // status
        HBox statusCondBlock = new HBox();
        Label statusLabel = new Label("套餐状态:");
        ObservableList<String> options = FXCollections.observableArrayList("全部", "已启用", "已停用");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.getSelectionModel().selectFirst();
        statusCondBlock.getChildren().add(newCenterLine(statusLabel, modelSelect));

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            DishesPackageQuery q = cond.get().newVersion();
            q.setName(CommonUtils.trim(nameInput.getText()));
            String selectedStatus = modelSelect.getSelectionModel().getSelectedItem();
            if (CommonUtils.eq(selectedStatus, "已启用")) q.setStatus(1 + "");
            else if (CommonUtils.eq(selectedStatus, "已停用")) q.setStatus(0 + "");
            else q.setStatus(null);
            cond.set(q);
        });

        Button addNew = new Button("新增套餐");
        addNew.setOnAction(evt -> openEditor(new DishesPackage()));
        HBox line = newCenterLine(nameCondBlock, statusCondBlock,
                queryBtn,
                new Separator(Orientation.VERTICAL),
                addNew);
        line.setSpacing(20);
        line.setPadding(new Insets(5, 0, 5, 0));
        addLine(line);
    }

    private void buildContent(double height) {

        tableView.getColumns().addAll(
                newCol("ID", "dishesPackageId", 100),
                newCol("套餐名称", "dishesPackageName", 200),
                newCol("图像", "dishesPackageImg", 200),
                newCol("菜品数量", "packageDishesSize", 30),
                newCol("状态", "status", 80),
                newCol("套餐价格", "dishesPackagePrice", 100),
                newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        Button prev = new Button("上一页");
        Button next = new Button("下一页");
        HBox line = newCenterLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(DishesPackage dishesPkg) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑套餐基础信息");
        PackageDishesEditView view = new PackageDishesEditView(dishesPkg);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    private void openDishesSelectPage(DishesPackage dishesPkg) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑套餐基础信息");
        PackageDishesTypeMangeView view = new PackageDishesTypeMangeView(dishesPkg);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    private void changeDishesStatus(Integer dishesId, EnumDishesStatus status) {
        Dishes d = new Dishes();
        d.setDishesId(dishesId);
        d.setDishesStatus(status.status);
        Result<Integer> rs = dishesService.save(d);
        if (rs.isSuccess()) {
            AlertBuilder.INFO("更新状态成功");
        } else {
            AlertBuilder.ERROR("~~系统异常了哦~~~~" + rs.getMsg());
        }
    }

    @Data
    public static class BO {
        Integer dishesPackageId;
        Integer dishesTypeId;
        String dishesPackageName;
        Money dishesPackagePrice;
        String dishesDescription;
        ImageSrc dishesPackageImg;
        String dishesUnitName;
        ObjectProperty<RichText> status = new SimpleObjectProperty<>();
        Long creatTime;
        Integer ifNeedMergePrint;
        Integer ifNeedPrint;
        String validTime;
        Integer packageDishesSize;
        Operations operations;
    }
}
