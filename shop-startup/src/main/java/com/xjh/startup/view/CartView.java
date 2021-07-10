package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.CartItemBO;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CartView extends VBox {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);

    private final DeskOrderParam param;
    private final TableView<CartItemBO> tv = new TableView<>();

    public CartView(DeskOrderParam param) {
        this.param = param;
        this.getChildren().add(tableList());
        this.getChildren().add(separator());
        this.getChildren().add(functions());


    }

    private HBox functions() {
        Integer deskId = param.getDeskId();

        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        Button remove = new Button("删除");
        Button place = new Button("确定下单");
        box.getChildren().add(remove);
        box.getChildren().add(place);
        remove.setOnMouseClicked(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("删除购物车");
            alert.setContentText("确定要删除这行记录吗?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }

            List<String> removeItems = new ArrayList<>();
            ObservableList<CartItemBO> list = tv.getSelectionModel().getSelectedItems();
            list.forEach(x -> removeItems.add(x.getDishesId() + ""));
            if (CommonUtils.isEmpty(removeItems)) {
                AlertBuilder.ERROR("删除购物车失败", "请选择删除记录");
                return;
            }
            Result<CartVO> getCartRs = cartService.getCartOfDesk(deskId);
            if (!getCartRs.isSuccess()) {
                AlertBuilder.ERROR("获取购物车信息失败", getCartRs.getMsg());
                return;
            }
            CartVO cartVO = getCartRs.getData();
            if (cartVO == null || CommonUtils.isEmpty(cartVO.getContents())) {
                return;
            }
            List<CartItemVO> items = cartVO.getContents();
            items = items.stream()
                    .filter(it -> !removeItems.contains(it.getDishesId() + ""))
                    .collect(Collectors.toList());
            cartVO.setContents(items);
            Result<CartVO> updateRs = cartService.updateCart(deskId, cartVO);
            if (updateRs.isSuccess()) {
                reloadData();
                // AlertBuilder.INFO("删除成功");
            } else {
                AlertBuilder.ERROR("删除失败," + updateRs.getMsg());
            }
        });
        place.setOnMouseClicked(evt -> {
            PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
            req.setDeskId(deskId);
            req.setOrderId(param.getOrderId());
            Result<String> createOrderRs = cartService.createOrder(req);
            if (createOrderRs.isSuccess()) {
                AlertBuilder.INFO("下单成功");
                this.getScene().getWindow().hide();
            } else {
                AlertBuilder.ERROR(createOrderRs.getMsg());
            }
        });
        return box;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
    }

    private TableView<CartItemBO> tableList() {
        try {
            tv.getColumns().addAll(
                    newCol("序号", "seqNo", 100),
                    newCol("菜品类型", "dishesTypeName", 100),
                    newCol("菜品名称", "dishesName", 200),
                    newCol("价格", "dishesPrice", 100),
                    newCol("数量", "nums", 100),
                    newCol("小计", "totalPrice", 100)
            );
            reloadData();
        } catch (Exception ex) {
            LogUtils.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return tv;
    }

    private void reloadData() {
        ObservableList<CartItemBO> observableList = FXCollections.observableList(loadCartItems());
        tv.setItems(observableList);
        tv.refresh();
    }

    private List<CartItemBO> loadCartItems() {
        try {
            Map<Integer, DishesType> typeMap = dishesTypeService.dishesTypeMap();
            List<CartItemVO> list = cartService.selectByDeskId(param.getDeskId());
            AtomicInteger seqNo = new AtomicInteger();
            return list.stream().map(it -> {
                Dishes dishes = dishesDAO.getById(it.getDishesId());
                CartItemBO bo = new CartItemBO();
                bo.setSeqNo(seqNo.incrementAndGet());
                bo.setDishesId(it.getDishesId());
                bo.setNums(CommonUtils.orElse(it.getNums(), 1));
                if (dishes != null) {
                    DishesType type = typeMap.get(dishes.getDishesTypeId());
                    if (type != null) {
                        bo.setDishesTypeName(new RichText(type.getTypeName()).with(Color.RED));
                    }
                    bo.setDishesName(new RichText(dishes.getDishesName()));
                    bo.setDishesPrice(new Money(dishes.getDishesPrice()));
                    bo.setTotalPrice(new Money(bo.getNums() * dishes.getDishesPrice()));
                }
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            LogUtils.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    private TableColumn<CartItemBO, Object> newCol(String name, String filed, double width) {
        TableColumn<CartItemBO, Object> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(col -> {
            TableCell<CartItemBO, Object> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, ov, nv) -> {
                if (nv == null) {
                    return;
                }
                if (nv instanceof RichText) {
                    RichText val = (RichText) nv;
                    cell.textProperty().set(CommonUtils.stringify(val.getText()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else if (nv instanceof Money) {
                    Money val = (Money) nv;
                    cell.textProperty().set(CommonUtils.formatMoney(val.getAmount()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else {
                    cell.textProperty().set(CommonUtils.stringify(nv));
                }
            });
            return cell;
        });
        return c;
    }
}
