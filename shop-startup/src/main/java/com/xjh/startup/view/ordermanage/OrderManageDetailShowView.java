package com.xjh.startup.view.ordermanage;


import static com.xjh.common.utils.TableViewUtils.newCol;
import static com.xjh.common.utils.TableViewUtils.snoSp;

import java.util.List;

import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderPayService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleForm;

import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;

public class OrderManageDetailShowView extends SimpleForm {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    public OrderManageDetailShowView(OrderManageListView.BO data) {
        displayOrderInfo(data);
        addLine(new Separator(Orientation.HORIZONTAL));
        displayOrderBillInfo(data);
        addLine(new Separator(Orientation.HORIZONTAL));
        displaySubOrder(data);
        addLine(new Separator(Orientation.HORIZONTAL));
        displayDishesInfo(data);
        displayPaymentInfo(data);
    }

    public void displayOrderInfo(OrderManageListView.BO data) {
        // 订单号
        Label orderNoName = new Label("订单号:");
        orderNoName.setAlignment(Pos.CENTER_RIGHT);
        Label orderNo = new Label(data.getOrderId().toString());
        // 订单状态
        Label orderStatusName = new Label("订单状态:");
        orderStatusName.setAlignment(Pos.CENTER_RIGHT);
        Label orderStatus = new Label(data.getPaymentStatus());
        // 桌号
        Label deskName = new Label("桌号:");
        deskName.setAlignment(Pos.CENTER_RIGHT);
        Label desk = new Label(data.getDeskName());
        // 就餐时间
        Label orderTimeName = new Label("就餐时间:");
        orderTimeName.setAlignment(Pos.CENTER_RIGHT);
        Label orderTime = new Label(data.getOrderTime());

        addLine(newCenterLine(
                orderNoName, orderNo,
                orderStatusName, orderStatus,
                deskName, desk,
                orderTimeName, orderTime
        ));
    }

    public void displayOrderBillInfo(OrderManageListView.BO data) {
        // 应付金额
        Label needPayName = new Label("应付金额:");
        needPayName.setAlignment(Pos.CENTER_RIGHT);
        Label needPay = new Label(data.getNeedPayAmt().toString());
        // 已付金额
        Label paidAmtName = new Label("已付金额:");
        paidAmtName.setAlignment(Pos.CENTER_RIGHT);
        Label paidAmt = new Label(data.getPaidAmt().toString());
        // 菜品总额
        Label totalPriceName = new Label("菜品总额:");
        totalPriceName.setAlignment(Pos.CENTER_RIGHT);
        Label totalPrice = new Label(data.getTotalPrice().toString());
        // 折扣金额
        Label discountAmtName = new Label("折扣金额:");
        discountAmtName.setAlignment(Pos.CENTER_RIGHT);
        Label discountAmt = new Label(data.getDiscountAmt().toString());
        // 折扣金额
        Label returnPriceName = new Label("退菜金额:");
        returnPriceName.setAlignment(Pos.CENTER_RIGHT);
        Label returnPrice = new Label(data.getReturnDishesPrice().toString());
        // 折扣金额
        Label eraseName = new Label("抹零金额:");
        eraseName.setAlignment(Pos.CENTER_RIGHT);
        Label erase = new Label(data.getEraseAmt().toString());
        // 折扣金额
        Label reductionName = new Label("店长减免:");
        reductionName.setAlignment(Pos.CENTER_RIGHT);
        Label reduction = new Label(data.getReductionAmt().toString());

        addLine(newCenterLine(
                needPayName, needPay,
                paidAmtName, paidAmt,
                totalPriceName, totalPrice,
                discountAmtName, discountAmt,
                returnPriceName, returnPrice,
                eraseName, erase,
                reductionName, reduction
        ));
    }

    public void displaySubOrder(OrderManageListView.BO data) {
        SubOrderDAO subOrderDAO = GuiceContainer.getInstance(SubOrderDAO.class);
        SubOrder subOrderCond = new SubOrder();
        subOrderCond.setOrderId(data.getOrderId());
        List<SubOrder> subOrderList = subOrderDAO.selectList(subOrderCond);
        Label label = new Label("点菜批次");
        label.setFont(Font.font(16));
        addLine(label);
        TableView<SubOrder> tableView = new TableView<>();
        tableView.getColumns().addAll(
                newCol("序号", snoSp(), 100),
                newCol("订单批次", "subOrderId", 100),
                newCol("订单时间", so -> DateBuilder.base(so.getCreatetime()).timeStr(), 200)
        );
        tableView.setItems(FXCollections.observableArrayList(subOrderList));
        tableView.refresh();
        addLine(tableView);
    }

    public void displayDishesInfo(OrderManageListView.BO data) {
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(data.getOrderId());

        Label label = new Label("订单菜品");
        label.setFont(Font.font(16));
        addLine(label);
        TableView<OrderDishes> tableView = new TableView<>();
        tableView.getColumns().addAll(
                newCol("序号", snoSp(), 100),
                newCol("菜品名称", orderDishes -> getDishesName(orderDishes.getDishesId()), 150),
                newCol("单价", orderDishes -> new Money(orderDishes.getOrderDishesPrice()), 200),
                newCol("折扣单价", OrderDishes::getOrderDishesDiscountPrice, 200),
                newCol("数量", OrderDishes::getOrderDishesNums, 80),
                newCol("备注", o -> null, 200)
        );
        tableView.setItems(FXCollections.observableArrayList(orderDishesList));
        tableView.refresh();
        addLine(tableView);
    }

    public void displayPaymentInfo(OrderManageListView.BO data) {
        OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);
        List<OrderPay> payList = orderPayService.selectByOrderId(data.getOrderId());
        Label label = new Label("支付信息");
        label.setFont(Font.font(16));
        addLine(label);
        TableView<OrderPay> tableView = new TableView<>();
        tableView.getColumns().addAll(
                newCol("支付时间", pay -> DateBuilder.base(pay.getCreatetime()).timeStr(), 200),
                newCol("支付金额", OrderPay::getActualAmount, 80),
                newCol("后台价格", "", 80),
                newCol("支付方式", pay -> EnumPayMethod.of(pay.getPaymentMethod()).name, 80),
                newCol("支付状态", pay -> "付款成功", 200),
                newCol("交易号", OrderPay::getCardNumber, 200),
                newCol("备注", OrderPay::getRemark, 200)
        );
        tableView.setItems(FXCollections.observableArrayList(payList));
        tableView.refresh();
        addLine(tableView);
    }

    private String getDishesName(Integer dishesId) {
        return dishesService.getDishesName(dishesId);
    }
}
