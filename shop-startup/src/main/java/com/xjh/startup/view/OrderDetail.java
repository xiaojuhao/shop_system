package com.xjh.startup.view;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class OrderDetail extends VBox {
    public OrderDetail(Desk orderDesk) {
        // 依赖服务
        OrderService orderService = GuiceContainer.getInstance(OrderService.class);
        DeskService deskService = GuiceContainer.getInstance(DeskService.class);
        OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

        Integer orderId = orderDesk.getOrderId();
        String orderTime = "";
        String payStatusName = "";
        Double paid = 0D;
        Order order = null;
        order = orderService.getOrder(orderId);
        if (order != null) {
            orderTime = DateBuilder.base(order.getCreateTime()).format("yyyy-MM-dd HH:mm:ss");
            payStatusName = EnumOrderStatus.of(order.getOrderStatus()).remark;
            paid = order.getOrderHadpaid();
        }


        double totalPrice = 0;
        List<OrderDishes> dishes = orderDishesService.selectOrderDishes(orderId);
        totalPrice = CommonUtils.map(dishes, OrderDishes::getOrderDishesPrice)
                .stream().filter(Objects::nonNull).reduce(0D, Double::sum);

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
                double notPaidBillAmount = orderService.notPaidBillAmount(orderId);
                if (notPaidBillAmount > 0) {
                    AlertBuilder.INFO("未支付完成，无法关台");
                    return;
                }
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

        TableView<OrderDishesTableItemVO> tv = new TableView<>();
        {
            tv.setMaxHeight(300);
            tv.setPadding(new Insets(5, 0, 0, 5));
            tv.getColumns().addAll(
                    newCol("序号", "col1", 100),
                    newCol("子订单", "col2", 100),
                    newCol("菜名名称", "col3", 300),
                    newCol("单价", "col4", 100),
                    newCol("折后价", "col5", 100),
                    newCol("数量", "col6", 100),
                    newCol("类型", "col7", 100)
            );
            this.getChildren().add(tv);
            tv.setItems(loadOrderDishes(orderId));
        }

        {
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.HORIZONTAL);
            this.getChildren().add(separator2);
        }
        // 功能菜单
        {
            FlowPane pane = new FlowPane();
            pane.setHgap(20);
            pane.setPadding(new Insets(10, 0, 10, 20));
            Button orderBtn = createButton("点菜");
            DeskOrderParam deskOrderParam = new DeskOrderParam();
            deskOrderParam.setDeskId(orderDesk.getDeskId());
            deskOrderParam.setDeskName(orderDesk.getDeskName());
            deskOrderParam.setOrderId(orderId);
            orderBtn.setOnMouseClicked(evt -> {
                Stage orderDishesStg = new Stage();
                orderDishesStg.initOwner(this.getScene().getWindow());
                orderDishesStg.initModality(Modality.WINDOW_MODAL);
                orderDishesStg.initStyle(StageStyle.DECORATED);
                orderDishesStg.centerOnScreen();
                orderDishesStg.setWidth(this.getScene().getWindow().getWidth());
                orderDishesStg.setHeight(this.getScene().getWindow().getHeight());
                orderDishesStg.setTitle("点菜[桌号:" + orderDesk.getDeskName() + "]");
                orderDishesStg.setScene(new Scene(new OrderDishesChoiceView(deskOrderParam)));
                orderDishesStg.setOnCloseRequest(e -> {
                    tv.setItems(loadOrderDishes(orderId));
                });
                orderDishesStg.showAndWait();
            });
            Button sendBtn = createButton("送菜");
            Button returnBtn = createButton("退菜");
            Button transferBtn = createButton("转台");
            Button splitBtn = createButton("拆台");
            Button payBillBtn = createButton("结账");
            pane.getChildren().addAll(orderBtn, sendBtn, returnBtn, transferBtn, splitBtn, payBillBtn);
            this.getChildren().add(pane);
        }

    }

    private Button createButton(String name) {
        Button btn = new Button(name);
        btn.setMinWidth(66);
        btn.setMinHeight(50);
        return btn;
    }

    private TableColumn newCol(String name, String filed, double width) {
        TableColumn<OrderDishesTableItemVO, SimpleStringProperty> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(new Callback<TableColumn<OrderDishesTableItemVO, SimpleStringProperty>, TableCell<OrderDishesTableItemVO, SimpleStringProperty>>() {
            public TableCell<OrderDishesTableItemVO, String> call(TableColumn param) {
                return new TableCell<OrderDishesTableItemVO, String>() {
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

    private ObservableList<OrderDishesTableItemVO> loadOrderDishes(Integer orderId) {
        // 依赖服务
        DishesPackageDAO dishesPackageDAO = GuiceContainer.getInstance(DishesPackageDAO.class);
        DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);
        OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);


        List<OrderDishes> orderDishes = orderDishesService.selectOrderDishes(orderId);
        List<OrderDishesTableItemVO> items = orderDishes.stream().map(o -> {
            String dishesName = "";
            String price = CommonUtils.formatMoney(o.getOrderDishesPrice());
            String discountPrice = CommonUtils.formatMoney(o.getOrderDishesDiscountPrice());
            String saleType = EnumOrderSaleType.of(o.getOrderDishesSaletype()).remark;
            // 套餐
            if (o.getIfDishesPackage() == 1) {
                DishesPackage pkg = dishesPackageDAO.getById(o.getDishesId());
                if (pkg != null) {
                    dishesName = pkg.getDishesPackageName();
                }
            } else {
                Dishes d = dishesDAO.getById(o.getDishesId());
                if (d != null) {
                    dishesName = d.getDishesName();
                }
            }
            return new OrderDishesTableItemVO(
                    o.getOrderDishesId() + "",
                    o.getSubOrderId() + "",
                    dishesName,
                    price, discountPrice,
                    o.getOrderDishesNums() + "",
                    saleType);
        }).collect(Collectors.toList());
        return FXCollections.observableArrayList(items);

    }
}
