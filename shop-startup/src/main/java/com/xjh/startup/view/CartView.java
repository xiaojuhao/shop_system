package com.xjh.startup.view;

import com.xjh.common.model.CartItemBO;
import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.*;
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
import com.xjh.service.ws.NotifyService;
import com.xjh.service.ws.SocketUtils;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xjh.common.utils.CommonUtils.sizeOf;
import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.service.domain.DishesTypeService.toDishesTypeName;

public class CartView extends VBox {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    NotifyService notifyService = GuiceContainer.getInstance(NotifyService.class);
    Runnable onPlaceOrder;

    TableView<CartItemBO> tv = new TableView<>();
    DeskOrderParam param;

    static Holder<WeakReference<CartView>> holder = new Holder<>();

    public CartView(DeskOrderParam param, Runnable onPlaceOrder) {
        this.onPlaceOrder = onPlaceOrder;
        this.param = param;
        this.getChildren().add(tableList());
        this.getChildren().add(new Separator(Orientation.HORIZONTAL));
        this.getChildren().add(buttons());

        holder.hold(new WeakReference<>(this));
    }

    private HBox buttons() {
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

    private TableView<CartItemBO> tableList() {
        try {
            tv.getColumns().addAll(newCol("序号", "seqNo", 100), // 序号
                    newCol("菜品类型", "dishesTypeName", 100), // 菜品类型
                    newCol("菜品名称", "dishesName", 200), // 菜品名称
                    newCol("价格", "dishesPrice", 100), // 价格
                    newCol("数量", "nums", 150), // 数量
                    newCol("小计", "totalPrice", 100), // 小计
                    newCol("备注", "attrRemark", 100));
            reloadData();
        } catch (Exception ex) {
            Logger.error("查询购物车异常:" + param.getDeskName() + ", " + ex.getMessage());
        }
        return tv;
    }

    public static void refreshCartList(int deskId) {
        if (holder.get() == null || holder.get().get() == null) {
            return;
        }
        if(holder.get().get().param.getDeskId().equals(deskId)) {
            Platform.runLater(() -> holder.get().get().reloadData());
        }
    }

    private void reloadData() {
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

        List<Integer> removedIds = new ArrayList<>();
        ObservableList<CartItemBO> list = tv.getSelectionModel().getSelectedItems();
        list.forEach(x -> removedIds.add(x.getCartDishesId()));
        if (CommonUtils.isEmpty(removedIds)) {
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
        List<Integer> removedIdx = new ArrayList<>();
        List<CartItemVO> items = cartVO.getContents();
        List<CartItemVO> newItems = new ArrayList<>();
        int itemLen = sizeOf(items);
        for (int i = 0; i < itemLen; i++) {
            if (!removedIds.contains(items.get(i).getCartDishesId())) {
                newItems.add(items.get(i));
            } else {
                removedIdx.add(i);
            }
        }
        cartVO.setContents(newItems);
        Result<CartVO> updateRs = cartService.updateCart(deskId, cartVO);
        if (updateRs.isSuccess()) {
            reloadData();
        } else {
            AlertBuilder.ERROR("删除失败," + updateRs.getMsg());
        }

        SocketUtils.delay(() -> notifyService.removeDishesFromCart(deskId, removedIdx), 1);
    }

    private void doUpdateItemNum(Integer deskId, Integer cartDishesId, Integer num) {
        CartVO cart = cartService.getCart(deskId).getData();
        if (cart != null) {
            CommonUtils.forEach(cart.getContents(), item -> {
                if (CommonUtils.eq(item.getCartDishesId(), cartDishesId)) {
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
                    bo.setCartDishesId(it.getCartDishesId());
                    bo.setDishesId(it.getDishesId());
                    InputNumber num = InputNumber.from(OrElse.orGet(it.getNums(), 1));
                    num.setOnChange(n -> doUpdateItemNum(param.getDeskId(), it.getCartDishesId(), n));
                    bo.setNums(num);
                    String dishesTypeName = toDishesTypeName(typeMap, dishes.getDishesTypeId());
                    bo.setDishesTypeName(new RichText(dishesTypeName).with(Color.RED));
                    bo.setDishesName(new RichText(dishes.getDishesName()));
                    bo.setDishesPrice(new Money(dishes.getDishesPrice()));
                    bo.setTotalPrice(new Money(bo.getNums().getNumber() * dishes.getDishesPrice()));
                    bo.setAttrRemark(DishesAttributeHelper.generateSelectedAttrDigest(it.getDishesAttrs()));
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
