package com.xjh.startup.view;

import com.google.common.collect.Lists;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.startup.view.model.DeskOrderParam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

public class OrderReturnDishesView extends VBox {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    public OrderReturnDishesView(DeskOrderParam param) {
        VBox box = this;
        List<String> returnList = param.getReturnList();
        box.setAlignment(Pos.CENTER);
        // 标题
        Label title = new Label("退菜[桌号:" + param.getDeskName() + "]");
        title.setFont(Font.font(16));
        title.setTextFill(Color.RED);
        title.setPadding(new Insets(0, 0, 20, 0));
        box.getChildren().add(title);
        // 退菜原因
        ObservableList<String> options = FXCollections.observableArrayList(
                Lists.newArrayList("客人不要了", "多点或下错单", "菜品缺货", "菜品质量问题", "上菜太慢", "测试", "其它")
        );
        Label reason = new Label("退菜原因: ");
        ComboBox<String> reasonList = new ComboBox<>(options);
        reasonList.setPrefWidth(200);
        HBox reasonLine = new HBox();
        reasonLine.setPrefWidth(300);
        reasonLine.setMaxWidth(300);
        reasonLine.getChildren().addAll(reason, reasonList);
        reasonLine.setPadding(new Insets(0, 0, 20, 0));
        box.getChildren().add(reasonLine);
        // 退菜按钮
        Button returnBtn = new Button("退菜");
        returnBtn.setOnMouseClicked(evt -> {
            String r = reasonList.getSelectionModel().getSelectedItem();
            doReturnDishes(returnList, r);
            this.getScene().getWindow().hide();
        });
        box.getChildren().add(returnBtn);
    }

    private void doReturnDishes(List<String> orderDishesIds, String reason) {
        Logger.info("退菜:" + orderDishesIds + ", " + reason);
        List<Integer> ids = orderDishesIds.stream()
                .map(id -> CommonUtils.parseInt(id, null))
                .collect(Collectors.toList());
        List<OrderDishes> list = orderDishesService.selectByIdList(ids);
        for (OrderDishes d : list) {
            int i = orderDishesService.returnOrderDishes(d);
            System.out.println("退菜结果: " + i);
        }
    }
}
