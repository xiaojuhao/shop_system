package com.xjh.startup.view;

import java.util.function.Supplier;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DiscountTypeBO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        VBox discountContentLine = new VBox();
        discountContentLine.setSpacing(10);
        Holder<Supplier<DiscountTypeBO>> discountHolder = new Holder<>();
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
                    discountHolder.hold(null);

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newLine(voucherLabel, voucher),
                            newLine(cardLabel, card));
                } else if (select == 2) {
                    Logger.info("店长折扣");
                    ComboBox<DiscountTypeBO> optList = getDiscountOptions();
                    optList.setPrefWidth(160);
                    Label label = new Label("折扣类型:");
                    discountHolder.hold(() -> {
                        return optList.getSelectionModel().getSelectedItem();
                    });

                    Label pwdLabel = new Label("确认密码:");
                    PasswordField pwd = new PasswordField();
                    pwd.setPrefWidth(160);


                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newLine(label, optList),
                            newLine(pwdLabel, pwd));
                } else {
                    Logger.info("未知类型");
                }
            });

            Label discountTypeLabel = new Label("折扣方式:");
            HBox.setMargin(discountTypeLabel, new Insets(0, 20, 0, 0));

            RadioButton coupon = new RadioButton("卡券优惠");
            coupon.setToggleGroup(toggleGroup);
            coupon.setUserData(1);

            RadioButton manager = new RadioButton("店长折扣");
            manager.setToggleGroup(toggleGroup);
            manager.setUserData(2);
            manager.setSelected(true);

            HBox typeSelectionLine = new HBox();
            typeSelectionLine.setAlignment(Pos.CENTER);
            typeSelectionLine.getChildren().addAll(discountTypeLabel, coupon, manager);
            box.getChildren().add(typeSelectionLine);
        }
        {
            box.getChildren().add(discountContentLine);
        }
        {
            Button button = new Button("使用优惠");
            button.setOnMouseClicked(evt -> {
                if (discountHolder.get() != null) {
                    DiscountTypeBO bo = discountHolder.get().get();
                    if (bo != null) {
                        Logger.info(JSON.toJSONString(bo));
                    }
                }
            });
            box.getChildren().add(button);
        }
    }

    private HBox newLine(Node title, Node node) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.getChildren().addAll(title, node);
        return line;
    }

    private ComboBox<DiscountTypeBO> getDiscountOptions() {
        ObservableList<DiscountTypeBO> list = FXCollections.observableArrayList(Lists.newArrayList(
                new DiscountTypeBO("d01", "员工折扣(7折)", 0.7),
                new DiscountTypeBO("d02", "朋友折扣(8.5折)", 0.85),
                new DiscountTypeBO("d03", "员工补单折扣(6折)", 0.6),
                new DiscountTypeBO("d04", "7.8折活动", 0.78),
                new DiscountTypeBO("d05", "8.8折活动", 0.88),
                new DiscountTypeBO("d06", "68元秒杀", 0.6),
                new DiscountTypeBO("d07", "5折活动", 0.5)
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

}
