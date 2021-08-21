package com.xjh.startup.view;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.StoreService;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DiscountApplyReq;
import com.xjh.startup.view.model.DiscountTypeBO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class OrderDiscountSelectionView extends VBox {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);

    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        VBox discountContentLine = new VBox();
        discountContentLine.setSpacing(10);
        Holder<Supplier<DiscountApplyReq>> discountHolder = new Holder<>();
        // 折扣方式选择
        {
            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.selectedToggleProperty().addListener((x, o, n) -> {
                int select = (int) n.getUserData();
                if (select == 1) {
                    Logger.info("优惠券");
                    Label voucherLabel = new Label("折扣券:");
                    TextField voucher = new TextField();

                    Label cardLabel = new Label("折扣卡:");
                    TextField card = new TextField();
                    discountHolder.hold(() -> {
                        String voucherNo = voucher.getText();
                        String cardNo = card.getText();
                        if (CommonUtils.isNotBlank(voucherNo) && CommonUtils.isNotBlank(cardNo)) {
                            AlertBuilder.ERROR("折扣券和卡折扣只能使用一种");
                            return null;
                        }
                        if (CommonUtils.isBlank(voucherNo) && CommonUtils.isBlank(cardNo)) {
                            return null;
                        }
                        if (CommonUtils.isNotBlank(voucherNo)) {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setDiscountType("voucher");
                            req.setDiscountName("优惠券");
                            req.setVoucherNo(voucherNo);
                            return req;
                        } else {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setDiscountType("card");
                            req.setDiscountName("折扣卡");
                            req.setCardNo(cardNo);
                            return req;
                        }
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newLine(voucherLabel, voucher),
                            newLine(cardLabel, card));
                } else if (select == 2) {
                    Logger.info("店长折扣");
                    ComboBox<DiscountTypeBO> optList = getDiscountOptions();
                    optList.setPrefWidth(160);
                    Label label = new Label("折扣类型:");

                    Label pwdLabel = new Label("确认密码:");
                    PasswordField pwd = new PasswordField();
                    pwd.setPrefWidth(160);

                    discountHolder.hold(() -> {
                        DiscountTypeBO bo = optList.getSelectionModel().getSelectedItem();
                        if (bo == null) {
                            return null;
                        }
                        DiscountApplyReq req = new DiscountApplyReq();
                        req.setDiscountType("manager");
                        req.setDiscountName(bo.getDiscountName());
                        req.setDiscountRate(bo.getDiscountRate());
                        req.setManagerPwd(CommonUtils.trim(pwd.getText()));
                        return req;
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newLine(label, optList),
                            newLine(pwdLabel, pwd));
                } else {
                    Logger.info("未知类型");
                }
            });

            Label discountTypeLabel = new Label("折扣方式:");

            RadioButton coupon = new RadioButton("卡券优惠");
            coupon.setToggleGroup(toggleGroup);
            coupon.setUserData(1);

            RadioButton manager = new RadioButton("店长折扣");
            manager.setToggleGroup(toggleGroup);
            manager.setUserData(2);
            manager.setSelected(true);

            box.getChildren().add(newLine(discountTypeLabel, coupon, manager));
        }
        {
            box.getChildren().add(discountContentLine);
        }
        {
            Button button = new Button("使用优惠");
            button.setOnMouseClicked(evt -> {
                if (discountHolder.get() != null) {
                    DiscountApplyReq req = discountHolder.get().get();
                    if (req == null) {
                        AlertBuilder.ERROR("请选择折扣信息");
                        return;
                    }
                    if ("manager".equals(req.getDiscountType())
                            && !storeService.checkManagerPwd(req.getManagerPwd())) {
                        AlertBuilder.ERROR("店长密码错误");
                        return;
                    }
                    if (req.getDiscountRate() <= 0.001 || req.getDiscountRate() >= 0.999) {
                        AlertBuilder.ERROR("折扣信息错误");
                        return;
                    }
                    Logger.info(JSON.toJSONString(req));
                    this.handleDiscount(param, req);
                }
            });
            box.getChildren().add(button);
        }
    }

    private HBox newLine(Node... nodes) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.setSpacing(10);
        for (Node n : nodes) {
            line.getChildren().add(n);
        }
        return line;
    }

    private ComboBox<DiscountTypeBO> getDiscountOptions() {
        ObservableList<DiscountTypeBO> list = FXCollections.observableArrayList(Lists.newArrayList(
                new DiscountTypeBO("员工折扣(7折)", 0.7),
                new DiscountTypeBO("朋友折扣(8.5折)", 0.85),
                new DiscountTypeBO("员工补单折扣(6折)", 0.6),
                new DiscountTypeBO("7.8折活动", 0.78),
                new DiscountTypeBO("8.8折活动", 0.88),
                new DiscountTypeBO("68元秒杀", 0.6),
                new DiscountTypeBO("5折活动", 0.5)
        ));
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
        // 加载orderDishes
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
        // 加载discount Checker
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        // 可以参加折扣的菜品
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
        AlertBuilder.INFO("优惠使用成功");
    }

}
