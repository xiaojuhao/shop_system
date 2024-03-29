package com.xjh.startup.view;


import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xjh.service.store.TableViewUtils.newCol;

public class DishesGroupManageListView extends SimpleForm implements Initializable {

    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        Window window = this.getScene().getWindow();
        buildContent(window.getHeight() - 130);
        loadData();
    }

    private void loadData() {
        List<BO> list = new ArrayList<>();
        list.add(new BO("打折集合"));
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(dd -> {
                BO bo = new BO("");
                bo.setDishesGroupName(dd.getDishesGroupName());
                bo.getOperations().add(new OperationButton("编辑菜品", () -> openEditor(bo)));
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }


    private void buildContent(double height) {

        tableView.getColumns().addAll(
                newCol("集合名称", "dishesGroupName", 350),
                newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void openEditor(BO bo) {
        Window sceneWindow = this.getScene().getWindow();
        Stage stage = new Stage();
        stage.initOwner(sceneWindow);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("集合菜品设置");
        stage.setScene(new Scene(new DishesGroupItemSetting(bo)));
        stage.showAndWait();
        loadData();
    }

    @Data
    public static class BO {
        BO(String dishesGroupName){
            this.dishesGroupName = dishesGroupName;
        }
        String dishesGroupName;
        Operations operations = new Operations();
    }
}
