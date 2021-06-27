package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.CartItemBO;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class CartView extends VBox {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);

    private DeskOrderParam param;
    private TableView tv = new TableView();

    public CartView(DeskOrderParam param) {
        this.param = param;
        this.getChildren().add(tableList());
        this.getChildren().add(separator());
        this.getChildren().add(functions());
    }

    private HBox functions() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        Button remove = new Button("删除");
        Button place = new Button("直接下单");
        box.getChildren().add(remove);
        box.getChildren().add(place);
        remove.setOnMouseClicked(evt -> {
            ObservableList<CartItemBO> list = tv.getSelectionModel().getSelectedItems();
            list.forEach(x -> {
                LogUtils.info("删除:" + JSON.toJSONString(x));
                tv.getItems().remove(x);
            });
        });
        place.setOnMouseClicked(evt -> {
            try {
                PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
                req.setDeskId(param.getDeskId());
                req.setOrderId(param.getOrderId());
                cartService.createOrder(req);
                Alert _alert = new Alert(AlertType.INFORMATION);
                _alert.setTitle("通知消息");
                _alert.setHeaderText("下单成功");
                _alert.showAndWait();
            } catch (Exception ex) {
                Alert _alert = new Alert(AlertType.ERROR);
                _alert.setTitle("通知消息");
                _alert.setHeaderText("下单失败:" + ex.getMessage());
                _alert.showAndWait();
            }
        });
        return box;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
    }

    private TableView tableList() {
        tv.getItems().clear();
        try {
            tv.getColumns().addAll(
                    newCol("菜品ID", "dishesId", 100),
                    newCol("菜品名称", "dishesName", 200),
                    newCol("价格", "dishesPrice", 100),
                    newCol("数量", "nums", 100)
            );
            tv.setItems(loadCartItems());
        } catch (Exception ex) {
            LogUtils.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return tv;
    }

    private ObservableList<CartItemBO> loadCartItems() {
        try {
            List<CartItemVO> list = cartService.selectByDeskId(param.getDeskId());
            List<CartItemBO> bolist = list.stream().map(it -> {
                Dishes dishes = dishesDAO.getById(it.getDishesId());
                CartItemBO bo = new CartItemBO();
                bo.setDishesId(it.getDishesId() + "");
                bo.setNums(it.getNums() + "");
                if (dishes != null) {
                    bo.setDishesName(dishes.getDishesName());
                    bo.setDishesPrice(CommonUtils.formatMoney(dishes.getDishesPrice()));
                }
                return bo;
            }).collect(Collectors.toList());
            return FXCollections.observableArrayList(bolist);
        } catch (Exception ex) {
            LogUtils.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return FXCollections.observableArrayList(new ArrayList<>());
    }

    private TableColumn newCol(String name, String filed, double width) {
        TableColumn<CartItemBO, SimpleStringProperty> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(new Callback<TableColumn<CartItemBO, SimpleStringProperty>, TableCell<CartItemBO, SimpleStringProperty>>() {
            public TableCell<CartItemBO, String> call(TableColumn param) {
                return new TableCell<CartItemBO, String>() {
                    public void updateItem(String item, boolean empty) {
                        if (CommonUtils.isNotBlank(item)) {
                            if (item.contains("@")) {
                                setTextFill(Color.RED);
                            } else {
                                setAlignment(Pos.CENTER_RIGHT);
                            }
                            setText(item);
                        }
                    }
                };
            }
        });
        return c;
    }
}
