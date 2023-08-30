package com.xjh.startup.view;

import com.google.common.collect.Lists;
import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.ReturnReasonDO;
import com.xjh.dao.mapper.ReturnReasonDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.ws.NotifyService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SmallForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderReturnDishesView extends SmallForm {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    ReturnReasonDAO returnReasonDAO = GuiceContainer.getInstance(ReturnReasonDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderReturnDishesView(DeskOrderParam param) {
        // 标题
        Label title = new Label("退菜[桌号:" + param.getDeskName() + "]");
        title.setFont(Font.font(16));
        title.setTextFill(Color.RED);
        addLine(title);
        // 退菜原因
        ComboBox<String> reasonList = buildReasonCombo();
        reasonList.setPrefWidth(200);
        addLine(newCenterLine(new Label("退菜原因: "), reasonList));
        // 退菜按钮
        Button returnBtn = new Button("退菜");
        returnBtn.setOnMouseClicked(evt -> {
            String r = reasonList.getSelectionModel().getSelectedItem();
            if(CommonUtils.isBlank(r)){
                AlertBuilder.ERROR("请选择退菜原因");
                return;
            }
            doReturnDishes(param, r);
            this.getScene().getWindow().hide();
        });
        addLine(returnBtn);
    }

    private void doReturnDishes(DeskOrderParam req, String reason) {
        List<Integer> ids = req.getReturnList().stream()
                .map(id -> CommonUtils.parseInt(id, null))
                .collect(Collectors.toList());
        // 查询选中的待退菜品信息
        List<OrderDishes> list = orderDishesService.selectByIdList(ids);
        // 汇总待退菜品的金额
        BigDecimal returnAmt = BigDecimal.ZERO;
        for(OrderDishes d : list){
            returnAmt = returnAmt.add(BigDecimal.valueOf(d.sumOrderDishesDiscountPrice()));
        }
        double notPaid = orderService.notPaidBillAmount(req.getOrderId());
        if(returnAmt.doubleValue() > notPaid){
            AlertBuilder.ERROR("退菜金额不能大于未支付金额");
            return;
        }
        for (OrderDishes d : list) {
            int i = orderDishesService.returnOrderDishes(d);
            Dishes dishes = dishesService.getById(d.getDishesId());
            // 退菜原因记录
            ReturnReasonDO returnReason = new ReturnReasonDO();
            returnReason.setOrderId(d.getOrderId());
            returnReason.setDeskName(req.getDeskName());
            returnReason.setDishesName(dishes.getDishesName());
            returnReason.setReturnReason(reason);
            returnReason.setAddtime(DateBuilder.now().mills());
            returnReasonDAO.insert(returnReason);

            NotifyService.notifyReturnDishes(req.getDeskId());
        }
    }

    private ComboBox<String> buildReasonCombo(){
        ObservableList<String> options = FXCollections.observableArrayList(
                Lists.newArrayList("客人不要了", "多点或下错单", "菜品缺货", "菜品质量问题", "上菜太慢",  "其它")
        );
        ComboBox<String> cb = new ComboBox<>(options);
        cb.getSelectionModel().select(0); // 默认选第一个
        return cb;
    }
}
