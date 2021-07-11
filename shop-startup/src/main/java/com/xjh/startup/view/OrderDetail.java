package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.TimeRecord;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.OrderService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OrderDetail extends VBox {
    final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    // 依赖服务
    OrderService orderService;
    DeskService deskService;
    OrderDishesService orderDishesService;
    DishesService dishesService;
    DishesPackageService dishesPackageService;
    OrderPayService orderPayService;


    ObjectProperty<OrderView> orderView = new SimpleObjectProperty<>();

    public OrderDetail(Desk desk) {
        TimeRecord cost = TimeRecord.start();
        deskService = GuiceContainer.getInstance(DeskService.class);
        orderService = GuiceContainer.getInstance(OrderService.class);
        orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
        dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
        dishesService = GuiceContainer.getInstance(DishesService.class);
        orderPayService = GuiceContainer.getInstance(OrderPayService.class);
        LogUtils.info("OrderDetail初始化服务耗时: " + cost.getCostAndReset());


        Integer orderId = desk.getOrderId();
        {
            int rowIndex = 0;
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            // 第一行
            String tableName = "桌号：" + desk.getDeskName();
            Label tableNameLabel = new Label(tableName);
            tableNameLabel.setMinWidth(800);
            tableNameLabel.setMinHeight(50);
            tableNameLabel.setFont(new Font(18));
            tableNameLabel.setAlignment(Pos.CENTER);
            gridPane.add(tableNameLabel, 0, rowIndex, 4, 1);
            // 关台按钮
            Button closeDeskBtn = new Button("关台");
            closeDeskBtn.setMinWidth(100);
            closeDeskBtn.setOnMouseClicked(evt -> doCloseDesk(desk));
            gridPane.add(closeDeskBtn, 4, rowIndex, 1, 2);
            this.getChildren().add(gridPane);

            // 第二行
            rowIndex++;
            Label labelCustNum = new Label("就餐人数: 0");
            orderView.addListener((x, ov, nv) -> labelCustNum.setText("就餐人数: " + nv.customerNum));
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 0, rowIndex);

            Label labelOrder = new Label("订单号: " + orderId);
            labelOrder.setMinWidth(200);
            gridPane.add(labelOrder, 1, rowIndex);

            Label labelOrderTime = new Label("就餐时间: ");
            orderView.addListener((x, ov, nv) -> {
                labelOrderTime.setText("就餐时间: " + nv.orderTime);
            });
            labelOrderTime.setMinWidth(200);
            gridPane.add(labelOrderTime, 2, rowIndex);

            Label labelPayStatus = new Label("支付状态: 未支付");
            orderView.addListener((x, ov, nv) -> {
                labelPayStatus.setText("支付状态: " + nv.payStatusName);
            });
            labelPayStatus.setMinWidth(200);
            gridPane.add(labelPayStatus, 3, rowIndex);
        }
        // 分割线
        this.getChildren().add(horizontalSeparator());
        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            /// ----------- 第一行 ---------------
            Label l = new Label("订单总额：0");
            orderView.addListener((a, b, c) -> l.setText("订单总额: " + CommonUtils.formatMoney(c.totalPrice)));
            l.setMinWidth(200);
            gridPane.add(l, 0, 0);
            // 第二行
            Label labelCustNum = new Label("已支付: ");
            orderView.addListener((x, ov, nv) -> labelCustNum.setText("已支付: " + CommonUtils.formatMoney(nv.orderHadpaid)));
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 1, 0);

            Label notPaid = new Label("还需支付: 0.00");
            orderView.addListener((a, b, c) -> {
                double totalPrice = c.totalPrice;
                double paid = c.orderHadpaid;
                String notPaidStr = CommonUtils.formatMoney(totalPrice - paid);
                notPaid.setText("还需支付: " + notPaidStr);
            });
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
        // 分割线
        this.getChildren().add(horizontalSeparator());

        TableView<OrderDishesTableItemVO> tv = new TableView<>();
        Runnable refreshTableView = () -> {
            TimeRecord start = TimeRecord.start();
            reloadOrder(orderId);
            tv.setItems(loadOrderDishes(orderId));
            tv.refresh();
        };
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

        }
        // 分割线
        this.getChildren().add(horizontalSeparator());
        // 支付消息
        {
            TextArea textArea = new TextArea();
            textArea.setPrefHeight(80);
            textArea.setEditable(false);
            orderView.addListener((x, ov, nv) -> textArea.setText(nv.payInfoRemark));
            this.getChildren().add(textArea);
        }
        // 分割线
        this.getChildren().add(horizontalSeparator());
        // 功能菜单
        {
            DeskOrderParam deskOrderParam = new DeskOrderParam();
            deskOrderParam.setDeskId(desk.getDeskId());
            deskOrderParam.setDeskName(desk.getDeskName());
            deskOrderParam.setOrderId(orderId);
            deskOrderParam.setCallback(refreshTableView);

            FlowPane pane = new FlowPane();
            pane.setHgap(20);
            pane.setPadding(new Insets(10, 0, 10, 20));
            // 按钮
            Button orderBtn = createButton("点菜");
            orderBtn.setOnMouseClicked(evt -> openDishesChoiceView(deskOrderParam));
            Button sendBtn = createButton("送菜");
            Button returnBtn = createButton("退菜");
            Button transferBtn = createButton("转台");
            Button splitBtn = createButton("拆台");
            Button payBillBtn = createButton("结账");
            payBillBtn.setOnMouseClicked(evt -> openPayWayChoiceView(deskOrderParam));
            // add all buttons
            pane.getChildren().addAll(orderBtn, sendBtn, returnBtn, transferBtn, splitBtn, payBillBtn);
            this.getChildren().add(pane);
        }
        LogUtils.info("OrderDetail构建页面耗时: " + cost.getCostAndReset());
        // 刷新页面
        refreshTableView.run();
        LogUtils.info("OrderDetail加载数据耗时: " + cost.getCostAndReset());
    }

    private Button createButton(String name) {
        Button btn = new Button(name);
        btn.setMinWidth(66);
        btn.setMinHeight(50);
        return btn;
    }

    private TableColumn<OrderDishesTableItemVO, Object> newCol(String name, String filed, double width) {
        TableColumn<OrderDishesTableItemVO, Object> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(col -> {
            TableCell<OrderDishesTableItemVO, Object> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, ov, nv) -> {
                if (nv == null) {
                    return;
                }
                if (nv instanceof RichText) {
                    RichText val = (RichText) nv;
                    cell.textProperty().set(CommonUtils.stringify(val.getText()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else if (nv instanceof Money) {
                    Money val = (Money) nv;
                    cell.textProperty().set(CommonUtils.formatMoney(val.getAmount()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else {
                    cell.textProperty().set(CommonUtils.stringify(nv));
                }
            });
            return cell;
        });
        return c;
    }

    private ObservableList<OrderDishesTableItemVO> loadOrderDishes(Integer orderId) {
        List<OrderDishes> orderDishes = orderDishesService.selectOrderDishes(orderId);
        List<Integer> dishesIdList = CommonUtils.map(orderDishes, OrderDishes::getDishesId);
        List<Dishes> dishesList = dishesService.getByIds(dishesIdList);
        Map<Integer, Dishes> dishesMap = CommonUtils.listToMap(dishesList, Dishes::getDishesId);
        List<OrderDishesTableItemVO> items = new ArrayList<>();
        orderDishes.forEach(o -> {
            String dishesName = "";
            String price = CommonUtils.formatMoney(o.getOrderDishesPrice());
            String discountPrice = CommonUtils.formatMoney(o.getOrderDishesDiscountPrice());
            String saleType = EnumOrderSaleType.of(o.getOrderDishesSaletype()).remark;
            // 套餐
            if (o.getIfDishesPackage() == 1) {
                DishesPackage pkg = dishesPackageService.getById(o.getDishesId());
                if (pkg != null) {
                    dishesName = "(套餐)" + pkg.getDishesPackageName();
                }
            } else {
                Dishes d = dishesMap.get(o.getDishesId());
                if (d != null) {
                    dishesName = d.getDishesName();
                }
            }
            items.add(new OrderDishesTableItemVO(
                    o.getOrderDishesId() + "",
                    o.getSubOrderId() + "",
                    dishesName,
                    price, discountPrice,
                    o.getOrderDishesNums() + "",
                    saleType));
        });
        return FXCollections.observableArrayList(items);

    }

    private void reloadOrder(Integer orderId) {
        Order o = orderService.getOrder(orderId);
        if (o != null) {
            OrderView v = new OrderView();
            v.customerNum = o.getOrderCustomerNums();
            v.orderTime = DateBuilder.base(o.getCreateTime()).format(DATETIME_PATTERN);
            v.orderHadpaid = o.getOrderHadpaid();
            v.totalPrice = sumTotalPrice(orderId);
            v.payStatusName = EnumOrderStatus.of(o.getOrderStatus()).remark;
            v.deduction = o.getFullReduceDishesPrice();
            // 支付信息
            List<OrderPay> pays = orderPayService.selectByOrderId(orderId);
            StringBuilder payInfo = new StringBuilder();
            CommonUtils.forEach(pays, p -> {
                payInfo.append(DateBuilder.base(p.getCreatetime()).format(DATETIME_PATTERN))
                        .append(" 收到付款:")
                        .append(CommonUtils.formatMoney(p.getAmount()))
                        .append(", 来自")
                        .append(EnumPayMethod.of(p.getPaymentMethod()).name);
                if (CommonUtils.isNotBlank(p.getCardNumber())) {
                    payInfo.append(",交易号:").append(p.getCardNumber());
                }
                payInfo.append("\r\n");
            });
            v.payInfoRemark = payInfo.toString();
            orderView.set(v);
        }
    }

    private double sumTotalPrice(Integer orderId) {
        double totalPrice = 0;
        List<OrderDishes> dishes = orderDishesService.selectOrderDishes(orderId);
        totalPrice = CommonUtils.map(dishes, OrderDishes::getOrderDishesPrice)
                .stream().reduce(0D, Double::sum);
        return totalPrice;
    }


    private void doCloseDesk(Desk desk) {
        double notPaidBillAmount = orderService.notPaidBillAmount(desk.getOrderId());
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
            Result<String> closeDeskRs = deskService.closeDesk(desk.getDeskId());
            if (!closeDeskRs.isSuccess()) {
                AlertBuilder.ERROR(closeDeskRs.getMsg());
                return;
            }
            this.getScene().getWindow().hide();
        }
    }

    private void openDishesChoiceView(DeskOrderParam param) {
        Stage orderDishesStg = new Stage();
        orderDishesStg.initOwner(this.getScene().getWindow());
        orderDishesStg.initModality(Modality.WINDOW_MODAL);
        orderDishesStg.initStyle(StageStyle.DECORATED);
        orderDishesStg.centerOnScreen();
        orderDishesStg.setWidth(this.getScene().getWindow().getWidth());
        orderDishesStg.setHeight(this.getScene().getWindow().getHeight());
        orderDishesStg.setTitle("点菜[桌号:" + param.getDeskName() + "]");
        orderDishesStg.setScene(new Scene(new OrderDishesChoiceView(param)));
        orderDishesStg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        orderDishesStg.showAndWait();
    }

    private void openPayWayChoiceView(DeskOrderParam param) {
        Stage orderDishesStg = new Stage();
        orderDishesStg.initOwner(this.getScene().getWindow());
        orderDishesStg.initModality(Modality.WINDOW_MODAL);
        orderDishesStg.initStyle(StageStyle.DECORATED);
        orderDishesStg.centerOnScreen();
        orderDishesStg.setWidth(this.getScene().getWindow().getWidth() / 3);
        orderDishesStg.setHeight(this.getScene().getWindow().getHeight() / 3 * 2);
        orderDishesStg.setTitle("结账[桌号:" + param.getDeskName() + "]");
        orderDishesStg.setScene(new Scene(new PayWayChoiceView(param)));
        orderDishesStg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        orderDishesStg.showAndWait();
    }

    private Separator horizontalSeparator() {
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.HORIZONTAL);
        return separator2;

    }

    public static class OrderView {
        int customerNum;
        String orderTime;
        String payStatusName;
        double totalPrice;
        double orderHadpaid;
        double deduction;
        String payInfoRemark;
    }

    @Override
    public void finalize() {
        System.out.println("OrderDetail ........ destroyed");
    }
}
