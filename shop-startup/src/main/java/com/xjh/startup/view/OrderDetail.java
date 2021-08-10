package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.xjh.common.enumeration.EnumChoiceAction;
import com.xjh.common.enumeration.EnumDesKStatus;
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
import com.xjh.startup.view.model.OrderDishesTableItemVO;
import com.xjh.startup.view.model.OrderViewBO;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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


    ObjectProperty<OrderViewBO> orderView = new SimpleObjectProperty<>();

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
            orderView.addListener((x, ov, nv) -> labelOrderTime.setText("就餐时间: " + nv.orderTime));
            labelOrderTime.setMinWidth(200);
            gridPane.add(labelOrderTime, 2, rowIndex);

            Label labelPayStatus = new Label("支付状态: 未支付");
            orderView.addListener((x, ov, nv) -> labelPayStatus.setText("支付状态: " + nv.payStatusName));
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
            Label l = new Label("订单总额：0.00");
            orderView.addListener((a, b, c) -> l.setText("订单总额: " + CommonUtils.formatMoney(c.totalPrice)));
            l.setMinWidth(200);
            gridPane.add(l, 0, 0);
            // 第二行
            Label labelCustNum = new Label("已支付: 0.00");
            orderView.addListener((x, ov, nv) -> labelCustNum.setText("已支付: " + CommonUtils.formatMoney(nv.orderHadpaid)));
            labelCustNum.setMinWidth(200);
            gridPane.add(labelCustNum, 1, 0);

            Label notPaid = new Label("还需支付: 0.00");
            orderView.addListener((a, b, c) -> {
                double totalPrice = c.totalPrice;
                double paid = c.orderHadpaid;
                double returnAmt = c.returnAmount;
                String notPaidStr = CommonUtils.formatMoney(totalPrice - paid - returnAmt);
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
            orderView.addListener((a, b, c) -> {
                String returnAmtStr = CommonUtils.formatMoney(c.returnAmount);
                refund.setText("退菜金额: " + returnAmtStr);
            });
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
            reloadOrder(orderId);
            tv.setItems(loadOrderDishes(orderId));
            tv.refresh();
        };
        {
            tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tv.setMinHeight(300);
            tv.setPadding(new Insets(5, 0, 0, 5));
            tv.getColumns().addAll(
                    newCol("序号", "col1", 100),
                    newCol("子订单", "col2", 100),
                    newCol("菜名名称", "col3", 300),
                    newCol("单价", "col4", 130),
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
            sendBtn.setOnMouseClicked(evt -> openSendDishesChoiceView(deskOrderParam));
            Button returnBtn = createButton("退菜");
            returnBtn.setOnMouseClicked(evt -> returnDishesConfirm(deskOrderParam, tv));
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
        List<OrderDishes> orderDishes = orderDishesService.selectByOrderId(orderId);
        List<Integer> dishesIdList = CommonUtils.collect(orderDishes, OrderDishes::getDishesId);
        List<Dishes> dishesList = dishesService.getByIds(dishesIdList);
        Map<Integer, Dishes> dishesMap = CommonUtils.listToMap(dishesList, Dishes::getDishesId);
        List<OrderDishesTableItemVO> items = new ArrayList<>();
        List<OrderDishes> discountableList = new ArrayList<>();
        List<OrderDishes> nonDiscountableList = new ArrayList<>();
        orderDishes.sort(Comparator.comparing(OrderDishes::getCreatetime));
        orderDishes.forEach(o -> {
            if (o.getIfDishesPackage() == 1) {
                discountableList.add(o);
            } else {
                nonDiscountableList.add(o);
            }
        });
        discountableList.forEach(o -> {
            String dishesName = "";
            String price = CommonUtils.formatMoney(o.getOrderDishesPrice());
            String discountPrice = CommonUtils.formatMoney(o.getOrderDishesDiscountPrice());
            EnumOrderSaleType saleType = EnumOrderSaleType.of(o.getOrderDishesSaletype());
            RichText saleTypeText = new RichText(saleType.remark).with(Color.BLACK);
            if (saleType == EnumOrderSaleType.RETURN) {
                saleTypeText.with(Color.GRAY);
            }
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
                    new RichText(dishesName),
                    new RichText(price),
                    discountPrice,
                    o.getOrderDishesNums() + "",
                    saleTypeText));
        });
        if (CommonUtils.isNotEmpty(discountableList)) {
            double discountTotalPrice = discountableList.stream()
                    .map(OrderDishes::getOrderDishesPrice)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    new RichText(""),
                    new RichText("优惠合计:" + discountTotalPrice).with(Color.RED).with(Pos.CENTER_RIGHT),
                    "",
                    "",
                    new RichText("")));
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    new RichText("以下为不参与优惠活动菜品").with(Color.RED).with(Pos.CENTER_RIGHT),
                    new RichText(""),
                    "",
                    "",
                    new RichText("")));
        }
        nonDiscountableList.forEach(o -> {
            String dishesName = "";
            String price = CommonUtils.formatMoney(o.getOrderDishesPrice());
            String discountPrice = CommonUtils.formatMoney(o.getOrderDishesDiscountPrice());
            EnumOrderSaleType saleType = EnumOrderSaleType.of(o.getOrderDishesSaletype());
            RichText saleTypeText = RichText.create(saleType.remark).with(Color.BLACK);
            if (saleType == EnumOrderSaleType.RETURN) {
                saleTypeText.with(Color.GRAY);
            }

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
                    new RichText(dishesName).with(Color.RED),
                    new RichText(price),
                    discountPrice,
                    o.getOrderDishesNums() + "",
                    saleTypeText));
        });
        if (CommonUtils.isNotEmpty(nonDiscountableList)) {
            double nonDiscountTotalPrice = nonDiscountableList.stream()
                    .map(OrderDishes::getOrderDishesPrice)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    new RichText(""),
                    new RichText("不优惠合计:" + nonDiscountTotalPrice).with(Color.RED).with(Pos.CENTER_RIGHT),
                    "",
                    "",
                    new RichText("")));
        }
        return FXCollections.observableArrayList(items);

    }

    private void reloadOrder(Integer orderId) {
        Order o = orderService.getOrder(orderId);
        if (o != null) {
            OrderViewBO v = new OrderViewBO();
            v.customerNum = o.getOrderCustomerNums();
            v.orderTime = DateBuilder.base(o.getCreateTime()).format(DATETIME_PATTERN);
            v.orderHadpaid = o.getOrderHadpaid();
            v.totalPrice = sumTotalPrice(orderId);
            v.payStatusName = EnumOrderStatus.of(o.getOrderStatus()).remark;
            v.deduction = o.getFullReduceDishesPrice();
            v.returnAmount = orderService.sumReturnAmount(orderId);
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
        List<OrderDishes> dishes = orderDishesService.selectByOrderId(orderId);
        return CommonUtils.collect(dishes, OrderDishes::getOrderDishesPrice)
                .stream().reduce(0D, Double::sum);
    }


    private void doCloseDesk(Desk desk) {
        Desk current = deskService.getById(desk.getDeskId());
        // 已关台
        if (current == null || EnumDesKStatus.of(current.getStatus()) == EnumDesKStatus.FREE) {
            this.getScene().getWindow().hide();
            return;
        }
        // 未支付金额
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
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() - 60;
        double height = this.getScene().getWindow().getHeight() - 100;
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle("点菜[桌号:" + param.getDeskName() + "]");
        param.setChoiceAction(EnumChoiceAction.PLACE);
        stg.setScene(new Scene(new OrderDishesChoiceView(param, width)));
        stg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        stg.showAndWait();
    }

    private void openSendDishesChoiceView(DeskOrderParam param) {
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() - 60;
        double height = this.getScene().getWindow().getHeight() - 100;
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle("点菜[桌号:" + param.getDeskName() + "]");
        param.setChoiceAction(EnumChoiceAction.SEND);
        stg.setScene(new Scene(new OrderDishesChoiceView(param, width)));
        stg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        stg.showAndWait();
    }

    private void returnDishesConfirm(DeskOrderParam param, TableView<OrderDishesTableItemVO> tv) {
        ObservableList<OrderDishesTableItemVO> list = tv.getSelectionModel().getSelectedItems();
        if (CollectionUtils.isEmpty(list)) {
            AlertBuilder.ERROR("请选择退菜记录");
            return;
        }
        List<String> returnList = new ArrayList<>();
        list.forEach(it -> returnList.add(it.getCol1()));

        List<Integer> ids = returnList.stream()
                .map(id -> CommonUtils.parseInt(id, null))
                .collect(Collectors.toList());
        List<OrderDishes> returnOrderDishes = orderDishesService.selectByIdList(ids);
        for (OrderDishes d : returnOrderDishes) {
            if (EnumOrderSaleType.of(d.getOrderDishesSaletype()) == EnumOrderSaleType.RETURN) {
                AlertBuilder.ERROR("菜品已退,请检查");
                return;
            }
            if (CommonUtils.orElse(d.getOrderDishesIfrefund(), 0) == 1) {
                AlertBuilder.ERROR("菜品不可退,请检查");
                return;
            }
        }
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() / 3;
        double height = this.getScene().getWindow().getHeight() / 4;
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle("退菜");
        param.setChoiceAction(EnumChoiceAction.RETURN);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(width);
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
            stg.close();
        });
        box.getChildren().add(returnBtn);

        stg.setScene(new Scene(box));
        stg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        stg.showAndWait();
    }

    private void doReturnDishes(List<String> orderDishesIds, String reason) {
        LogUtils.info("退菜:" + orderDishesIds + ", " + reason);
        List<Integer> ids = orderDishesIds.stream()
                .map(id -> CommonUtils.parseInt(id, null))
                .collect(Collectors.toList());
        List<OrderDishes> list = orderDishesService.selectByIdList(ids);
        for (OrderDishes d : list) {
            int i = orderDishesService.returnOrderDishes(d);
            System.out.println("退菜结果: " + i);
        }
    }

    private void openPayWayChoiceView(DeskOrderParam param) {
        Stage stg = new Stage();
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(this.getScene().getWindow().getWidth() / 3);
        stg.setHeight(this.getScene().getWindow().getHeight() / 3 * 2);
        stg.setTitle("结账[桌号:" + param.getDeskName() + "]");
        stg.setScene(new Scene(new PayWayChoiceView(param)));
        stg.setOnHidden(e -> CommonUtils.safeRun(param.getCallback()));
        stg.showAndWait();
    }

    private Separator horizontalSeparator() {
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.HORIZONTAL);
        return separator2;

    }

    @Override
    protected void finalize() {
        System.out.println("OrderDetail ........ destroyed");
    }
}
