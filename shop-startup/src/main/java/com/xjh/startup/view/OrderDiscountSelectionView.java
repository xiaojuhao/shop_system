package com.xjh.startup.view;

import com.alibaba.fastjson.JSON;
import com.xjh.common.enumeration.EnumDiscountType;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.enumeration.EnumPayStatus;
import com.xjh.common.utils.*;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.valueobject.OrderDiscountVO;
import com.xjh.dao.dataobject.DiscountDO;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.mapper.DiscountDAO;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.vo.DiscountResultVO;
import com.xjh.service.ws.NotifyService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.OkCancelDialog;
import com.xjh.startup.view.base.SmallForm;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DiscountApplyReq;
import com.xjh.startup.view.model.DiscountTypeBO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.xmlbeans.ResourceLoader;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.xjh.common.utils.CommonUtils.*;

public class OrderDiscountSelectionView extends SmallForm {
    OrderDishesService orderDishesService = GuiceContainer.getInstance(OrderDishesService.class);

    OrderPayService orderPayService = GuiceContainer.getInstance(OrderPayService.class);
    StoreService storeService = GuiceContainer.getInstance(StoreService.class);
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    DiscountDAO discountDAO = GuiceContainer.getInstance(DiscountDAO.class);

    public OrderDiscountSelectionView(DeskOrderParam param) {
        VBox discountContentLine = new VBox();
        discountContentLine.setSpacing(10);
        Holder<ResultSupplier<DiscountApplyReq>> discountHolder = new Holder<>();
        // 折扣方式选择
        {
            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.selectedToggleProperty().addListener((x, o, n) -> {
                int select = (int) n.getUserData();
                if (select == 1) {
                    Logger.info("优惠券");
                    Label voucherLabel = new Label("折扣券:");
                    TextField voucher = new TextField();

                    Label cardLabel = new Label("折扣卡:");
                    TextField card = new TextField();
                    discountHolder.hold(() -> {
                        String voucherNo = voucher.getText();
                        String cardNo = card.getText();
                        if (CommonUtils.isNotBlank(voucherNo) && CommonUtils.isNotBlank(cardNo)) {
                            return Result.fail("折扣券和卡折扣只能使用一种");
                        }
                        if (CommonUtils.isBlank(voucherNo) && CommonUtils.isBlank(cardNo)) {
                            return Result.fail("请录入折扣券或者折扣卡");
                        }
                        if (CommonUtils.isNotBlank(voucherNo)) {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setType(EnumDiscountType.COUPON);
                            req.setDiscountName("优惠券");
                            req.setDiscountCode(voucherNo);
                            return Result.success(req);
                        } else {
                            DiscountApplyReq req = new DiscountApplyReq();
                            req.setType(EnumDiscountType.CARD);
                            req.setDiscountName("折扣卡");
                            req.setDiscountCode(cardNo);
                            return Result.success(req);
                        }
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(newCenterLine(voucherLabel, voucher), newCenterLine(cardLabel, card));
                } else if (select == 2) {
                    Logger.info("店长减免");
                    ComboBox<DiscountTypeBO> optList = getDiscountOptions();
                    optList.setPrefWidth(160);
                    Label label = new Label("折扣类型:");

                    Label pwdLabel = new Label("确认密码:");
                    PasswordField pwd = new PasswordField();
                    pwd.setPrefWidth(160);

                    discountHolder.hold(() -> {
                        DiscountTypeBO bo = optList.getSelectionModel().getSelectedItem();
                        if (bo == null) {
                            return Result.fail("请选择折扣类型");
                        }
                        DiscountApplyReq req = new DiscountApplyReq();
                        req.setType(EnumDiscountType.MANAGER);
                        req.setDiscountName(bo.getDiscountName());
                        req.setDiscountRate(bo.getDiscountRate());
                        req.setDiscountCode(EnumDiscountType.MANAGER.name());
                        req.setManagerPwd(CommonUtils.trim(pwd.getText()));
                        return Result.success(req);
                    });

                    discountContentLine.getChildren().clear();
                    discountContentLine.getChildren().addAll(newCenterLine(label, optList), newCenterLine(pwdLabel, pwd));
                } else {
                    Logger.info("未知类型");
                }
            });

            Label discountTypeLabel = new Label("折扣方式:");

            RadioButton coupon = new RadioButton("卡券优惠");
            coupon.setToggleGroup(toggleGroup);
            coupon.setUserData(1);

            RadioButton manager = new RadioButton("店长减免");
            manager.setToggleGroup(toggleGroup);
            manager.setUserData(2);
            manager.setSelected(true);

            addLine(newCenterLine(discountTypeLabel, coupon, manager));
        }
        {
            addLine(discountContentLine);
        }
        {
            Button quit = new Button("关 闭");
            quit.setOnMouseClicked(evt -> this.getScene().getWindow().hide());

            Button button = new Button("使用优惠");
            button.setOnMouseClicked(evt -> {
                if (discountHolder.get() != null) {
                    Result<DiscountApplyReq> req = discountHolder.get().get();
                    if (req.isSuccess()) {
                        Result<String> rs = useCoupon(req.getData(), param);
                        if (rs.isSuccess()) {
                            if (!"CANCEL".equals(rs.getData())) {
                                AlertBuilder.INFO(rs.getData());
                                this.getScene().getWindow().hide();
                            }
                        } else {
                            AlertBuilder.ERROR(rs.getMsg());
                        }
                    } else {
                        AlertBuilder.ERROR(req.getMsg());
                    }
                }
            });

            addLine(newCenterLine(quit, button));

            Button cancel = new Button("取消优惠");
            cancel.setStyle("-fx-text-fill:#FF0000;");
            cancel.setOnMouseClicked(evt -> {
                OkCancelDialog dialog = new OkCancelDialog("取消折扣", "是否取消折扣？");
                Optional<ButtonType> decideRs = dialog.showAndWait();
                if (decideRs.isPresent() && decideRs.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    Result<DiscountResultVO> cancelRs = cancelDiscount(param);
                    if (cancelRs.isSuccess()) {
                        DiscountResultVO discountResult = cancelRs.getData();
                        if (CommonUtils.isBiggerThanZERO(discountResult.getOldDiscountAmount())) {
                            AlertBuilder.INFO("取消折扣成功, 折扣金额:" + new Money(discountResult.getOldDiscountAmount()));
                        } else {
                            AlertBuilder.INFO("当前订单无折扣信息");
                        }
                        this.getScene().getWindow().hide();
                    } else {
                        AlertBuilder.ERROR(cancelRs.getMsg());
                    }
                }
            });
            addLine(newCenterLine(cancel));
        }
    }

    private Result<String> useCoupon(DiscountApplyReq req, DeskOrderParam param) {
        if (req == null) {
            return Result.fail("请选择折扣信息");
        }
        if (req.getType() == EnumDiscountType.MANAGER && !storeService.checkManagerPwd(req.getManagerPwd())) {
            return Result.fail("店长密码错误");
        }
        if (req.getDiscountRate() <= 0.001 || req.getDiscountRate() >= 0.999) {
            return Result.fail("折扣信息错误");
        }
        Logger.info(JSON.toJSONString(req));
        Result<DiscountResultVO> rs = this.calculateDiscount(param, req);
        if (rs.isSuccess()) {
            DiscountResultVO discountResult = rs.getData();
            String tips = "折扣金额:" + new Money(discountResult.getDiscountAmount()) + "";
            if (isBiggerThanZERO(discountResult.getOldDiscountPrice())) {
                BigDecimal delta = subtract(discountResult.getDiscountPrice(), discountResult.getOldDiscountPrice());
                if (isBiggerThanZERO(delta)) {
                    tips += ",新折扣比原折扣多" + new Money(abs(delta));
                } else {
                    tips += ",新折扣比原折扣少" + new Money(abs(delta));
                }
            }
            tips += ", 是否使用?";
            OkCancelDialog dialog = new OkCancelDialog("折扣信息", tips);
            Optional<ButtonType> decideRs = dialog.showAndWait();
            if (decideRs.isPresent() && decideRs.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                rs = this.handleDiscount(param, req);
                if (rs.isSuccess()) {
                    return Result.success("优惠使用成功,优惠金额:" + new Money(discountResult.getDiscountAmount()));
                } else {
                    return Result.fail(rs.getMsg());
                }
            } else {
                return Result.success("CANCEL");
            }
        } else {
            return Result.fail(rs.getMsg());
        }
    }

    private ComboBox<DiscountTypeBO> getDiscountOptions() {
        List<DiscountDO> discountList = discountDAO.selectList(new DiscountDO());
        ObservableList<DiscountTypeBO> list = FXCollections.observableArrayList(discountList.stream().map(it -> new DiscountTypeBO(it.getDiscountName(), it.getRate())).collect(Collectors.toSet()));
        ComboBox<DiscountTypeBO> optList = new ComboBox<>(list);
        optList.setConverter(new StringConverter<DiscountTypeBO>() {
            @Override
            public String toString(DiscountTypeBO object) {
                return object.getDiscountName();
            }

            @Override
            public DiscountTypeBO fromString(String string) {
                return null;
            }
        });
        return optList;
    }

    private Result<DiscountResultVO> handleDiscount(DeskOrderParam param, DiscountApplyReq req) {
        if (param == null || req == null) {
            return Result.fail("入参错误");
        }
        DiscountResultVO discountResult = new DiscountResultVO();
        Integer orderId = param.getOrderId();
        Order order = orderService.getOrder(orderId);
        // 加载orderDishes
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
        // 加载discount Checker
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        // 可以参加折扣的菜品
        List<OrderDishes> discountableOrderDishes = CommonUtils.filter(orderDishesList, discountChecker);
        //
        for (OrderDishes od : discountableOrderDishes) {
            discountResult.addOldDiscountPrice(od.getOrderDishesDiscountPrice()); // 累计原折扣金额

            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(od.getOrderDishesId());
            update.setOrderDishesDiscountPrice(od.getOrderDishesPrice() * req.getDiscountRate());

            discountResult.addTotalPrice(od.getOrderDishesPrice()); // 累计总金额
            discountResult.addDiscountPrice(update.getOrderDishesDiscountPrice()); // 累计折扣金额

            Result<Integer> rs = orderDishesService.updatePrimaryKey(update);
            if (!rs.isSuccess()) {
                return Result.fail(rs.getMsg());
            }
        }
        // 保存折扣信息
        OrderDiscountVO orderDiscount = new OrderDiscountVO();
        orderDiscount.setDiscountName(req.getDiscountName());
        orderDiscount.setRate(new Double(req.getDiscountRate()).floatValue());
        orderDiscount.setType(req.getType().code());
        orderDiscount.setDiscountCode(req.getDiscountCode());
        orderDiscount.setDiscountId(0);

        Order orderUpdate = new Order();
        orderUpdate.setOrderId(param.getOrderId());
        orderUpdate.setOrderDiscountInfo(tryEncodeBase64(JSON.toJSONString(orderDiscount)));
        orderUpdate.setDiscountReason(req.getDiscountName());
        orderService.updateByOrderId(orderUpdate);

        NotifyService.useDiscount(order.getDeskId());

        return Result.success(discountResult);
    }

    private Result<DiscountResultVO> cancelDiscount(DeskOrderParam param) {
        if (param == null) {
            return Result.fail("入参错误");
        }
        DiscountResultVO discountResult = new DiscountResultVO();
        Integer orderId = param.getOrderId();
        List<OrderPay> orderPays = orderPayService.selectByOrderId(orderId);
        for (OrderPay pay : orderPays) {
            if (Objects.equals(pay.getPaymentStatus(), EnumPayStatus.PAID.code)) {
                Set<Integer> payMethod = newHashset(EnumPayMethod.VOUCHER.code, //
                        EnumPayMethod.MEITUAN_COUPON.code, EnumPayMethod.MEITUAN_PACKAGE.code, //
                        EnumPayMethod.WECHAT_COUPON.code, EnumPayMethod.OHTER.code, //
                        EnumPayMethod.WANDA_COUPON.code, EnumPayMethod.WANDA_PACKAGE.code); //
                if (payMethod.contains(pay.getPaymentMethod())) {
                    return Result.fail("已使用别的优惠方式，无法再进行打折操作");
                }
            }
        }
        Order order = orderService.getOrder(orderId);
        if (CommonUtils.isNotBlank(order.getOrderDiscountInfo())) {
            OrderDiscountVO d = JSON.parseObject(tryDecodeBase64(order.getOrderDiscountInfo()), OrderDiscountVO.class);
            if (d != null && d.getType() != EnumDiscountType.MANAGER.code() && d.getType() != -1) {
                return Result.fail("已使用别的优惠方式，无法再进行打折操作");
            }
        }
        // 加载orderDishes
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
        // 加载discount Checker
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        // 可以参加折扣的菜品
        List<OrderDishes> discountableOrderDishes = CommonUtils.filter(orderDishesList, discountChecker);
        //
        for (OrderDishes od : discountableOrderDishes) {
            discountResult.addOldDiscountPrice(od.getOrderDishesDiscountPrice()); // 累计原折扣金额
            discountResult.addTotalPrice(od.getOrderDishesPrice()); // 累计总金额

            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(od.getOrderDishesId());
            update.setOrderDishesDiscountPrice(od.getOrderDishesPrice());
            discountResult.addDiscountPrice(update.getOrderDishesDiscountPrice()); // 累计折扣金额

            Result<Integer> rs = orderDishesService.updatePrimaryKey(update);
            if (!rs.isSuccess()) {
                return Result.fail(rs.getMsg());
            }
        }
        // 保存折扣信息
        OrderDiscountVO orderDiscount = new OrderDiscountVO();

        Order orderUpdate = new Order();
        orderUpdate.setOrderId(param.getOrderId());
        orderUpdate.setOrderDiscountInfo(tryEncodeBase64(JSON.toJSONString(orderDiscount)));
        orderUpdate.setDiscountReason("无");
        orderService.updateByOrderId(orderUpdate);

        return Result.success(discountResult);
    }


    private Result<DiscountResultVO> calculateDiscount(DeskOrderParam param, DiscountApplyReq req) {
        if (param == null || req == null) {
            return Result.fail("入参错误");
        }
        DiscountResultVO discountResult = new DiscountResultVO();
        Integer orderId = param.getOrderId();
        // 加载orderDishes
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(orderId);
        // 加载discount Checker
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        // 可以参加折扣的菜品
        List<OrderDishes> discountableOrderDishes = CommonUtils.filter(orderDishesList, discountChecker);
        //
        for (OrderDishes od : discountableOrderDishes) {
            discountResult.addOldDiscountPrice(od.getOrderDishesDiscountPrice()); // 累计原折扣金额
            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(od.getOrderDishesId());
            update.setOrderDishesDiscountPrice(od.getOrderDishesPrice() * req.getDiscountRate());
            discountResult.addTotalPrice(od.getOrderDishesPrice()); // 累计总金额
            discountResult.addDiscountPrice(update.getOrderDishesDiscountPrice()); // 累计折扣金额

        }

        return Result.success(discountResult);
    }

}
