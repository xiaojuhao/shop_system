package com.xjh.startup.view;


import static com.xjh.common.utils.CommonUtils.deepClone;
import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumDishesStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.query.DishesQuery;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.stage.Window;
import lombok.Data;

public class DishesManageListView extends SimpleForm implements Initializable {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObjectProperty<DishesQuery> cond = new SimpleObjectProperty<>(new DishesQuery());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildCond();
        buildContent(window.getHeight() - 130);
        buildFoot();
        loadData();
    }

    private void loadData() {
        List<Dishes> list = dishesService.pageQuery(cond.get());
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(dishes -> {
                BO bo = new BO();
                bo.setDishesId(dishes.getDishesId());
                bo.setDishesName(dishes.getDishesName());
                bo.setDishesPrice(new Money(dishes.getDishesPrice()));
                if (dishes.getDishesStock() != null && dishes.getDishesStock() >= 0) {
                    bo.setDishesStock(dishes.getDishesStock().toString());
                } else {
                    bo.setDishesStock("不限");
                }
                StringProperty onOffTitle = new SimpleStringProperty();
                if (dishes.getDishesStatus() != null && dishes.getDishesStatus() == 1) {
                    bo.getDishesStatus().set("上架");
                    onOffTitle.set("下架");
                } else {
                    bo.getDishesStatus().set("下架");
                    onOffTitle.set("上架");
                }
                Operations operations = new Operations();
                OperationButton edit = new OperationButton("编辑", () -> openEditor(dishes));
                OperationButton onoff = new OperationButton("上下架", cv -> {
                    if (onOffTitle.get().equals("上架")) {
                        changeDishesStatus(dishes.getDishesId(), EnumDishesStatus.ON);
                        onOffTitle.set("下架");
                        bo.getDishesStatus().set("上架");
                    } else {
                        onOffTitle.set("上架");
                        changeDishesStatus(dishes.getDishesId(), EnumDishesStatus.OFF);
                        bo.getDishesStatus().set("下架");
                    }
                });
                onoff.setTitleProperty(onOffTitle);
                OperationButton del = new OperationButton("删除", () -> {
                });
                operations.add(edit);
                operations.add(onoff);
                operations.add(del);
                bo.setOperations(operations);
                ImageHelper.resolveImgs(dishes.getDishesImgs()).stream()
                        .findFirst().ifPresent(x -> {
                    ImageSrc img = new ImageSrc(x.getImageSrc());
                    img.setWidth(100);
                    img.setHeight(60);
                    bo.setDishesImgs(img);
                });
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }

    private void buildCond() {
        // name
        HBox nameCondBlock = new HBox();
        Label nameLabel = new Label("名称:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(130);
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, nameInput));

        // status
        HBox statusCondBlock = new HBox();
        Label statusLabel = new Label("状态:");
        ObservableList<String> options = FXCollections.observableArrayList("全部", "上架", "下架");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.getSelectionModel().selectFirst();
        statusCondBlock.getChildren().add(newCenterLine(statusLabel, modelSelect));

        Button queryBtn = new Button("查询");
        queryBtn.setOnAction(evt -> {
            DishesQuery q = deepClone(cond.get(), DishesQuery.class).newVersion();
            q.setDishesName(CommonUtils.trim(nameInput.getText()));
            String selectedStatus = modelSelect.getSelectionModel().getSelectedItem();
            if (CommonUtils.eq(selectedStatus, "上架")) q.setStatus(1);
            if (CommonUtils.eq(selectedStatus, "下架")) q.setStatus(0);
            cond.set(q);
        });

        Button addNew = new Button("新增菜品");
        addNew.setOnAction(evt -> openEditor(new Dishes()));
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
                newCol("ID", "dishesId", 100),
                newCol("名称", "dishesName", 200),
                newCol("图像", "dishesImgs", 200),
                newCol("状态", "dishesStatus", 80),
                newCol("价格", "dishesPrice", 100),
                newCol("库存", "dishesStock", 100),
                newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        cond.addListener((ob, o, n) -> {
            loadData();
        });
        Button prev = new Button("上一页");
        prev.setOnMouseClicked(e -> {
            DishesQuery c = deepClone(cond.get(), DishesQuery.class);
            int pageNo = c.getPageNo();
            if (pageNo <= 1) {
                c.setPageNo(1);
            } else {
                c.setPageNo(pageNo - 1);
            }
            cond.set(c);
        });
        Button next = new Button("下一页");
        next.setOnMouseClicked(e -> {
            DishesQuery c = deepClone(cond.get(), DishesQuery.class);
            c.setPageNo(c.getPageNo() + 1);
            cond.set(c);
        });
        HBox line = newCenterLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑菜品");
        DishesEditView view = new DishesEditView(dishes);
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
        Integer dishesId;
        Integer dishesTypeId;
        String dishesName;
        Money dishesPrice;
        String dishesStock;
        String dishesDescription;
        ImageSrc dishesImgs;
        String dishesUnitName;
        StringProperty dishesStatus = new SimpleStringProperty();
        Long creatTime;
        Integer ifNeedMergePrint;
        Integer ifNeedPrint;
        String validTime;
        Operations operations;
    }
}
