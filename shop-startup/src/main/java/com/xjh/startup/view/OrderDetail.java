package com.xjh.startup.view;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OrderDetail extends VBox {
    public OrderDetail(Desk orderDesk) {
        OrderService orderService = GuiceContainer.getInstance(OrderService.class);
        String orderId = "";
        String orderTime = "";
        String payStatusName = "";
        Order order = null;
        if (CommonUtils.isNotBlank(orderDesk.getOrderId())) {
            orderId = orderDesk.getOrderId();
            order = orderService.getOrder(orderId);
            if (order != null) {
                orderTime = DateBuilder.base(order.getCreateTime()).format("yyyy-MM-dd HH:mm:ss");
                Integer orderStatus = order.getOrderStatus();
                if (orderStatus == null) {
                    payStatusName = "无";
                } else if (orderStatus == 1) {
                    payStatusName = "未支付";
                } else if (orderStatus == 2) {
                    payStatusName = "已付款";
                } else if (orderStatus == 3) {
                    payStatusName = "部分支付";
                } else if (orderStatus == 4) {
                    payStatusName = "合并餐桌";
                } else if (orderStatus == 5) {
                    payStatusName = "逃单";
                } else if (orderStatus == 6) {
                    payStatusName = "免单";
                }
            }
        }
        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            // gridPane.setStyle("-fx-border-width: 1 1 1 1;-fx-border-style: solid;-fx-border-color: red");
            Label l = new Label("桌号：" + orderDesk.getDeskName());
            l.setMinWidth(800);
            l.setMinHeight(50);
            l.setFont(new Font(18));
            l.setAlignment(Pos.CENTER);
            gridPane.add(l, 0, 0, 4, 1);
            // 第二行
            int customerNum = 0;
            if (order != null) {
                customerNum = order.getOrderCustomerNums();
            }
            Label labelCustNum = new Label("就餐人数: " + customerNum);
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 0, 1);

            Label labelOrder = new Label("订单号: " + orderId);
            labelOrder.setMinWidth(200);
            gridPane.add(labelOrder, 1, 1);

            Label labelOrderTime = new Label("就餐时间: " + orderTime);
            labelOrderTime.setMinWidth(200);
            gridPane.add(labelOrderTime, 2, 1);

            Label labelPayStatus = new Label("支付状态: " + payStatusName);
            labelPayStatus.setMinWidth(200);
            gridPane.add(labelPayStatus, 3, 1);

            // 关台按钮
            Button button = new Button("关台");
            button.setMinWidth(100);
            gridPane.add(button, 4, 0, 1, 2);

            this.getChildren().add(gridPane);
        }
        {
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.HORIZONTAL);
            this.getChildren().add(separator2);
        }
        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            // gridPane.setStyle("-fx-border-width: 1 1 1 1;-fx-border-style: solid;-fx-border-color: red");
            Label l = new Label("桌号：" + orderDesk.getDeskName());
            l.setMinWidth(800);
            l.setMinHeight(50);
            l.setFont(new Font(18));
            l.setAlignment(Pos.CENTER);
            gridPane.add(l, 0, 0, 4, 1);
            // 第二行
            Label labelCustNum = new Label("就餐人数: 2");
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 0, 1);

            Label labelOrder = new Label("订单号: 8888888");
            labelOrder.setMinWidth(200);
            gridPane.add(labelOrder, 1, 1);

            Label labelOrderTime = new Label("就餐时间: 2021-04-25 20:01");
            labelOrderTime.setMinWidth(200);
            gridPane.add(labelOrderTime, 2, 1);

            Label labelPayStatus = new Label("支付状态: 待支付");
            labelPayStatus.setMinWidth(200);
            gridPane.add(labelPayStatus, 3, 1);

            // 关台按钮
            Button button = new Button("关台");
            button.setMinWidth(100);
            gridPane.add(button, 4, 0, 1, 2);

            this.getChildren().add(gridPane);
        }
        {
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.HORIZONTAL);
            this.getChildren().add(separator2);
        }

        {
            TableView<TableItem> tv = new TableView<>();
            tv.setMaxHeight(300);
            TableColumn<TableItem, SimpleStringProperty> col1 = new TableColumn<>("列1");
            col1.setMinWidth(100);
            col1.setCellValueFactory(new PropertyValueFactory<>("col1"));

            TableColumn<TableItem, SimpleStringProperty> col2 = new TableColumn<>("列2");
            col2.setMinWidth(200);
            col2.setCellValueFactory(new PropertyValueFactory<>("col2"));

            TableColumn<TableItem, SimpleStringProperty> col3 = new TableColumn<>("列3");
            col3.setMinWidth(300);
            col3.setCellValueFactory(new PropertyValueFactory<>("col3"));

            TableColumn<TableItem, SimpleStringProperty> col4 = new TableColumn<>("列4");
            col4.setMinWidth(100);
            col4.setCellValueFactory(new PropertyValueFactory<>("col4"));

            tv.getColumns().addAll(col1, col2, col3, col4);
            this.getChildren().add(tv);

            ObservableList<TableItem> data =
                    FXCollections.observableArrayList(
                            new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                            new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                            new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                            new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                            new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                            new TableItem("Michael", "Brown", "michael.brown@example.com", "1111")
                    );
            tv.setItems(data);
        }
    }

    public static class TableItem {
        SimpleStringProperty col1;
        SimpleStringProperty col2;
        SimpleStringProperty col3;
        SimpleStringProperty col4;

        public TableItem(String col1, String col2, String col3, String col4) {
            this.col1 = new SimpleStringProperty(col1);
            this.col2 = new SimpleStringProperty(col2);
            this.col3 = new SimpleStringProperty(col3);
            this.col4 = new SimpleStringProperty(col4);
        }

        public String getCol1() {
            return col1.get();
        }

        public SimpleStringProperty col1Property() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1.set(col1);
        }

        public String getCol2() {
            return col2.get();
        }

        public SimpleStringProperty col2Property() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2.set(col2);
        }

        public String getCol3() {
            return col3.get();
        }

        public SimpleStringProperty col3Property() {
            return col3;
        }

        public void setCol3(String col3) {
            this.col3.set(col3);
        }

        public String getCol4() {
            return col4.get();
        }

        public SimpleStringProperty col4Property() {
            return col4;
        }

        public void setCol4(String col4) {
            this.col4.set(col4);
        }
    }
}
