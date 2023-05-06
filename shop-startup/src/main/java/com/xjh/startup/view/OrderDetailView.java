package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumChoiceAction;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.utils.*;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.PrinterDAO;
import com.xjh.dao.mapper.PrinterTaskDAO;
import com.xjh.service.domain.*;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.foundation.printers.OrderPrinterHelper;
import com.xjh.startup.foundation.printers.PrintResult;
import com.xjh.startup.foundation.printers.PrinterImpl;
import com.xjh.startup.foundation.printers.PrinterStatus;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.MediumForm;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.OrderDishesTableItemBO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.xjh.common.utils.CommonUtils.formatMoney;
import static com.xjh.common.utils.TableViewUtils.newCol;

public class OrderDetailView extends VBox implements Initializable {
    // 依赖服务
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);
    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    OrderPrinterHelper orderPrinterHelper = GuiceContainer.getInstance(OrderPrinterHelper.class);
    PrinterDAO printerDAO = GuiceContainer.getInstance(PrinterDAO.class);
    PrinterTaskDAO printerTaskDAO = GuiceContainer.getInstance(PrinterTaskDAO.class);

    ObjectProperty<OrderOverviewVO> orderView = new SimpleObjectProperty<>();
    Desk desk;

    public OrderDetailView(Desk desk) {
        this.desk = desk;
    }

    public void initialize() {
        double width = this.getScene().getWidth();
        double height = this.getScene().getHeight();
        TimeRecord cost = TimeRecord.start();
        Integer orderId = desk.getOrderId();
        TableView<OrderDishesTableItemBO> tableView = new TableView<>();
        Runnable refreshView = () -> {
            Order order = orderService.getOrder(orderId);
            List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
            List<OrderPay> orderPays = orderPayService.selectByOrderId(orderId);
            // 账单数据
            loadAndRefreshOrderBill(order, orderDishesList, orderPays);
            // 菜品明细
            tableView.setItems(buildTableItemList(orderDishesList));
            // 刷新列表
            tableView.refresh();
        };

        {
            int rowIndex = 0;
            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            // 第一行
            String tableName = "桌号：" + desk.getDeskName();
            Label tableNameLabel = new Label(tableName);
            tableNameLabel.setMinWidth(width / 5 * 4);
            tableNameLabel.setMinHeight(50);
            tableNameLabel.setFont(new Font(18));
            tableNameLabel.setAlignment(Pos.CENTER);
            orderView.addListener((x, o, n) -> tableNameLabel.setText("桌号:" + n.getDeskName()));
            gridPane.add(tableNameLabel, 0, rowIndex, 4, 1);
            // 关台按钮
            Button closeDeskBtn = new Button("关台");
            closeDeskBtn.setMinWidth(100);
            GridPane.setMargin(closeDeskBtn, new Insets(0, 0, 30, 0));
            closeDeskBtn.setOnMouseClicked(evt -> doCloseDesk(desk));
            gridPane.add(closeDeskBtn, 4, rowIndex, 1, 2);


            // 第二行
            rowIndex++;
            Label customerNumLabel = createLabel("就餐人数", width, c -> c.customerNum + "");
            gridPane.add(customerNumLabel, 0, rowIndex);

            Label labelOrder = createLabel("订单号", width, c -> c.orderId);
            gridPane.add(labelOrder, 1, rowIndex);

            Label labelOrderTime = createLabel("就餐时间", width, c -> c.orderTime);
            gridPane.add(labelOrderTime, 2, rowIndex);

            Label labelPayStatus = createLabel("支付状态", width, c -> c.payStatusName);
            gridPane.add(labelPayStatus, 3, rowIndex);

            Button checkPayStatus = new Button("检测支付结果");
            checkPayStatus.setMinWidth(100);
            checkPayStatus.setOnMouseClicked(evt -> refreshView.run());
            gridPane.add(checkPayStatus, 4, rowIndex);
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
            Label totalPriceLabel = createLabel("订单总额", width, c -> formatMoney(c.totalPrice));
            gridPane.add(totalPriceLabel, 0, row);
            // 第二行
            Label paidAmtLabel = createLabel("已支付", width, c -> formatMoney(c.orderHadpaid));
            gridPane.add(paidAmtLabel, 1, row);

            Label notPaid = createLabel("还需支付", width, c -> formatMoney(c.orderNeedPay));
            notPaid.setStyle("-fx-font-size: 16; -fx-text-fill: red");
            gridPane.add(notPaid, 2, row);

            Label labelOrderTime = createLabel("当前折扣", width, c -> c.discountName);
            gridPane.add(labelOrderTime, 3, row);

            Label labelPayStatus = createLabel("参与优惠金额", width, c -> formatMoney(c.discountableAmount));
            gridPane.add(labelPayStatus, 4, row);

            /// ----------- 第二行 ---------------
            row++;
            Label orderErase = createLabel("抹零金额", width, c -> formatMoney(c.orderErase));
            gridPane.add(orderErase, 0, row);

            Label mangerReduction = createLabel("店长折扣", width, c -> formatMoney(c.orderReduction));
            gridPane.add(mangerReduction, 1, row);

            Label discount = createLabel("折扣金额", width, c -> formatMoney(c.discountAmount));
            gridPane.add(discount, 2, row);

            Label refund = createLabel("退菜金额", width, c -> formatMoney(c.returnDishesPrice));
            gridPane.add(refund, 3, row);

            Label fan = createLabel("反结账金额", width, c -> "0.00");
            gridPane.add(fan, 4, row);

            addLine(gridPane);
        }
        // 分割线
        addHorizontalSeparator();
        // 订单菜单菜单明细
        {
            tableView.setCache(false);
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.setMinHeight(300);
            if (height > 800) {
                tableView.setMinHeight(height - 450);
            }
            tableView.setPadding(new Insets(5, 0, 0, 5));
            tableView.getColumns().addAll(
                    newCol("序号", "orderDishesId", 100),
                    newCol("子订单", "subOrderId", 100),
                    newCol("菜名名称", "dishesName", 300),
                    newCol("单价", "price", 160),
                    newCol("折后价", "discountPrice", 100),
                    newCol("数量", "orderDishesNum", 100),
                    newCol("类型", "saleType", 100)
            );
            addLine(tableView);
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
            deskOrderParam.setCallback(refreshView);

            FlowPane operationButtonPane = new FlowPane();
            // 按钮一栏上下左右间隔
            operationButtonPane.setPadding(new Insets(10, 0, 10, 20));
            // 按钮之间的间隔
            operationButtonPane.setHgap(20);
            // 功能按钮
            Button orderBtn = createButton("点菜", width, e -> openDishesChoiceView(deskOrderParam));
            Button sendBtn = createButton("送菜", width, e -> openSendDishesChoiceView(deskOrderParam));
            Button returnBtn = createButton("退菜", width, e -> returnDishesConfirm(deskOrderParam, tableView));
            Button transferBtn = createButton("转台", width, e -> openDeskChangeView(deskOrderParam));
            Button splitBtn = createButton("拆台", width, null);
            Button payBillBtn = createButton("结账", width, evt -> openPayWayChoiceView(deskOrderParam));

            FlowPane.setMargin(payBillBtn, new Insets(0, 80, 0, 0));

            Button orderErase = createButton("抹零", width, evt -> openOrderEraseView(deskOrderParam));
            Button repay = createButton("重新结账", width, evt -> orderRepay(deskOrderParam));
            Button reduction = createButton("店长减免", width, evt -> openOrderReductionDialog(deskOrderParam));
            Button discount = createButton("选择折扣", width, evt -> openDiscountSelectionDialog(deskOrderParam));
            Button printOrder = createButton("打印账单", width, evt -> submitPrintOrderInfo(deskOrderParam));
            // add all buttons
            operationButtonPane.getChildren().addAll(
                    orderBtn, sendBtn, returnBtn, transferBtn, splitBtn, payBillBtn,
                    repay, orderErase, reduction, discount, printOrder);
            addLine(operationButtonPane);
        }
        Logger.info("OrderDetail构建页面耗时: " + cost.getCostAndReset());
        // 刷新页面
        refreshView.run();
        Logger.info("OrderDetail加载数据耗时: " + cost.getCostAndReset());
    }

    private void addLine(Node line) {
        this.getChildren().add(line);
    }

    private Label createLabel(String name, double width, Function<OrderOverviewVO, String> onChage) {
        double swdith = Math.max(200, width / 5 - 20);

        Label label = new Label(name);
        label.setMinWidth(swdith);
        if (onChage != null) {
            orderView.addListener((a, b, c) -> label.setText(name + ": " + onChage.apply(c)));
        }
        return label;
    }

    private Button createButton(String name, double width, EventHandler<? super MouseEvent> onClick) {
        width = Math.max(66, (width - 12 * 15) / 12);
        if (width > 100) {
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

    private ObservableList<OrderDishesTableItemBO> buildTableItemList(List<OrderDishes> orderDishes) {
        // 可折扣的菜品信息
        Predicate<OrderDishes> discountableChecker = orderDishesService.discountableChecker();
        // 订单菜品明细
        orderDishes.sort(Comparator.comparing(OrderDishes::getCreatetime));

        List<Integer> dishesIdList = CommonUtils.collect(orderDishes, OrderDishes::getDishesId);
        // 菜品明细
        Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIdList);
        List<OrderDishesTableItemBO> items = new ArrayList<>();
        List<OrderDishes> discountableList = CommonUtils.filter(orderDishes, discountableChecker);
        List<OrderDishes> nonDiscountableList = CommonUtils.filter(orderDishes, discountableChecker.negate());
        if (CommonUtils.isNotEmpty(discountableList)) {
            // 构建菜品展示明细
            discountableList.forEach(o -> items.add(buildTableItem(dishesMap.get(o.getDishesId()), o)));
            // 可参与优惠价格
            double discountableAmount = sumDishesPrice(discountableList);
            orderView.get().discountableAmount = discountableAmount;
            orderView.set(CopyUtils.cloneObj(orderView.get()));
            items.add(new OrderDishesTableItemBO(
                    "",
                    "",
                    RichText.EMPTY,
                    new RichText("参与优惠合计:" + discountableAmount).with(Color.RED).with(Pos.CENTER_RIGHT),
                    RichText.EMPTY,
                    "",
                    RichText.EMPTY));
        }
        if (CommonUtils.isNotEmpty(nonDiscountableList)) {
            items.add(new OrderDishesTableItemBO(
                    "",
                    "",
                    new RichText("以下为不参与优惠活动菜品").with(Color.RED).with(Pos.CENTER_RIGHT),
                    RichText.EMPTY,
                    RichText.EMPTY,
                    "",
                    RichText.EMPTY));
            // 不参加优惠的菜品
            nonDiscountableList.forEach(o -> {
                // 构建菜品展示明细
                OrderDishesTableItemBO vo = buildTableItem(dishesMap.get(o.getDishesId()), o);
                vo.getDishesName().with(Color.RED);
                vo.getPrice().with(Color.RED);
                vo.getDiscountPrice().with(Color.RED);
                items.add(vo);
            });
            // 不可参与优惠的价格合计
            items.add(new OrderDishesTableItemBO(
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

    private void loadAndRefreshOrderBill(Order order, List<OrderDishes> orderDishesList, List<OrderPay> orderPays) {
        Result<OrderOverviewVO> billRs = orderService.buildOrderOverview(order, orderDishesList, orderPays);
        if (billRs.isSuccess()) {
            orderView.set(billRs.getData());
        } else {
            AlertBuilder.ERROR(billRs.getMsg());
        }
    }


    private void doCloseDesk(Desk desk) {
        Desk current = deskService.getById(desk.getDeskId());
        // 已关台
        if (current == null || EnumDeskStatus.of(current.getStatus()) == EnumDeskStatus.FREE) {
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
                new ButtonType("关台", ButtonBar.ButtonData.YES),
                new ButtonType("取消", ButtonBar.ButtonData.NO));
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
        openView(title, param, new OrderDishesChoiceView(param, width));
    }

    private void openSendDishesChoiceView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.SEND);
        String title = "送菜[桌号:" + param.getDeskName() + "]";
        double width = this.getScene().getWindow().getWidth() - 60;
        openView(title, param, new OrderDishesChoiceView(param, width));
    }

    private void returnDishesConfirm(DeskOrderParam param, TableView<OrderDishesTableItemBO> tv) {
        ObservableList<OrderDishesTableItemBO> list = tv.getSelectionModel().getSelectedItems();
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
            if (OrElse.orGet(d.getOrderDishesIfrefund(), 0) == 1) {
                AlertBuilder.ERROR("菜品不可退,请检查");
                return;
            }
        }
        param.setChoiceAction(EnumChoiceAction.RETURN);
        param.setReturnList(returnList);
        String title = "退菜[桌号:" + param.getDeskName() + "]";
        openView(title, param, new OrderReturnDishesView(param));
    }

    private void openDeskChangeView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "转台[桌号:" + param.getDeskName() + "]";
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() * 0.4;
        double height = this.getScene().getWindow().getHeight() * 0.68;
        stg.initOwner(this.getScene().getWindow());
        stg.initModality(Modality.WINDOW_MODAL);
        stg.initStyle(StageStyle.DECORATED);
        stg.centerOnScreen();
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle(title);
        stg.setScene(new Scene(new DeskChangeView(param)));
        stg.showAndWait();
        // 窗口关闭之后执行回调函数
        CommonUtils.safeRun(param.getCallback());
    }

    private void openPayWayChoiceView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "结账[桌号:" + param.getDeskName() + "]";
        openView(title, param, new PayWayChoiceView(param));
    }

    private void openOrderEraseView(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.ERASE);
        String title = "抹零[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderEraseView(param);
        openView(title, param, view);
    }

    private void openOrderReductionDialog(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "店长减免[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderReductionView(param);
        openView(title, param, view);
    }

    private void orderRepay(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "重新结账[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderRepayView(param);
        openView(title, param, view);
    }

    private void openDiscountSelectionDialog(DeskOrderParam param) {
        param.setChoiceAction(EnumChoiceAction.NULL);
        String title = "选择折扣[桌号:" + param.getDeskName() + "]";
        VBox view = new OrderDiscountSelectionView(param);
        openView(title, param, view);
    }

    private void submitPrintOrderInfo(DeskOrderParam param) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            PrinterTaskDO task = printerTaskDAO.selectByPrintTaskName("api.print.task.PrintTaskOrderSample");
            if (task == null) {
                AlertBuilder.ERROR("打印机配置错误,请检查");
                return;
            }
            JSONObject taskContent = JSON.parseObject(Base64.decodeStr(task.getPrintTaskContent()));
            JSONArray array = JSON.parseArray(taskContent.getString("printerSelectStrategy"));
            if (array == null) {
                AlertBuilder.ERROR("打印机配置错误,请检查2");
                return;
            }
            Desk desk = deskService.getById(param.getDeskId());
            Integer printerId = null;
            for (int i = 0; i < array.size(); i++) {
                JSONObject conf = array.getJSONObject(i);
                if (CommonUtils.eq(conf.getInteger("deskTypeId"), desk.getBelongDeskType())) {
                    printerId = conf.getInteger("printerId");
                }
            }
            PrinterDO dd = printerDAO.selectByPrinterId(printerId);
            if (dd == null) {
                AlertBuilder.ERROR("打印机配置错误,请检查3");
                return;
            }
            PrinterImpl printer = new PrinterImpl(dd);

            List<Object> printData = orderPrinterHelper.buildOrderPrintData(param);
            PrintResult rs = printer.print(printData, true);
            Logger.info(JSON.toJSONString(rs));
            if (!rs.isSuccess()) {
                PrinterStatus sts = PrinterStatus.of(rs.getResultCode());
                if (sts == PrinterStatus.UNKNOWN) {
                    AlertBuilder.ERROR("打印机配置错误,请检查4");
                } else {
                    AlertBuilder.ERROR(sts.remark);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            clear.run();
        }
    }

    private void openView(String title, DeskOrderParam param, Parent node) {
        Stage stg = new Stage();
        double width = this.getScene().getWindow().getWidth() - 60;
        double height = this.getScene().getWindow().getHeight() - 100;
        if (node instanceof MediumForm) {
            width = this.getScene().getWindow().getWidth() / 2;
            height = this.getScene().getWindow().getHeight() / 2;
        } else if (node instanceof SmallForm) {
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
        stg.setScene(new Scene(node));
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
            String attrInfo = orderDishesService.generateAttrDigest(orderDishes);
            return dishes.getDishesName() + attrInfo;
        }
        return null;
    }

    private OrderDishesTableItemBO buildTableItem(Dishes dishes, OrderDishes orderDishes) {
        EnumOrderSaleType saleType = EnumOrderSaleType.of(orderDishes.getOrderDishesSaletype());
        RichText saleTypeText = new RichText(saleType.remark).with(Color.BLACK);
        if (saleType == EnumOrderSaleType.RETURN) {
            saleTypeText.with(Color.GRAY);
        }
        return new OrderDishesTableItemBO(
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
