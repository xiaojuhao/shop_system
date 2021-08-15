package com.xjh.startup.view;

import com.xjh.common.utils.Logger;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderDiscountSelectionView extends VBox {
    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox box = this;
        box.setAlignment(Pos.CENTER);
        // 折扣方式选择
        {
            Label discountTypeLabel = new Label("折扣方式:");
            HBox.setMargin(discountTypeLabel, new Insets(0, 20, 0, 0));

            ToggleGroup group = new ToggleGroup();
            group.selectedToggleProperty().addListener((x, o, n) -> {
                int select = (int) n.getUserData();
                if (select == 1) {
                    Logger.info("优惠券");
                } else if (select == 2) {
                    Logger.info("店长折扣");
                } else {
                    Logger.info("未知类型");
                }
            });

            RadioButton coupon = new RadioButton("选择优惠");
            coupon.setToggleGroup(group);
            coupon.setUserData(1);

            RadioButton manager = new RadioButton("店长折扣");
            manager.setToggleGroup(group);
            manager.setUserData(2);
            manager.setSelected(true);

            HBox discountTypeSelection = new HBox();
            discountTypeSelection.setAlignment(Pos.CENTER);
            discountTypeSelection.getChildren().addAll(discountTypeLabel, coupon, manager);
            box.getChildren().addAll(discountTypeSelection);
        }
    }
}
