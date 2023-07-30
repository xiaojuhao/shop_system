package com.xjh.startup.view.paymethods;

import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.CouponList;
import com.xjh.dao.mapper.CouponListDAO;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.IntStringPair;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentDialogOfCoupon extends SimpleForm implements Initializable {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    CouponListDAO couponListDAO = GuiceContainer.getInstance(CouponListDAO.class);
    DeskOrderParam param;
    int type;
    String name;
    EnumPayMethod payMethod;

    public PaymentDialogOfCoupon(DeskOrderParam param, int type, String name, EnumPayMethod payMethod) {
        this.param = param;
        this.type = type;
        this.name = name;
        this.payMethod = payMethod;
    }

    @Override
    public void initialize() {
        Window pwindow = this.getScene().getWindow();
        double oriHeight = pwindow.getHeight();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        int row = 0;
        grid.add(createTitleLabel("桌号"), 0, row);
        grid.add(new Label(param.getDeskName()), 1, row);

        row++;
        double needPayAmt = orderService.notPaidBillAmount(param.getOrderId());
        StringProperty couponValue = new SimpleStringProperty("0.0");
        StringProperty actualPay = new SimpleStringProperty("0.0");
        grid.add(createTitleLabel("待支付"), 0, row);
        grid.add(new Label(CommonUtils.formatMoney(needPayAmt) + " 元"), 1, row);

        row++;
        Label couponAmtLabel = new Label("00");
        couponAmtLabel.textProperty().bind(couponValue);
        grid.add(createTitleLabel("支付金额"), 0, row);
        grid.add(couponAmtLabel, 1, row);

        row++;
        Label actualAmtLabel = new Label("00");
        actualAmtLabel.textProperty().bind(actualPay);
        grid.add(createTitleLabel("后台金额"), 0, row);
        grid.add(actualAmtLabel, 1, row);

        row++;
        TextField couponNum = new TextField();
        ComboBox<IntStringPair> combo = buildCombo(type);
        combo.setPrefWidth(100);
        combo.valueProperty().addListener((obs, old, _new) -> {
            if (_new == null) {
                return;
            }
            int num = CommonUtils.parseInt(couponNum.getText(), 0);
            CouponList coupon = (CouponList) _new.getAttachment();
            couponValue.set(CommonUtils.formatMoney(num * Math.min(needPayAmt, coupon.getCouponAmount())));
            actualPay.set(CommonUtils.formatMoney(num * coupon.getActualAmount()));
        });
        VBox groupBox = new VBox();
        groupBox.setSpacing(5);
        List<TextField> serialNoList = new ArrayList<>();

        couponNum.setPromptText("张数");
        couponNum.setText("0");
        couponNum.setPrefWidth(40);
        couponNum.textProperty().addListener((obs, old, _new) -> {
            serialNoList.clear();
            groupBox.getChildren().clear();
            int num = CommonUtils.parseInt(_new, 0);
            for (int i = 0; i < num; i++) {
                TextField serialNoInput = new TextField();
                Label serialNoLabel = new Label("序列号" + i + ":");
                serialNoLabel.setPrefWidth(60);
                serialNoList.add(serialNoInput);
                groupBox.getChildren().add(newLine(serialNoLabel, serialNoInput));

                CouponList coupon = (CouponList) combo.getSelectionModel().getSelectedItem().getAttachment();
                if (coupon != null) {
                    couponValue.set(CommonUtils.formatMoney(num * Math.min(needPayAmt, coupon.getCouponAmount())));
                    actualPay.set(CommonUtils.formatMoney(num * coupon.getActualAmount()));
                } else {
                    couponValue.set("0.00");
                    actualPay.set("0.00");
                }

            }
            pwindow.setHeight(oriHeight + num * (couponNum.getHeight() + 5));
        });
        grid.add(createTitleLabel("选择券"), 0, row);
        Label middleLabel = new Label("元 X");
        middleLabel.setAlignment(Pos.BOTTOM_RIGHT);
        HBox box = new HBox();
        box.getChildren().addAll(combo, middleLabel, couponNum, new Label("张"));
        grid.add(box, 1, row);
        // 序列号
        row++;
        grid.add(groupBox, 0, row, 2, 1);

        row++;
        TextField remarkField = new TextField();
        remarkField.setPromptText("支付消息");
        grid.add(createTitleLabel("备注"), 0, row);
        grid.add(remarkField, 1, row);

        this.addLine(newCenterLine(grid));

        //操作按钮
        row++;
        Button cancel = new Button("取 消");
        cancel.setOnAction(evt -> {
            PaymentResult result = new PaymentResult();
            result.setPayAction(EnumPayAction.CANCEL_PAY);
            this.getScene().getWindow().setUserData(result);
            this.getScene().getWindow().hide();
        });
        Button submit = new Button("确 定");
        submit.setOnAction(evt -> {
            List<String> cardNoList = serialNoList.stream().map(TextInputControl::getText).collect(Collectors.toList());
            PaymentResult result = new PaymentResult();
            result.setPayAction(EnumPayAction.DO_PAY);
            result.setOrderId(param.getOrderId());
            result.setActualAmount(CommonUtils.parseDouble(actualPay.getValue(), 0D));
            result.setPayAmount(CommonUtils.parseDouble(couponValue.getValue(), 0D));
            result.setVoucherNum(cardNoList.size());
            result.setCardNumber(String.join(",", cardNoList));
            result.setPayMethod(payMethod);
            result.setPayRemark(remarkField.getText());
            this.getScene().getWindow().setUserData(result);
            this.getScene().getWindow().hide();
        });
        grid.add(newCenterLine(cancel, submit), 1, row);
    }


    private ComboBox<IntStringPair> buildCombo(int type) {
        CouponList cond = new CouponList();
        cond.setType(type);
        List<CouponList> list = couponListDAO.selectList(cond);
        return new ComboBox<>(FXCollections.observableArrayList(
                list.stream().map(it -> new IntStringPair(it.getType(), it.getCouponName(), it))
                        .collect(Collectors.toList())
        ));
    }

    private Label createTitleLabel(String labelName) {
        Label label = new Label(labelName + ":");
        label.setTextAlignment(TextAlignment.RIGHT);
        return label;
    }


}
