package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.valueobject.PageCond;
import com.xjh.dao.dataobject.Dishes;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Data;

public class DishesManageListView extends SimpleForm implements Initializable {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObjectProperty<Condition> cond = new SimpleObjectProperty<>(new Condition());
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
        PageCond page = new PageCond();
        page.setPageNo(cond.get().getPageNo());
        page.setPageSize(cond.get().getPageSize());
        List<Dishes> list = dishesService.pageQuery(new Dishes(), page);
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(it -> {
                BO bo = new BO();
                bo.setDishesId(it.getDishesId());
                bo.setDishesName(it.getDishesName());
                bo.setDishesPrice(new Money(it.getDishesPrice()));
                if (it.getDishesStock() != null && it.getDishesStock() >= 0) {
                    bo.setDishesStock(it.getDishesStock().toString());
                } else {
                    bo.setDishesStock("不限");
                }
                if (it.getDishesStatus() != null && it.getDishesStatus() == 1) {
                    bo.setDishesStatus("上架");
                } else {
                    bo.setDishesStatus("下架");
                }
                Operations operations = new Operations();
                OperationButton edit = new OperationButton("编辑", () -> {
                    Window window = this.getScene().getWindow();
                    ModelWindow mw = new ModelWindow(window, "编辑菜品");
                    DishesEditView view = new DishesEditView(it);
                    view.setPrefWidth(window.getWidth() * 0.75);
                    mw.setScene(new Scene(view));
                    mw.showAndWait();
                });
                OperationButton onoff = new OperationButton("上下架", () -> {
                });
                OperationButton del = new OperationButton("删除", () -> {
                });
                operations.add(edit);
                operations.add(onoff);
                operations.add(del);
                bo.setOperations(operations);
                ImageHelper.resolveImgs(it.getDishesImgs()).stream()
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
        HBox nameLine = new HBox();
        Label nameLabel = new Label("名称:");
        TextField nameInput = new TextField();
        nameLine.getChildren().add(nameLabel);
        nameLine.getChildren().add(nameInput);


        // status
        HBox statusLine = new HBox();
        Label statusLabel = new Label("状态:");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton all = new RadioButton("全部");
        all.setToggleGroup(toggleGroup);
        all.setUserData(-1);

        RadioButton online = new RadioButton("上架");
        online.setToggleGroup(toggleGroup);
        online.setUserData(1);
        online.setSelected(true);

        RadioButton offline = new RadioButton("下架");
        offline.setToggleGroup(toggleGroup);
        offline.setUserData(0);

        statusLine.getChildren().add(statusLabel);
        statusLine.getChildren().add(newLine(online, offline));

        HBox line = newLine(nameLine, statusLine);
        line.setSpacing(20);
        line.setPadding(new Insets(10, 0, 20, 0));
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
            Condition c = CommonUtils.deepClone(cond.get(), Condition.class);
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
            Condition c = CommonUtils.deepClone(cond.get(), Condition.class);
            c.setPageNo(c.getPageNo() + 1);
            cond.set(c);
        });
        HBox line = newLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    @Data
    public static class Condition {
        int pageNo = 1;
        int pageSize = 20;
        String name;
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
        String dishesStatus;
        Long creatTime;
        Integer ifNeedMergePrint;
        Integer ifNeedPrint;
        String validTime;
        Operations operations;
    }
}
