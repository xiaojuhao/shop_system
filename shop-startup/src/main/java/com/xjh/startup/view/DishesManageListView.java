package com.xjh.startup.view;


import com.xjh.common.utils.TableViewUtils;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.OrderDishesTableItemBO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class DishesManageListView extends SimpleForm implements Initializable {
    ObjectProperty<Condition> cond = new SimpleObjectProperty<>();

    @Override
    public void initialize() {
        buildCond();
        buildContent();
        buildFoot();
    }

    private void buildCond() {
        // name
        HBox nameLine = new HBox();
        Label nameLabel = new Label("名称:");
        TextField nameInput = new TextField();
        nameLine.getChildren().add(nameLabel);
        nameLine.getChildren().add(nameInput);


        // status
        HBox statusLine = new HBox();
        Label statusLabel = new Label("状态:");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton all = new RadioButton("全部");
        all.setToggleGroup(toggleGroup);
        all.setUserData(-1);

        RadioButton online = new RadioButton("上架");
        online.setToggleGroup(toggleGroup);
        online.setUserData(1);
        online.setSelected(true);

        RadioButton offline = new RadioButton("下架");
        offline.setToggleGroup(toggleGroup);
        offline.setUserData(0);

        statusLine.getChildren().add(statusLabel);
        statusLine.getChildren().add(newLine(online, offline));


        addLine(newLine(nameLine, statusLine));
    }

    private void buildContent() {
        TableView<OrderDishesTableItemBO> tableView = new TableView<>();
        tableView.getColumns().addAll(
                TableViewUtils.newCol("序号", "orderDishesId", 100),
                TableViewUtils.newCol("子订单", "subOrderId", 100),
                TableViewUtils.newCol("菜名名称", "dishesName", 300),
                TableViewUtils.newCol("单价", "price", 160),
                TableViewUtils.newCol("折后价", "discountPrice", 100),
                TableViewUtils.newCol("数量", "orderDishesNum", 100),
                TableViewUtils.newCol("类型", "saleType", 100)
        );
        addLine(tableView);
    }

    private void buildFoot() {
        Button prev = new Button("上一页");
        Button next = new Button("下一页");
        addLine(newLine(prev, next));
    }

    public static class Condition {
        int pageNo;
        int pageSize;
        String name;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
