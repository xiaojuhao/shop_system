package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.TableViewUtils;
import com.xjh.common.utils.cellvalue.InputNumber;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.startup.foundation.ioc.GuiceContainer;
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
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CartView extends VBox {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    Runnable onPlaceOrder = null;

    public CartView(DeskOrderParam param, Runnable onPlaceOrder) {
        this.onPlaceOrder = onPlaceOrder;
        TableView<CartItemBO> tv = new TableView<>();
        this.getChildren().add(tableList(param, tv));
        this.getChildren().add(new Separator(Orientation.HORIZONTAL));
        this.getChildren().add(buttons(param, tv));
    }

    private HBox buttons(DeskOrderParam param, TableView<CartItemBO> tv) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);

        Button removeBtn = new Button("删除");
        removeBtn.setOnMouseClicked(evt -> doDeleteItem(param, tv));

        Button placeBtn = new Button("确定下单");
        placeBtn.setOnMouseClicked(evt -> doPlaceOrder(param));

        box.getChildren().addAll(removeBtn, placeBtn);
        return box;
    }

    private TableView<CartItemBO> tableList(DeskOrderParam param, TableView<CartItemBO> tv) {
        try {
            tv.getColumns().addAll(
                    TableViewUtils.newCol("序号", "seqNo", 100),
                    TableViewUtils.newCol("菜品类型", "dishesTypeName", 100),
                    TableViewUtils.newCol("菜品名称", "dishesName", 200),
                    TableViewUtils.newCol("价格", "dishesPrice", 100),
                    TableViewUtils.newCol("数量", "nums", 150),
                    TableViewUtils.newCol("小计", "totalPrice", 100)
            );
            reloadData(param, tv);
        } catch (Exception ex) {
            Logger.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return tv;
    }

    private void reloadData(DeskOrderParam param, TableView<CartItemBO> tv) {
        tv.setItems(FXCollections.observableList(loadCartItems(param)));
        tv.refresh();
    }

    private void doPlaceOrder(DeskOrderParam param) {
        PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
        req.setDeskId(param.getDeskId());
        req.setOrderId(param.getOrderId());
        req.setAccountId(CurrentAccount.currentAccountId());
        Result<String> createOrderRs = cartService.createOrder(req);
        if (createOrderRs.isSuccess()) {
            AlertBuilder.INFO("下单成功");
            this.getScene().getWindow().hide();
            CommonUtils.safeRun(onPlaceOrder);
        } else {
            AlertBuilder.ERROR(createOrderRs.getMsg());
        }
    }

    private void doDeleteItem(DeskOrderParam param, TableView<CartItemBO> tv) {
        Integer deskId = param.getDeskId();

        List<Integer> removedDishesIds = new ArrayList<>();
        ObservableList<CartItemBO> list = tv.getSelectionModel().getSelectedItems();
        list.forEach(x -> removedDishesIds.add(x.getDishesId()));
        if (CommonUtils.isEmpty(removedDishesIds)) {
            AlertBuilder.ERROR("删除购物车失败", "请选择删除记录");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除购物车");
        alert.setContentText("确定要删除这行记录吗?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) != ButtonType.OK) {
            return;
        }

        Result<CartVO> getCartRs = cartService.getCart(deskId);
        if (!getCartRs.isSuccess()) {
            AlertBuilder.ERROR("获取购物车信息失败", getCartRs.getMsg());
            return;
        }
        CartVO cartVO = getCartRs.getData();
        if (cartVO == null || CommonUtils.isEmpty(cartVO.getContents())) {
            return;
        }
        List<CartItemVO> items = cartVO.getContents();
        items = CommonUtils.filter(items, it -> !removedDishesIds.contains(it.getDishesId()));
        cartVO.setContents(items);
        Result<CartVO> updateRs = cartService.updateCart(deskId, cartVO);
        if (updateRs.isSuccess()) {
            reloadData(param, tv);
        } else {
            AlertBuilder.ERROR("删除失败," + updateRs.getMsg());
        }
    }

    private void doUpdateItemNum(Integer deskId, Integer dishesId, Integer num) {
        CartVO cart = cartService.getCart(deskId).getData();
        if (cart != null) {
            CommonUtils.forEach(cart.getContents(), item -> {
                if (CommonUtils.eq(item.getDishesId(), dishesId)) {
                    item.setNums(num);
                }
            });
            cartService.updateCart(deskId, cart);
        }
    }

    private List<CartItemBO> loadCartItems(DeskOrderParam param) {
        try {
            Map<Integer, DishesType> typeMap = dishesTypeService.dishesTypeMap();
            List<CartItemVO> list = cartService.getCartItems(param.getDeskId());
            List<Integer> dishesIds = CommonUtils.collect(list, CartItemVO::getDishesId);
            Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIds);
            AtomicInteger seqNo = new AtomicInteger();
            return CommonUtils.collect(list, it -> {
                Dishes dishes = dishesMap.get(it.getDishesId());
                if (dishes != null) {
                    CartItemBO bo = new CartItemBO();
                    bo.setSeqNo(seqNo.incrementAndGet());
                    bo.setDishesId(it.getDishesId());
                    InputNumber num = InputNumber.from(OrElse.orGet(it.getNums(), 1));
                    num.setOnChange(n -> doUpdateItemNum(param.getDeskId(), it.getDishesId(), n));
                    bo.setNums(num);
                    DishesType type = typeMap.get(dishes.getDishesTypeId());
                    if (type != null) {
                        bo.setDishesTypeName(new RichText(type.getTypeName()).with(Color.RED));
                    }
                    bo.setDishesName(new RichText(dishes.getDishesName()));
                    bo.setDishesPrice(new Money(dishes.getDishesPrice()));
                    bo.setTotalPrice(new Money(bo.getNums().getNumber() * dishes.getDishesPrice()));
                    return bo;
                } else {
                    return null;
                }
            });
        } catch (Exception ex) {
            Logger.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return new ArrayList<>();
    }
}
