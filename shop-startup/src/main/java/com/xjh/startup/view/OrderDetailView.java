package com.xjh.startup.view;

import static com.xjh.common.utils.CommonUtils.formatMoney;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.xjh.common.enumeration.EnumChoiceAction;
import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.TimeRecord;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.OrderBillVO;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.OrderDishesTableItemVO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OrderDetailView extends VBox {
    // 依赖服务
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);

    ObjectProperty<OrderBillVO> orderView = new SimpleObjectProperty<>();

    public OrderDetailView(Desk desk, double width, double height) {
        TimeRecord cost = TimeRecord.start();
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


            // 第二行
            rowIndex++;
            Label customerNumLabel = createLabel("就餐人数", c -> c.customerNum + "");
            gridPane.add(customerNumLabel, 0, rowIndex);

            Label labelOrder = createLabel("订单号", c -> c.orderId);
            gridPane.add(labelOrder, 1, rowIndex);

            Label labelOrderTime = createLabel("就餐时间", c -> c.orderTime);
            gridPane.add(labelOrderTime, 2, rowIndex);

            Label labelPayStatus = createLabel("支付状态", c -> c.payStatusName);
            gridPane.add(labelPayStatus, 3, rowIndex);

            addLine(gridPane);
        }
        // 分割线
        addHorizontalSeparator();
        {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            /// ----------- 第一行 ---------------
            int row = 0;
            Label totalPriceLabel = createLabel("订单总额", c -> formatMoney(c.totalPrice));
            gridPane.add(totalPriceLabel, 0, row);
            // 第二行
            Label paidAmtLabel = createLabel("已支付", c -> formatMoney(c.orderHadpaid));
            gridPane.add(paidAmtLabel, 1, row);

            Label notPaid = createLabel("还需支付", c -> formatMoney(c.orderNeedPay));
            notPaid.setStyle("-fx-font-size: 16; -fx-text-fill: red");
            gridPane.add(notPaid, 2, row);

            Label labelOrderTime = createLabel("当前折扣", c -> c.discountName);
            gridPane.add(labelOrderTime, 3, row);

            Label labelPayStatus = createLabel("参与优惠金额", c -> formatMoney(c.discountableAmount));
            gridPane.add(labelPayStatus, 4, row);

            /// ----------- 第二行 ---------------
            row++;
            Label orderErase = createLabel("抹零金额", c -> formatMoney(c.orderErase));
            gridPane.add(orderErase, 0, row);

            Label mangerReduction = createLabel("店长折扣", c -> formatMoney(c.orderReduction));
            gridPane.add(mangerReduction, 1, row);

            Label discount = createLabel("折扣金额", c -> formatMoney(c.discountAmount));
            gridPane.add(discount, 2, row);

            Label refund = createLabel("退菜金额", c -> formatMoney(c.returnAmount));
            gridPane.add(refund, 3, row);

            Label fan = createLabel("反结账金额", c -> "0.00");
            gridPane.add(fan, 4, row);

            addLine(gridPane);
        }
        // 分割线
        addHorizontalSeparator();
        // 订单菜单菜单明细
        TableView<OrderDishesTableItemVO> tv = new TableView<>();
        tv.setCache(false);
        Runnable refreshTableView = () -> {
            Order order = orderService.getOrder(orderId);
            List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
            // 账单数据
            loadAndRefreshOrderBill(order, orderDishesList);
            // 菜品明细
            tv.setItems(buildTableItemList(orderDishesList));
            // 刷新列表
            tv.refresh();
        };
        {
            tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tv.setMinHeight(300);
            if (height > 800) {
                tv.setMinHeight(height - 450);
            }
            tv.setPadding(new Insets(5, 0, 0, 5));
            tv.getColumns().addAll(
                    newCol("序号", "orderDishesId", 100),
                    newCol("子订单", "subOrderId", 100),
                    newCol("菜名名称", "dishesName", 300),
                    newCol("单价", "price", 160),
                    newCol("折后价", "discountPrice", 100),
                    newCol("数量", "orderDishesNum", 100),
                    newCol("类型", "saleType", 100)
            );
            addLine(tv);
        }
        // 分割线
        addHorizontalSeparator();
        // 支付消息
        {
            TextArea textArea = new TextArea();
            textArea.setPrefHeight(80);
            textArea.setEditable(false);
            orderView.addListener((x, ov, nv) -> textArea.setText(nv.payInfoRemark));
            addLine(textArea);
        }
        // 分割线
        addHorizontalSeparator();
        // 功能菜单
        {
            DeskOrderParam deskOrderParam = new DeskOrderParam();
            deskOrderParam.setDeskId(desk.getDeskId());
            deskOrderParam.setDeskName(desk.getDeskName());
            deskOrderParam.setOrderId(orderId);
            deskOrderParam.setCallback(refreshTableView);

            FlowPane operationButtonPane = new FlowPane();
            // 按钮一栏上下左右间隔
            operationButtonPane.setPadding(new Insets(10, 0, 10, 20));
            // 按钮之间的间隔
            operationButtonPane.setHgap(20);
            // 功能按钮
            Button orderBtn = createButton("点菜", width, e -> openDishesChoiceView(deskOrderParam));
            Button sendBtn = createButton("送菜", width,e -> openSendDishesChoiceView(deskOrderParam));
            Button returnBtn = createButton("退菜",width, e -> returnDishesConfirm(deskOrderParam, tv));
            Button transferBtn = createButton("转台",width, null);
            Button splitBtn = createButton("拆台",width, null);
            Button payBillBtn = createButton("结账", width,evt -> openPayWayChoiceView(deskOrderParam));
            Button orderErase = createButton("抹零",width, evt -> openOrderEraseView(deskOrderParam));
            FlowPane.setMargin(orderErase, new Insets(0, 0, 0, 100));
            Button reduction = createButton("店长减免",width, evt -> openOrderReductionDialog(deskOrderParam));
            Button discount = createButton("选择折扣", width,evt -> openDiscountSelectionDialog(deskOrderParam));
            // add all buttons
            operationButtonPane.getChildren().addAll(orderBtn, sendBtn, returnBtn, transferBtn, splitBtn, payBillBtn,
                    orderErase, reduction, discount);
            addLine(operationButtonPane);
        }
        Logger.info("OrderDetail构建页面耗时: " + cost.getCostAndReset());
        // 刷新页面
        refreshTableView.run();
        Logger.info("OrderDetail加载数据耗时: " + cost.getCostAndReset());
    }

    private void addLine(Node line){
        this.getChildren().add(line);
    }

    private Label createLabel(String name, Function<OrderBillVO, String> onChage) {
        Label label = new Label(name);
        label.setMinWidth(200);
        if (onChage != null) {
            orderView.addListener((a, b, c) -> label.setText(name + ": " + onChage.apply(c)));
        }
        return label;
    }

    private Button createButton(String name, double width, EventHandler<? super MouseEvent> onClick) {
        width = Math.max(66, (width - 11 * 20) / 11);
        if(width > 100){
            width = 100;
        }
        Button btn = new Button(name);
        btn.setMinWidth(width);
        btn.setMinHeight(width / 5 * 3);
        if (onClick != null) {
            btn.setOnMouseClicked(onClick);
        }
        return btn;
    }

    private TableColumn<OrderDishesTableItemVO, Object> newCol(String name, String filed, double width) {
        TableColumn<OrderDishesTableItemVO, Object> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setSortable(false);
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

    private ObservableList<OrderDishesTableItemVO> buildTableItemList(List<OrderDishes> orderDishes) {
        // 可折扣的菜品信息
        Predicate<OrderDishes> discountableChecker = orderDishesService.discountableChecker();
        // 订单菜品明细
        orderDishes.sort(Comparator.comparing(OrderDishes::getCreatetime));

        List<Integer> dishesIdList = CommonUtils.collect(orderDishes, OrderDishes::getDishesId);
        // 菜品明细
        Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIdList);
        List<OrderDishesTableItemVO> items = new ArrayList<>();
        List<OrderDishes> discountableList = CommonUtils.filter(orderDishes, discountableChecker);
        List<OrderDishes> nonDiscountableList = CommonUtils.filter(orderDishes, discountableChecker.negate());
        if (CommonUtils.isNotEmpty(discountableList)) {
            // 构建菜品展示明细
            discountableList.forEach(o -> items.add(buildTableItem(dishesMap.get(o.getDishesId()), o)));
            // 可参与优惠价格
            double discountableAmount = sumDishesPrice(discountableList);
            orderView.get().discountableAmount = discountableAmount;
            orderView.set(CopyUtils.cloneObj(orderView.get()));
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    RichText.EMPTY,
                    new RichText("参与优惠合计:" + discountableAmount).with(Color.RED).with(Pos.CENTER_RIGHT),
                    RichText.EMPTY,
                    "",
                    RichText.EMPTY));
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    new RichText("以下为不参与优惠活动菜品").with(Color.RED).with(Pos.CENTER_RIGHT),
                    RichText.EMPTY,
                    RichText.EMPTY,
                    "",
                    RichText.EMPTY));
        }
        if (CommonUtils.isNotEmpty(nonDiscountableList)) {
            nonDiscountableList.forEach(o -> {
                // 构建菜品展示明细
                OrderDishesTableItemVO vo = buildTableItem(dishesMap.get(o.getDishesId()), o);
                vo.getDishesName().with(Color.RED);
                vo.getPrice().with(Color.RED);
                vo.getDiscountPrice().with(Color.RED);
                items.add(vo);
            });
            // 不可参与优惠的价格合计
            items.add(new OrderDishesTableItemVO(
                    "",
                    "",
                    RichText.EMPTY,
                    new RichText("不参与优惠合计:" + sumDishesPrice(nonDiscountableList)).with(Color.RED).with(Pos.CENTER_RIGHT),
                    RichText.EMPTY,
                    "",
                    RichText.EMPTY));
        }
        return FXCollections.observableArrayList(items);

    }

    private void loadAndRefreshOrderBill(Order order, List<OrderDishes> orderDishesList) {
        Result<OrderBillVO> billRs = orderService.calcOrderBill(order, orderDishesList);
        if (billRs.isSuccess()) {
            orderView.set(billRs.getData());
        } else {
            AlertBuilder.ERROR(billRs.getMsg());
        }
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
        if (notPaidBillAmount > 0.01) {
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
        if (_buttonType.isPresent() && _buttonType.get().getButtonData() == ButtonBar.ButtonData.YES) {
            Result<String> closeDeskRs = deskService.closeDesk(desk.getDeskId());
            if (!closeDeskRs.isSuccess()) {
                AlertBuilder.ERROR(closeDeskRs.getMsg());
                return;
            }
            this.getScene().getWindow().hide();
        }
    }

    private void openDishesChoiceView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.PLACE);
        String title = "点菜[桌号:" + param.getDeskName() + "]";
        double width = this.getScene().getWindow().getWidth() - 60;
        openView(title, param, new OrderDishesChoiceView(param, width), 1);
    }

    private void openSendDishesChoiceView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.SEND);
        String title = "送菜[桌号:" + param.getDeskName() + "]";
        double width = this.getScene().getWindow().getWidth() - 60;
        openView(title, param, new OrderDishesChoiceView(param, width), 1);
    }

    private void returnDishesConfirm(DeskOrderParam param, TableView<OrderDishesTableItemVO> tv) {
        ObservableList<OrderDishesTableItemVO> list = tv.getSelectionModel().getSelectedItems();
        if (CollectionUtils.isEmpty(list)) {
            AlertBuilder.ERROR("请选择退菜记录");
            return;
        }
        List<String> returnList = new ArrayList<>();
        list.forEach(it -> returnList.add(it.getOrderDishesId()));

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
        param.setChoiceAction(EnumChoiceAction.RETURN);
        param.setReturnList(returnList);
        String title = "退菜[桌号:" + param.getDeskName() + "]";
        openView(title, param, new OrderReturnDishesView(param), 3);
    }

    private void openPayWayChoiceView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "结账[桌号:" + param.getDeskName() + "]";
        openView(title, param, new PayWayChoiceView(param), 3);
    }

    private void openOrderEraseView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.ERASE);
        String title = "抹零[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderEraseView(param);
        openView(title, param, view, 3);
    }

    private void openOrderReductionDialog(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "店长减免[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderReductionView(param);
        openView(title, param, view, 3);
    }

    private void openDiscountSelectionDialog(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "选择折扣[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderDiscountSelectionView(param);
        openView(title, param, view, 3);
    }

    private void openView(String title, DeskOrderParam param, VBox view, int size) {
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() - 60;
        double height = this.getScene().getWindow().getHeight() - 100;
        if (size == 2) {
            width = this.getScene().getWindow().getWidth() / 2;
            height = this.getScene().getWindow().getHeight() / 2;
        } else if (size == 3) {
            width = this.getScene().getWindow().getWidth() / 3;
            height = this.getScene().getWindow().getHeight() / 3;
        }
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle(title);
        stg.setScene(new Scene(view));
        stg.showAndWait();
        // 窗口关闭之后执行回调函数
        CommonUtils.safeRun(param.getCallback());
    }

    private void addHorizontalSeparator() {
        addLine(new Separator(Orientation.HORIZONTAL));
    }

    private boolean notReturn(OrderDishes x) {
        if (x == null) {
            return true;
        }
        return EnumOrderSaleType.of(x.getOrderDishesSaletype()) != EnumOrderSaleType.RETURN;
    }



    private double sumDishesPrice(List<OrderDishes> orderDishes) {
        return orderDishes.stream()
                .filter(this::notReturn)
                .map(OrderDishes::getOrderDishesPrice)
                .filter(Objects::nonNull)
                .reduce(0D, Double::sum);
    }

    private String buildDishesName(Dishes dishes, OrderDishes orderDishes) {
        if (orderDishes.getIfDishesPackage() == 1) {
            DishesPackage pkg = dishesPackageService.getById(orderDishes.getDishesId());
            if (pkg != null) {
                return "(套餐)" + pkg.getDishesPackageName();
            }
        } else if (dishes != null) {
            return dishes.getDishesName();
        }
        return null;
    }

    private OrderDishesTableItemVO buildTableItem(Dishes dishes, OrderDishes orderDishes) {
        EnumOrderSaleType saleType = EnumOrderSaleType.of(orderDishes.getOrderDishesSaletype());
        RichText saleTypeText = new RichText(saleType.remark).with(Color.BLACK);
        if (saleType == EnumOrderSaleType.RETURN) {
            saleTypeText.with(Color.GRAY);
        }
        return new OrderDishesTableItemVO(
                orderDishes.getOrderDishesId() + "",
                orderDishes.getSubOrderId() + "",
                new RichText(buildDishesName(dishes, orderDishes)),
                new RichText(CommonUtils.formatMoney(orderDishes.getOrderDishesPrice())),
                new RichText(CommonUtils.formatMoney(orderDishes.getOrderDishesDiscountPrice())),
                orderDishes.getOrderDishesNums() + "",
                saleTypeText);
    }

    @Override
    protected void finalize() {
        System.out.println("OrderDetail ........ destroyed");
    }
}
