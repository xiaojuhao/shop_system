package com.xjh.startup.view;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class OrderDetail extends VBox {
    public OrderDetail(Desk orderDesk) {
        OrderService orderService = GuiceContainer.getInstance(OrderService.class);
        DeskService deskService = GuiceContainer.getInstance(DeskService.class);
        String orderId = "";
        String orderTime = "";
        String payStatusName = "";
        Double paid = 0D;
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

                paid = order.getOrderHadpaid();
            }
        }

        OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
        double totalPrice = 0;
        if (CommonUtils.isNotBlank(orderId)) {
            List<OrderDishes> dishes = orderDishesService.selectOrderDishes(orderId);
            totalPrice = CommonUtils.collect(dishes, OrderDishes::getOrderDishesPrice)
                    .stream().filter(Objects::nonNull).reduce(0D, Double::sum);
        }

        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
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
            button.setOnMouseClicked(evt -> {
                Alert _alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "您确定要关台吗？",
                        new ButtonType("取消", ButtonBar.ButtonData.NO),
                        new ButtonType("确定", ButtonBar.ButtonData.YES));
                _alert.setTitle("关台操作");
                _alert.setHeaderText("当前订单已结清");
                Optional<ButtonType> _buttonType = _alert.showAndWait();
                if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    deskService.closeDesk(orderDesk.getDeskId());
                    this.getScene().getWindow().hide();
                }
            });
            gridPane.add(button, 4, 0, 1, 2);

            this.getChildren().add(gridPane);
        }
        {
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.HORIZONTAL);
            // separator2.setPadding(new Insets(10));
            this.getChildren().add(separator2);
        }
        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            /// ----------- 第一行 ---------------
            Label l = new Label("订单总额：" + totalPrice);
            l.setMinWidth(200);
            gridPane.add(l, 0, 0);
            // 第二行
            Label labelCustNum = new Label("已支付: " + paid);
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 1, 0);

            Label notPaid = new Label("还需支付: " + (totalPrice - paid));
            notPaid.setMinWidth(200);
            notPaid.setStyle("-fx-font-size: 16; -fx-text-fill: red");
            gridPane.add(notPaid, 2, 0);

            Label labelOrderTime = new Label("当前折扣: 无");
            labelOrderTime.setMinWidth(200);
            gridPane.add(labelOrderTime, 3, 0);

            Label labelPayStatus = new Label("优惠金额: 0");
            labelPayStatus.setMinWidth(200);
            gridPane.add(labelPayStatus, 4, 0);

            /// ----------- 第二行 ---------------
            Label reduction = new Label("抹零金额: 0");
            reduction.setMinWidth(200);
            gridPane.add(reduction, 0, 1);

            Label mangerReduction = new Label("店长减免: 0");
            mangerReduction.setMinWidth(200);
            gridPane.add(mangerReduction, 1, 1);

            Label discount = new Label("折扣金额: 0");
            discount.setMinWidth(200);
            gridPane.add(discount, 2, 1);

            Label refund = new Label("退菜金额: 0");
            refund.setMinWidth(200);
            gridPane.add(refund, 3, 1);

            Label fan = new Label("反结账金额: 0");
            fan.setMinWidth(200);
            gridPane.add(fan, 4, 1);

            this.getChildren().add(gridPane);
        }
        {
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.HORIZONTAL);
            // separator2.setPadding(new Insets(10));
            this.getChildren().add(separator2);
        }

        {
            TableView<TableItem> tv = new TableView<>();
            tv.setMaxHeight(300);
            tv.setPadding(new Insets(5, 0, 0, 5));
            tv.getColumns().addAll(
                    newCol("列1", "col1", 200),
                    newCol("列2", "col2", 200),
                    newCol("列3", "col3", 300),
                    newCol("列4", "col4", 100),
                    newCol("列5", "col5", 100),
                    newCol("列6", "col6", 200)
            );
            this.getChildren().add(tv);
            tv.setItems(mockData());
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

    private TableColumn newCol(String name, String filed, double width) {
        TableColumn<TableItem, SimpleStringProperty> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(new Callback<TableColumn<TableItem, SimpleStringProperty>, TableCell<TableItem, SimpleStringProperty>>() {
            public TableCell<TableItem, String> call(TableColumn param) {
                return new TableCell<TableItem, String>() {
                    public void updateItem(String item, boolean empty) {
                        if (CommonUtils.isNotBlank(item)) {
                            if (item.contains("@")) {
                                setTextFill(Color.RED);
                            } else {
                                setAlignment(Pos.CENTER_RIGHT);
                            }
                            setText(item);
                        }
                    }
                };
            }
        });
        return c;
    }

    private ObservableList<TableItem> mockData() {
        return FXCollections.observableArrayList(
                new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                new TableItem("Jacob", "Smith", "jacob.smith@example.com", "1111"),
                new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                new TableItem("", "", "", ""),
                new TableItem("Isabella", "Johnson", "isabella.johnson@example.com", "1111"),
                new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                new TableItem("Ethan", "Williams", "ethan.williams@example.com", "1111"),
                new TableItem("", "", "ethan.williams@example.com", ""),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("", "", "emma.jones@example.com", ""),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Emma", "Jones", "emma.jones@example.com", "1111"),
                new TableItem("Michael", "Brown", "michael.brown@example.com", "1111")
        );

    }
}
