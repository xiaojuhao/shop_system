package com.xjh.startup.view;

import com.google.common.collect.Lists;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

public class OrderReturnDishesView extends SmallForm {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    public OrderReturnDishesView(DeskOrderParam param) {
        // 标题
        Label title = new Label("退菜[桌号:" + param.getDeskName() + "]");
        title.setFont(Font.font(16));
        title.setTextFill(Color.RED);
        addLine(title);
        // 退菜原因
        ComboBox<String> reasonList = buildReasonCombo();
        reasonList.setPrefWidth(200);
        addLine(newLine(new Label("退菜原因: "), reasonList));
        // 退菜按钮
        Button returnBtn = new Button("退菜");
        returnBtn.setOnMouseClicked(evt -> {
            String r = reasonList.getSelectionModel().getSelectedItem();
            doReturnDishes(param.getReturnList(), r);
            this.getScene().getWindow().hide();
        });
        addLine(returnBtn);
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

    private ComboBox<String> buildReasonCombo(){
        ObservableList<String> options = FXCollections.observableArrayList(
                Lists.newArrayList("客人不要了", "多点或下错单", "菜品缺货", "菜品质量问题", "上菜太慢",  "其它")
        );
        return new ComboBox<>(options);
    }
}
