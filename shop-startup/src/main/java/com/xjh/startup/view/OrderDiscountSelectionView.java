package com.xjh.startup.view;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.xjh.common.enumeration.EnumDiscountType;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.OrderDiscountVO;
import com.xjh.dao.dataobject.DiscountDO;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.DiscountDAO;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DiscountApplyReq;
import com.xjh.startup.view.model.DiscountTypeBO;

import cn.hutool.core.codec.Base64;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class OrderDiscountSelectionView extends SmallForm {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    DiscountDAO discountDAO = GuiceContainer.getInstance(DiscountDAO.class);

    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox discountContentLine = new VBox();
        discountContentLine.setSpacing(10);
        Holder<Supplier<DiscountApplyReq>> discountHolder = new Holder<>();
        // ??????????????????
        {
            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.selectedToggleProperty().addListener((x, o, n) -> {
                int select = (int) n.getUserData();
                if (select == 1) {
                    Logger.info("?????????");
                    Label voucherLabel = new Label("?????????:");
                    TextField voucher = new TextField();

                    Label cardLabel = new Label("?????????:");
                    TextField card = new TextField();
                    discountHolder.hold(() -> {
                        String voucherNo = voucher.getText();
                        String cardNo = card.getText();
                        if (CommonUtils.isNotBlank(voucherNo) && CommonUtils.isNotBlank(cardNo)) {
                            AlertBuilder.ERROR("???????????????????????????????????????");
                            return null;
                        }
                        if (CommonUtils.isBlank(voucherNo) && CommonUtils.isBlank(cardNo)) {
                            return null;
                        }
                        if (CommonUtils.isNotBlank(voucherNo)) {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setType(EnumDiscountType.COUPON);
                            req.setDiscountName("?????????");
                            req.setDiscountCode(voucherNo);
                            return req;
                        } else {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setType(EnumDiscountType.CARD);
                            req.setDiscountName("?????????");
                            req.setDiscountCode(cardNo);
                            return req;
                        }
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newCenterLine(voucherLabel, voucher),
                            newCenterLine(cardLabel, card));
                } else if (select == 2) {
                    Logger.info("????????????");
                    ComboBox<DiscountTypeBO> optList = getDiscountOptions();
                    optList.setPrefWidth(160);
                    Label label = new Label("????????????:");

                    Label pwdLabel = new Label("????????????:");
                    PasswordField pwd = new PasswordField();
                    pwd.setPrefWidth(160);

                    discountHolder.hold(() -> {
                        DiscountTypeBO bo = optList.getSelectionModel().getSelectedItem();
                        if (bo == null) {
                            return null;
                        }
                        DiscountApplyReq req = new DiscountApplyReq();
                        req.setType(EnumDiscountType.MANAGER);
                        req.setDiscountName(bo.getDiscountName());
                        req.setDiscountRate(bo.getDiscountRate());
                        req.setDiscountCode(EnumDiscountType.MANAGER.name());
                        req.setManagerPwd(CommonUtils.trim(pwd.getText()));
                        return req;
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newCenterLine(label, optList),
                            newCenterLine(pwdLabel, pwd));
                } else {
                    Logger.info("????????????");
                }
            });

            Label discountTypeLabel = new Label("????????????:");

            RadioButton coupon = new RadioButton("????????????");
            coupon.setToggleGroup(toggleGroup);
            coupon.setUserData(1);

            RadioButton manager = new RadioButton("????????????");
            manager.setToggleGroup(toggleGroup);
            manager.setUserData(2);
            manager.setSelected(true);

            addLine(newCenterLine(discountTypeLabel, coupon, manager));
        }
        {
            addLine(discountContentLine);
        }
        {
            Button cancel = new Button("??????");
            cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());

            Button button = new Button("????????????");
            button.setOnMouseClicked(evt -> {
                if (discountHolder.get() != null) {
                    DiscountApplyReq req = discountHolder.get().get();
                    if (req == null) {
                        AlertBuilder.ERROR("?????????????????????");
                        return;
                    }
                    if (req.getType() == EnumDiscountType.MANAGER
                            && !storeService.checkManagerPwd(req.getManagerPwd())) {
                        AlertBuilder.ERROR("??????????????????");
                        return;
                    }
                    if (req.getDiscountRate() <= 0.001 || req.getDiscountRate() >= 0.999) {
                        AlertBuilder.ERROR("??????????????????");
                        return;
                    }
                    Logger.info(JSON.toJSONString(req));
                    this.handleDiscount(param, req);
                }
            });
            addLine(newCenterLine(cancel, button));
        }
    }

    private ComboBox<DiscountTypeBO> getDiscountOptions() {
        List<DiscountDO> discountList = discountDAO.selectList(new DiscountDO());
        ObservableList<DiscountTypeBO> list =
                FXCollections.observableArrayList(discountList.stream()
                        .map(it -> new DiscountTypeBO(it.getDiscountName(), it.getRate()))
                        .collect(Collectors.toSet()));
        ComboBox<DiscountTypeBO> optList = new ComboBox<>(list);
        optList.setConverter(new StringConverter<DiscountTypeBO>() {
            @Override
            public String toString(DiscountTypeBO object) {
                return object.getDiscountName();
            }

            @Override
            public DiscountTypeBO fromString(String string) {
                return null;
            }
        });
        return optList;
    }

    private void handleDiscount(DeskOrderParam param, DiscountApplyReq req) {
        if (param == null || req == null) {
            return;
        }
        Integer orderId = param.getOrderId();
        // ??????orderDishes
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
        // ??????discount Checker
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        // ???????????????????????????
        List<OrderDishes> discountableOrderDishes = CommonUtils.filter(orderDishesList, discountChecker);
        //
        for (OrderDishes od : discountableOrderDishes) {
            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(od.getOrderDishesId());
            update.setOrderDishesDiscountPrice(od.getOrderDishesPrice() * req.getDiscountRate());
            Result<Integer> rs = orderDishesService.updatePrimaryKey(update);
            if (!rs.isSuccess()) {
                AlertBuilder.ERROR(rs.getMsg());
                return;
            }
        }
        // ??????????????????
        OrderDiscountVO orderDiscount = new OrderDiscountVO();
        orderDiscount.setDiscountName(req.getDiscountName());
        orderDiscount.setRate(new Double(req.getDiscountRate()).floatValue());
        orderDiscount.setType(req.getType().code());
        orderDiscount.setDiscountCode(req.getDiscountCode());
        orderDiscount.setDiscountId(0);

        Order orderUpdate = new Order();
        orderUpdate.setOrderId(param.getOrderId());
        orderUpdate.setOrderDiscountInfo(Base64.encode(JSON.toJSONString(orderDiscount)));
        orderUpdate.setDiscountReason(req.getDiscountName());
        orderService.updateByOrderId(orderUpdate);
        AlertBuilder.INFO("??????????????????");
        this.getScene().getWindow().hide();
    }

}
