package com.xjh.startup.view;


import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.common.utils.TableViewUtils.rowIndex;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesValidTime;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.dao.query.DishesQuery;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.IntStringPair;

import cn.hutool.core.lang.Holder;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import lombok.Data;

public class DishesManageListView extends SimpleForm implements Initializable {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesPriceDAO dishesPriceDAO = GuiceContainer.getInstance(DishesPriceDAO.class);

    ObjectProperty<DishesQuery> cond = new SimpleObjectProperty<>(new DishesQuery());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();
    Holder<Integer> totalPage = new Holder<>();

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
        int pageCount = dishesService.pageCount(cond.get());
        totalPage.set(pageCount / cond.get().getPageSize() + 1);
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
                    bo.setDishesStock("??????");
                }
                StringProperty onOffTitle = new SimpleStringProperty();
                Predicate<Dishes> isOnline = d -> d.getDishesStatus() != null && d.getDishesStatus() == 1;
                bo.getDishesStatus().set(isOnline.test(dishes) ?
                        RichText.create("??????").with(Color.BLUE) :
                        RichText.create("??????").with(Color.RED));
                onOffTitle.set(isOnline.test(dishes) ? "??????" : "??????");

                Operations operations = new Operations();
                OperationButton edit = new OperationButton("??????", () -> openEditor(dishes));
                OperationButton onoff = new OperationButton("?????????", cv -> {
                    if (onOffTitle.get().equals("??????")) {
                        changeDishesStatus(dishes.getDishesId(), EnumDishesStatus.ON);
                        onOffTitle.set("??????");
                        bo.getDishesStatus().set(RichText.create("??????").with(Color.BLUE));
                    } else {
                        onOffTitle.set("??????");
                        changeDishesStatus(dishes.getDishesId(), EnumDishesStatus.OFF);
                        bo.getDishesStatus().set(RichText.create("??????").with(Color.RED));
                    }
                });
                onoff.setTitleProperty(onOffTitle);
                OperationButton del = new OperationButton("??????", () -> {
                });
                OperationButton editPrice = new OperationButton("???????????????", () -> openPriceEditor(dishes));
                OperationButton editValidTime = new OperationButton("???????????????", () -> openValidTimeEditor(dishes));
                operations.add(edit);
                operations.add(onoff);
                operations.add(del);
                operations.add(editPrice);
                operations.add(editValidTime);
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
        Label nameLabel = new Label("??????:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(130);
        nameCondBlock.getChildren().add(newCenterLine(nameLabel, nameInput));

        // status
        HBox statusCondBlock = new HBox();
        Label statusLabel = new Label("??????:");
        ObservableList<String> options = FXCollections.observableArrayList("??????", "??????", "??????");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.getSelectionModel().selectFirst();
        statusCondBlock.getChildren().add(newCenterLine(statusLabel, modelSelect));

        Button queryBtn = new Button("??????");
        queryBtn.setOnAction(evt -> {
            DishesQuery q = cond.get().newVersion();
            q.setPageNo(1);
            q.setDishesName(CommonUtils.trim(nameInput.getText()));
            String selectedStatus = modelSelect.getSelectionModel().getSelectedItem();
            if (CommonUtils.eq(selectedStatus, "??????")) q.setStatus(1);
            if (CommonUtils.eq(selectedStatus, "??????")) q.setStatus(0);
            cond.set(q);
        });

        Button addNew = new Button("????????????");
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
                newCol("??????", "dishesName", 200),
                newCol("??????", "dishesImgs", 200),
                newCol("??????", "dishesStatus", 80),
                newCol("??????", "dishesPrice", 100),
                newCol("??????", "dishesStock", 100),
                newCol("??????", "operations", 400)
        );
        tableView.setItems(items);
        tableView.setPrefHeight(height);
        addLine(tableView);
    }

    private void buildFoot() {
        cond.addListener((ob, o, n) -> loadData());
        Button prev = new Button("?????????");
        prev.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            c.decreasePageNo();
            cond.set(c);
        });
        Button next = new Button("?????????");
        next.setOnMouseClicked(e -> {
            DishesQuery c = cond.get().newVersion();
            if (totalPage.get() != null && c.getPageNo() >= totalPage.get()) {
                AlertBuilder.ERROR("????????????????????????");
                return;
            }
            c.increasePageNo();
            cond.set(c);
        });
        HBox line = newCenterLine(prev, next);
        line.setPadding(new Insets(10, 0, 0, 0));
        addLine(line);
    }

    private void openEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "????????????");
        DishesEditView view = new DishesEditView(dishes);
        view.setPrefWidth(window.getWidth() * 0.75);
        mw.setScene(new Scene(view));
        mw.showAndWait();
        loadData();
    }

    private void openPriceEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "???????????????");
        mw.setWidth(550);
        mw.setHeight(450);
        SimpleForm form = new SimpleForm();
        form.setSpacing(10);
        // ????????????
        Label dishesNameLabel = new Label("????????????:");
        Label dishesName = new Label(dishes.getDishesName());
        form.addLine(newLine(dishesNameLabel, dishesName));
        // ????????????
        TableView<DishesPrice> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("????????????", DishesPrice::getDishesPriceName, 250),
                newCol("??????", DishesPrice::getDishesPrice, 250)
        );

        Runnable reloadPriceData = () -> {
            tv.getItems().clear();
            tv.getItems().addAll(dishesPriceDAO.queryByDishesId(dishes.getDishesId()));
            tv.refresh();
        };
        form.addLine(tv);
        reloadPriceData.run();
        // ????????????
        Button add = new Button("??????");
        add.setOnAction(evt -> {
            editPrice(mw, new DishesPrice(), dishes);
            reloadPriceData.run();
        });
        Button edit = new Button("??????");
        edit.setOnAction(evt -> {
            DishesPrice selectedPrices = tv.getSelectionModel().getSelectedItem();
            if (selectedPrices == null) {
                AlertBuilder.ERROR("?????????????????????");
                return;
            }
            editPrice(mw, selectedPrices, dishes);
            reloadPriceData.run();
        });
        Button del = new Button("??????");
        del.setOnAction(evt -> {
            DishesPrice selectedPrices = tv.getSelectionModel().getSelectedItem();
            if (selectedPrices == null) {
                AlertBuilder.ERROR("?????????????????????");
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

    private void openValidTimeEditor(Dishes dishes) {
        Window window = this.getScene().getWindow();
        ModelWindow mw = new ModelWindow(window, "???????????????");
        mw.setWidth(550);
        mw.setHeight(450);
        Dishes dd = dishesService.getById(dishes.getDishesId());

        ObservableList<DishesValidTime> data = FXCollections.observableArrayList();
        CommonUtils.safeRun(() -> {
            List<String> validTimeList = JSONArray.parseArray(dd.getValidTime(), String.class);
            CommonUtils.forEach(validTimeList, s -> {
                DishesValidTime t = DishesValidTime.from(s);
                if (t != null) {
                    data.add(t);
                }
            });
        });

        TableView<DishesValidTime> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("??????", rowIndex(), 100),
                newCol("????????????", it -> it.getStart() + "???" + it.getEnd(), 200)
        );
        tv.setPrefWidth(350);
        tv.setPrefHeight(300);

        Holder<Integer> currDay = Holder.of(1);
        Runnable refreshData = () -> {
            tv.getItems().clear();
            tv.getItems().addAll(data.stream().filter(it -> it.getDay().equals(currDay.get())).collect(Collectors.toList()));
            tv.refresh();
        };
        refreshData.run(); // ????????????

        GridPane grid = new GridPane();
        // ??????
        BiFunction<Integer, String, Button> createButton = (day, name) -> {
            Button btn = new Button(name);
            btn.setOnAction(evt -> {
                currDay.set(day);
                refreshData.run();
            });
            return btn;
        };
        VBox week = new VBox();
        week.setSpacing(10);
        week.setPadding(new Insets(30, 0, 0, 0));
        week.setAlignment(Pos.TOP_CENTER);
        week.getChildren().add(createButton.apply(1, "?????????"));
        week.getChildren().add(createButton.apply(2, "?????????"));
        week.getChildren().add(createButton.apply(3, "?????????"));
        week.getChildren().add(createButton.apply(4, "?????????"));
        week.getChildren().add(createButton.apply(5, "?????????"));
        week.getChildren().add(createButton.apply(6, "?????????"));
        week.getChildren().add(createButton.apply(7, "?????????"));
        week.setPrefWidth(80);
        grid.add(week, 0, 0);
        // ????????????
        VBox timeRange = new VBox();
        timeRange.setSpacing(10);
        timeRange.setPadding(new Insets(0, 0, 10, 0));

        timeRange.getChildren().add(tv);
        Button update = new Button("????????????");
        update.setOnAction(evt -> {
            Dishes u = new Dishes();
            u.setDishesId(dishes.getDishesId());
            u.setValidTime(JSON.toJSONString(data.stream().map(DishesValidTime::asStr).collect(Collectors.toList())));
            dishesService.save(u);
            AlertBuilder.INFO("????????????");
            mw.close();
        });
        timeRange.getChildren().add(update);
        grid.add(timeRange, 1, 0);
        // ??????
        VBox operations = new VBox();
        operations.setSpacing(10);
        operations.setPadding(new Insets(30, 0, 0, 10));
        Button add = new Button("????????????");
        add.setOnAction(evt -> {
            DishesValidTime newTime = new DishesValidTime();
            newTime.setDay(currDay.get());
            editValidTime(mw, newTime);
            if (newTime.isValid()) {
                data.add(newTime);
                refreshData.run();
            }
        });
        operations.getChildren().add(add);
        Button edit = new Button("????????????");
        edit.setOnAction(evt -> {
            DishesValidTime s = tv.getSelectionModel().getSelectedItem();
            if (s == null) {
                AlertBuilder.ERROR("?????????????????????");
                return;
            }
            editValidTime(mw, s);
            refreshData.run();
        });
        operations.getChildren().add(edit);

        Button del = new Button("????????????");
        del.setOnAction(evt -> {
            DishesValidTime tr = tv.getSelectionModel().getSelectedItem();
            data.remove(tr);
            refreshData.run();
        });
        operations.getChildren().add(del);
        grid.add(operations, 2, 0);
        mw.setScene(new Scene(grid));
        mw.showAndWait();

    }

    private void editPrice(Window parent, DishesPrice dishesPrice, Dishes dishes) {
        ModelWindow mw = new ModelWindow(parent, "???????????????");
        mw.setWidth(400);
        mw.setHeight(300);
        SimpleForm form = new SimpleForm();
        form.setSpacing(15);
        form.setPadding(new Insets(20, 0, 0, 15));
        //
        Label priceName = new Label("????????????:");
        priceName.setPrefWidth(150);
        TextField priceNameInput = new TextField(dishesPrice.getDishesPriceName());
        //
        Label price = new Label("??????:");
        price.setPrefWidth(150);
        TextField priceInput = new TextField(new Money(dishes.getDishesPrice()).toString());
        if (dishesPrice.getDishesPriceId() != null) {
            priceInput.setText(new Money(dishesPrice.getDishesPrice()).toString());
        }
        form.addLine(newLine(priceName, priceNameInput));
        form.addLine(newLine(price, priceInput));
        //
        Button button = new Button("??? ???");
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
            AlertBuilder.INFO("??????????????????");
        } else {
            AlertBuilder.ERROR("~~??????????????????~~~~" + rs.getMsg());
        }
    }

    private void editValidTime(Window pwindow, DishesValidTime time) {
        ModelWindow w = new ModelWindow(pwindow, "????????????");
        w.setHeight(150);
        w.setWidth(pwindow.getWidth());

        SimpleForm form = new SimpleForm();
        w.setScene(new Scene(form));

        form.setSpacing(10);
        form.setPadding(new Insets(6, 0, 0, 0));
        Label label = new Label("????????????:");
        ComboBox<IntStringPair> hours = createHourSelector(time.getStartHour());
        ComboBox<IntStringPair> hours2 = createHourSelector(time.getEndHour());
        ComboBox<IntStringPair> minute = createMinuteSelector(time.getStartMinute());
        ComboBox<IntStringPair> minute2 = createMinuteSelector(time.getEndMinute());
        form.addLine(newCenterLine(
                label, hours, new Label(":"), minute,
                new Label("???"),
                hours2, new Label(":"), minute2));
        Button submit = new Button("??????");
        submit.setOnAction(evt -> {
            time.setStart(hours.getSelectionModel().getSelectedItem().getValue()
                    + ":"
                    + minute.getSelectionModel().getSelectedItem().getValue());
            time.setEnd(hours2.getSelectionModel().getSelectedItem().getValue()
                    + ":"
                    + minute2.getSelectionModel().getSelectedItem().getValue());
            if (!time.isValid()) {
                AlertBuilder.ERROR("???????????????????????????");
                return;
            }
            w.close();
        });
        form.addLine(newCenterLine(submit));

        w.showAndWait();
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
        ObjectProperty<RichText> dishesStatus = new SimpleObjectProperty<>();
        String validTime;
        Operations operations;
    }

    private ComboBox<IntStringPair> createHourSelector(Integer init) {
        List<IntStringPair> hours = IntStream.range(0, 24)
                .mapToObj(i -> new IntStringPair(i, i < 10 ? "0" + i : "" + i))
                .collect(Collectors.toList());
        ComboBox<IntStringPair> combo = new ComboBox<>(FXCollections.observableArrayList(hours));
        IntStringPair.select(combo, init, null);
        return combo;
    }

    private ComboBox<IntStringPair> createMinuteSelector(Integer init) {
        List<IntStringPair> hours = IntStream.range(0, 60)
                .mapToObj(i -> new IntStringPair(i, i < 10 ? "0" + i : "" + i))
                .collect(Collectors.toList());
        ComboBox<IntStringPair> combo = new ComboBox<>(FXCollections.observableArrayList(hours));
        IntStringPair.select(combo, init, null);
        return combo;
    }
}
