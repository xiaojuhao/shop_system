package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumDishesStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.mapper.DishesPriceDAO;
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
    DishesPriceDAO dishesPriceDAO = GuiceContainer.getInstance(DishesPriceDAO.class);

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
                bo.setDishesPrice(new Money(dishes.getDishesPrice()).toString());
                List<DishesPrice> dishesPrices = dishesPriceDAO.queryByDishesId(dishes.getDishesId());
                if (dishesPrices.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (DishesPrice price : dishesPrices) {
                        sb.append(price.getDishesPriceName()).append(":")
                                .append(new Money(price.getDishesPrice()).toString())
                                .append("\n");
                    }
                    bo.setDishesPrice(sb.toString());
                }
                if (dishes.getDishesStock() != null && dishes.getDishesStock() >= 0) {
                    bo.setDishesStock(dishes.getDishesStock().toString());
                } else {
                    bo.setDishesStock("不限");
                }
                StringProperty onOffTitle = new SimpleStringProperty();
                Predicate<Dishes> isOnline = d -> d.getDishesStatus() != null && d.getDishesStatus() == 1;
                bo.getDishesStatus().set(isOnline.test(dishes) ? "上架" : "下架");
                onOffTitle.set(isOnline.test(dishes) ? "下架" : "上架");

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
                OperationButton editPrice = new OperationButton("编辑多价格", () -> openPriceEditor(dishes));
                operations.add(edit);
                operations.add(onoff);
                operations.add(del);
                operations.add(editPrice);
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
            DishesQuery q = cond.get().newVersion();
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
                newCol("操作", "operations", 250)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        cond.addListener((ob, o, n) -> loadData());
        Button prev = new Button("上一页");
        prev.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            c.increasePageNo();
            cond.set(c);
        });
        Button next = new Button("下一页");
        next.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            c.decreasePageNo();
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

    private void openPriceEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "编辑多价格");
        mw.setWidth(450);
        mw.setHeight(350);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        // 菜品信息
        Label dishesNameLabel = new Label("菜品名称:");
        Label dishesName = new Label(dishes.getDishesName());
        form.addLine(newLine(dishesNameLabel, dishesName));
        // 价格列表
        TableView<DishesPrice> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("价格名称", DishesPrice::getDishesPriceName, 200),
                newCol("价格", DishesPrice::getDishesPrice, 200)
        );

        Runnable reloadPriceData = () -> {
            tv.getItems().clear();
            tv.getItems().addAll(dishesPriceDAO.queryByDishesId(dishes.getDishesId()));
            tv.refresh();
        };
        form.addLine(tv);
        reloadPriceData.run();
        // 操作按钮
        Button add = new Button("添加");
        add.setOnAction(evt -> {
            editPrice(mw, new DishesPrice(), dishes);
            reloadPriceData.run();
        });
        Button edit = new Button("编辑");
        edit.setOnAction(evt -> {
            DishesPrice selectedPrices = tv.getSelectionModel().getSelectedItem();
            if (selectedPrices == null) {
                AlertBuilder.ERROR("请选择操作记录");
                return;
            }
            editPrice(mw, selectedPrices, dishes);
            reloadPriceData.run();
        });
        Button del = new Button("删除");
        del.setOnAction(evt -> {
            DishesPrice selectedPrices = tv.getSelectionModel().getSelectedItem();
            if (selectedPrices == null) {
                AlertBuilder.ERROR("请选择操作记录");
                return;
            }
            dishesPriceDAO.deleteByPK(selectedPrices.getDishesPriceId());
            reloadPriceData.run();
        });
        form.addLine(newCenterLine(add, edit, del));
        mw.setScene(new Scene(form));
        mw.showAndWait();
        loadData();
    }

    private void editPrice(Window parent, DishesPrice dishesPrice, Dishes dishes) {
        ModelWindow mw = new ModelWindow(parent, "编辑多价格");
        mw.setWidth(400);
        mw.setHeight(300);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        form.setPadding(new Insets(20, 0, 0, 15));
        //
        Label priceName = new Label("价格名称:");
        priceName.setPrefWidth(150);
        TextField priceNameInput = new TextField(dishesPrice.getDishesPriceName());
        //
        Label price = new Label("价格:");
        price.setPrefWidth(150);
        TextField priceInput = new TextField(new Money(dishes.getDishesPrice()).toString());
        if (dishesPrice.getDishesPriceId() != null) {
            priceInput.setText(new Money(dishesPrice.getDishesPrice()).toString());
        }
        form.addLine(newLine(priceName, priceNameInput));
        form.addLine(newLine(price, priceInput));
        //
        Button button = new Button("确 定");
        button.setOnAction(evt -> {
            dishesPrice.setDishesPrice(CommonUtils.parseDouble(priceInput.getText(), null));
            dishesPrice.setDishesPriceName(priceNameInput.getText());
            if (dishesPrice.getDishesPriceId() == null) {
                dishesPrice.setDishesId(dishes.getDishesId());
                dishesPrice.setCreatTime(DateBuilder.now().mills());
                dishesPriceDAO.insert(dishesPrice);
            } else {
                dishesPriceDAO.updateByPK(dishesPrice);
            }
            mw.close();
        });
        form.addLine(newCenterLine(button));
        mw.setScene(new Scene(form));
        mw.showAndWait();
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
        String dishesPrice;
        String dishesStock;
        String dishesDescription;
        ImageSrc dishesImgs;
        String dishesUnitName;
        StringProperty dishesStatus = new SimpleStringProperty();
        String validTime;
        Operations operations;
    }
}
