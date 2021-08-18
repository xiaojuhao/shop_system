package com.xjh.startup.view;

import com.google.common.collect.Lists;
import com.xjh.common.utils.Logger;
import com.xjh.startup.view.model.DeskOrderParam;

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

public class OrderDiscountSelectionView extends VBox {
    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        VBox discountContentLine = new VBox();
        discountContentLine.setSpacing(10);
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

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(
                            newLine(voucherLabel, voucher),
                            newLine(cardLabel, card));
                } else if (select == 2) {
                    Logger.info("店长折扣");
                    ComboBox<String> optList = new ComboBox<>(getDiscountOptions());
                    optList.setPrefWidth(160);
                    Label label = new Label("折扣类型:");

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
            VBox.setMargin(discountContentLine, new Insets(20, 0, 0, 0));
            box.getChildren().add(discountContentLine);
        }
        {
            Button button = new Button("使用优惠");
            VBox.setMargin(button, new Insets(20, 0, 0, 0));
            box.getChildren().add(button);
        }
    }

    private HBox newLine(Node title, Node node) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.getChildren().addAll(title, node);
        return line;
    }

    private ObservableList<String> getDiscountOptions() {
        return FXCollections.observableArrayList(
                Lists.newArrayList(
                        "员工折扣(7折)", "朋友折扣(8.5折)", "员工补单折扣(6折)",
                        "7.8折活动", "8.8折活动", "68元秒杀", "其它")
        );
    }

}
