package com.xjh.startup.view;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.TableViewUtils;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.MediumForm;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class DeskChangeView extends MediumForm {
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    public DeskChangeView(DeskOrderParam param) {
        List<Item> desks = deskService.getAllDesks().stream().map(Item::new).collect(Collectors.toList());
        TableView<Item> tableView = new TableView<>();
        tableView.setPadding(new Insets(5, 0, 0, 5));
        tableView.getColumns().addAll(
                TableViewUtils.newCol("餐桌", "deskName", 200),
                TableViewUtils.newCol("状态", "status", -1)
        );
        tableView.setItems(FXCollections.observableArrayList(desks));
        addLine(new Label());
        addLine(new Label("请选择转台目标"));
        addLine(tableView);
        Button cancel = new Button("取消");
        cancel.setOnMouseClicked(evt -> this.getScene().getWindow().hide());

        Button ok = new Button("确定转台");
        ok.setOnMouseClicked(evt -> {
            ObservableList<Item> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected.isEmpty()) {
                AlertBuilder.INFO("请选择转台餐桌");
                return;
            }
            Item item = selected.get(0);
            Result<String> changeRs = orderService.changeDesk(param.getOrderId(), item.getDeskId());
            if (!changeRs.isSuccess()) {
                AlertBuilder.ERROR(changeRs.getMsg());
                return;
            }
            AlertBuilder.INFO("转台成功");
            this.getScene().getWindow().hide();
        });
        addLine(newLine(cancel, ok));
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
