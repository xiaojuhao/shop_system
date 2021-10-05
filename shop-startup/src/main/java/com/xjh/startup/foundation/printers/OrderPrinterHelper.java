package com.xjh.startup.foundation.printers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderPayService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.startup.foundation.constants.EnumAlign;
import com.xjh.startup.foundation.constants.EnumComType;
import com.xjh.startup.view.model.DeskOrderParam;

@Singleton
public class OrderPrinterHelper {
    @Inject
    OrderService orderService;
    @Inject
    OrderDishesService orderDishesService;
    @Inject
    StoreService storeService;
    @Inject
    DeskService deskService;
    @Inject
    DishesService dishesService;
    @Inject
    OrderPayService orderPayService;

    public JSONArray buildOrderPrintData(DeskOrderParam param) {
        // 可折扣的菜品信息
        Predicate<OrderDishes> discountableChecker = orderDishesService.discountableChecker();
        List<JSONObject> array = new ArrayList<>();
        Order order = orderService.getOrder(param.getOrderId());
        StoreVO store = storeService.getStore().getData();
        Desk desk = deskService.getById(order.getDeskId());
        array.add(simpleLineText("订单编号:" + order.getCreateTime() + "" + order.getOrderId()));
        array.add(simpleLineText("门店名称:" + store.getName()));
        array.add(simpleLineText("开台时间:" + DateBuilder.base(order.getCreateTime()).timeStr()));
        array.add(simpleLineText("桌台信息:" + desk.getBelongDeskType() + desk.getDeskName()));
        array.add(simpleLineText("用餐人数:" + order.getOrderCustomerNums()));
        array.add(simpleLineText("点菜员:点菜员1"));
        array.add(crlf()); // 换行
        // 订单菜品
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(param.getOrderId());
        List<OrderDishes> discountableList = CommonUtils.filter(orderDishesList, discountableChecker);
        List<OrderDishes> nonDiscountableList = CommonUtils.filter(orderDishesList, discountableChecker.negate());
        DoubleAdder sumPrice = new DoubleAdder();
        array.addAll(dishesItems("普通菜品", discountableList, sumPrice));
        array.add(crlf()); // 换行
        array.addAll(dishesItems("特价菜及酒水", nonDiscountableList, sumPrice));
        // 支付信息
        List<OrderPay> orderPays = orderPayService.selectByOrderId(param.getOrderId());
        OrderOverviewVO billView = orderService.buildOrderOverview(order, orderDishesList).getData();
        array.addAll(paymentInfos(orderPays, sumPrice.doubleValue(), billView));
        // 二维码
        array.addAll(qrCode());

        JSONArray rs = new JSONArray();
        rs.addAll(array);
        return rs;
    }

    private List<JSONObject> orderDishesTitle() {
        List<JSONObject> titles = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "商品名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "商品名称");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        titles.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品单价");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "单价");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 19);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        titles.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "数量");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "数量");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 4);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        titles.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "小计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "小计");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 4);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        titles.add(jsonObject);

        return titles;
    }

    private JSONObject simpleLineText(String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "simple-text-name");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", text);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        return jsonObject;
    }

    private JSONObject crlf() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "换行");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        return jsonObject;
    }

    private JSONObject dotLine() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        return jsonObject;
    }

    private List<JSONObject> dishesItems(String title, List<OrderDishes> orderDishesList, DoubleAdder sumAdder) {
        List<JSONObject> list = new ArrayList<>();

        list.add(simpleLineText(title));
        list.addAll(orderDishesTitle()); // 菜品标题
        list.add(dotLine());

        JSONObject details = new JSONObject();
        list.add(details);
        details.put("Name", "结账菜单表");
        details.put("ComType", EnumComType.TABLE.type);
        details.put("Size", 1);
        details.put("FrontEnterNum", 0);
        details.put("BehindEnterNum", 1);
        // 标题
        details.put("columnNames", asArray("", "", "", ""));
        // 间隔
        details.put("columnWidths", asArray(50, 15, 15, 20));
        // 对齐方式
        JSONArray columnAligns = asArray(
                EnumAlign.LEFT.type, EnumAlign.RIGHT.type,
                EnumAlign.RIGHT.type, EnumAlign.RIGHT.type);
        details.put("columnAligns", columnAligns);
        // 内容
        List<Integer> dishesIds = CommonUtils.collect(orderDishesList, OrderDishes::getDishesId);
        Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIds);
        double sumPrices = 0;
        JSONArray rows = new JSONArray();
        for (OrderDishes orderDishes : orderDishesList) {
            Dishes dishes = dishesMap.get(orderDishes.getDishesId());
            sumPrices += orderDishes.getOrderDishesPrice();
            rows.add(asArray(
                    dishes.getDishesName(),
                    formatMoney(orderDishes.getOrderDishesPrice()),
                    orderDishes.getOrderDishesNums(),
                    orderDishes.getOrderDishesPrice()));
        }
        details.put("rows", rows);

        // 分隔符
        list.add(dotLine());
        // 总计
        JSONObject sumItem = new JSONObject();
        sumItem.put("Name", title + "合计");
        sumItem.put("ComType", EnumComType.TEXT.type);
        sumItem.put("SampleContent", title + "合计:" + formatMoney(sumPrices));
        sumItem.put("Size", 1);
        sumItem.put("FrontLen", 22);
        sumItem.put("BehindLen", 0);
        sumItem.put("FrontEnterNum", 0);
        sumItem.put("BehindEnterNum", 1);
        list.add(sumItem);

        // 累计总价
        sumAdder.add(sumPrices);
        return list;
    }

    private List<JSONObject> paymentInfos(
            List<OrderPay> orderPays,
            double sumPrice,
            OrderOverviewVO bill) {
        if (bill == null) {
            bill = new OrderOverviewVO();
        }
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "订单合计值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "订单合计:" + formatMoney(sumPrice));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        list.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "已付");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "已付:" + formatMoney(bill.getOrderHadpaid()));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        list.add(jsonObject);
        // 分类统计
        Map<Integer, List<OrderPay>> orderPayGroup = CommonUtils.groupBy(orderPays, OrderPay::getPaymentMethod);
        for (Map.Entry<Integer, List<OrderPay>> entry : orderPayGroup.entrySet()) {
            double sum = 0;
            for (OrderPay pay : entry.getValue()) {
                sum += pay.getAmount();
            }
            EnumPayMethod payMethod = EnumPayMethod.of(entry.getKey());
            if (payMethod == null) {
                payMethod = EnumPayMethod.UNKNOWN;
            }
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+" + payMethod.name);
            jsonObject.put("SampleContent", "+" + payMethod.name + ":" + formatMoney(sum));
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", EnumComType.TEXT.type);
            list.add(jsonObject);
        }

        jsonObject = new JSONObject();
        jsonObject.put("Name", "抹零");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "抹零:" + formatMoney(bill.getOrderErase()));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        list.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "折扣合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "折扣合计:" + formatMoney(bill.getDiscountAmount()));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        list.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "应收合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "应收合计:" + formatMoney(bill.getOrderNeedPay()));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        list.add(jsonObject);

        return list;
    }

    private List<JSONObject> qrCode() {
        List<JSONObject> qrcode = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "双二维码");
        jsonObject.put("ComType", EnumComType.QRCODE2.type);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonObject.put("Width", 560);
        jsonObject.put("Height", 260);
        jsonObject.put("QrWidth", 250);
        jsonObject.put("LeftPadding1", 30);
        jsonObject.put("LeftPadding2", 30);
        jsonObject.put("Text1", "http://www.xiaojuhao.org/pay/?d=");
        jsonObject.put("Text2", "LocalServerConfig.publicAddress");
        qrcode.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "微信扫一扫");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "微信扫一扫,加菜,结账");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 3);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        qrcode.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "关注微信公众号");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "关注微信公众号");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 6);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        qrcode.add(jsonObject);
        return qrcode;
    }

    private JSONArray asArray(Object... vals) {
        JSONArray array = new JSONArray();
        array.addAll(Arrays.asList(vals));
        return array;
    }

    private String formatMoney(double val) {
        return CommonUtils.formatMoney(val, "0.0");
    }
}
