package com.xjh.startup.view;

import com.google.common.collect.Lists;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.utils.*;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.ReturnReasonDO;
import com.xjh.dao.mapper.ReturnReasonDAO;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.ws.NotifyService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.MediumForm;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderSeparateView extends MediumForm {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public OrderSeparateView(DeskOrderParam param) {
        List<Desk> allDesks = deskService.getAllDesks();
        List<Item> desks = allDesks.stream() //
                .filter(DeskService.statusFilter(EnumDeskStatus.IN_USE)) // 正在使用中的
                .filter(desk -> !Objects.equals(desk.getOrderId(), param.getOrderId())) // 过滤订单当前的桌子
                .map(Item::new) // 转换成Item对象
                .collect(Collectors.toList());
        TableView<Item> tableView = new TableView<>();
        tableView.setPadding(new Insets(5, 0, 0, 5));
        tableView.getColumns().addAll(
                TableViewUtils.newCol("餐桌", "deskName", 200),
                TableViewUtils.newCol("状态", "status", -1)
        );
        tableView.setItems(FXCollections.observableArrayList(desks));
        addLine(new Label());
        addLine(new Label("请选择拆台目标"));
        addLine(tableView);
        Button cancel = new Button("取消");
        cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());

        Button ok = new Button("确定拆台");
        ok.setOnMouseClicked(evt -> {
            ObservableList<Item> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected.isEmpty()) {
                AlertBuilder.INFO("请选择拆台目标餐桌");
                return;
            }
            Item item = selected.get(0);
            Result<String> changeRs = orderService.separateOrder(param.getSeparateSubOrderIdList(), item.getDeskId());
            if (!changeRs.isSuccess()) {
                AlertBuilder.ERROR(changeRs.getMsg());
                return;
            }
            AlertBuilder.INFO("拆台成功");
            this.getScene().getWindow().hide();
        });
        addLine(newCenterLine(cancel, ok));
    }

    public static class Item {
        public Item(Desk desk) {
            this.deskId = desk.getDeskId();
            this.deskName = desk.getDeskName();
            EnumDeskStatus status = EnumDeskStatus.of(desk.getStatus());
            this.status = RichText.create(status.remark());
            if (status != EnumDeskStatus.FREE) {
                this.status.with(Color.RED);
            }
        }

        Integer deskId;
        String deskName;
        RichText status;

        public Integer getDeskId() {
            return deskId;
        }

        public void setDeskId(Integer deskId) {
            this.deskId = deskId;
        }

        public String getDeskName() {
            return deskName;
        }

        public void setDeskName(String deskName) {
            this.deskName = deskName;
        }

        public RichText getStatus() {
            return status;
        }

        public void setStatus(RichText status) {
            this.status = status;
        }
    }
}
