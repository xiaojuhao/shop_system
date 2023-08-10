package com.xjh.startup.view.paymethods;

import com.xjh.common.enumeration.EnumPayAction;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.PaymentResult;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.service.remote.RemoteService;
import com.xjh.service.vo.PrePaidCard;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.common.model.DeskOrderParam;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;

import java.util.Optional;

public class PaymentDialogOfPreCard extends SimpleForm implements Initializable {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);

    RemoteService remoteService = GuiceContainer.getInstance(RemoteService.class);

    StoreService storeService = GuiceContainer.getInstance(StoreService.class);
    DeskOrderParam param;
    String name;
    EnumPayMethod payMethod;

    public PaymentDialogOfPreCard(DeskOrderParam param, String name, EnumPayMethod payMethod) {
        this.param = param;
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

        StringProperty actualPay = new SimpleStringProperty("0.0");
        grid.add(createTitleLabel("待支付"), 0, row);
        grid.add(new Label(CommonUtils.formatMoney(needPayAmt) + " 元"), 1, row);

        row++;
        Label couponAmtLabel = new Label(CommonUtils.formatMoney(needPayAmt));
        StringProperty payAmtValue = new SimpleStringProperty(CommonUtils.formatMoney(needPayAmt));
        couponAmtLabel.textProperty().bind(payAmtValue);
        grid.add(createTitleLabel("支付金额"), 0, row);
        grid.add(couponAmtLabel, 1, row);

        row++;
        TextField cardNo = new TextField();
        cardNo.setPromptText("储值卡号");
        grid.add(createTitleLabel("储值卡号"), 0, row);
        grid.add(cardNo, 1, row);

        row++;
        TextArea remarkField = new TextArea();
        remarkField.setPrefWidth(oriHeight * 0.8);
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
            String preCardCode = CommonUtils.trim(cardNo.getText());
            if (CommonUtils.length(preCardCode) < 3) {
                AlertBuilder.ERROR("请输入储值卡号");
                return;
            }
            PaymentResult result = new PaymentResult();
            result.setPayAction(EnumPayAction.DO_PAY);
            result.setOrderId(param.getOrderId());
            result.setActualAmount(CommonUtils.parseDouble(actualPay.getValue(), 0D));
            result.setPayAmount(CommonUtils.parseDouble(payAmtValue.getValue(), 0D));
            result.setVoucherNum(1);
            result.setCardNumber(preCardCode);
            result.setPayMethod(payMethod);
            result.setPayRemark(remarkField.getText());
            if (result.getPayAmount() < 0.001) {
                AlertBuilder.ERROR("支付金额不能为0");
                return;
            }
            StoreVO store = storeService.getStore().getData();
            Result<PrePaidCard> preCardQs = remoteService.getOnePrePaidCard(store.getStoreId(), preCardCode);
            if (!preCardQs.isSuccess()) {
                AlertBuilder.ERROR(preCardQs.getMsg());
                return;
            }
            if (preCardQs.getData().getBalance() < result.getPayAmount()) {
                AlertBuilder.ERROR("储值卡余额不足");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("储值卡支付");
            alert.setHeaderText("确定要用储值卡[" + preCardCode + "]支付" + result.getPayAmount() + "元吗?");
            Optional<ButtonType> confirmRs = alert.showAndWait();
            if (confirmRs.orElse(null) != ButtonType.OK) {
                return;
            }
            Result<String> consumeRs = remoteService.prePaidCardConsume(store.getStoreId(), preCardQs.getData(), result.getPayAmount(), param.getOrderId());
            if (!consumeRs.isSuccess()) {
                Logger.info("使用失败，恢复储值卡金额");
                remoteService.updatePrePaidCardBalance(store.getStoreId(), preCardQs.getData(), preCardQs.getData().getBalance());
                AlertBuilder.ERROR(consumeRs.getMsg());
                return;
            }
            this.getScene().getWindow().setUserData(result);
            this.getScene().getWindow().hide();
        });
        grid.add(newCenterLine(cancel, submit), 1, row);
    }

    private Label createTitleLabel(String labelName) {
        Label label = new Label(labelName + ":");
        label.setTextAlignment(TextAlignment.RIGHT);
        return label;
    }


}
