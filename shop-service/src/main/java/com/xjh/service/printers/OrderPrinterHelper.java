package com.xjh.service.printers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.*;
import com.xjh.common.model.ConfigurationBO;
import com.xjh.common.model.DeskOrderParam;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.DeskKeyDAO;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.dao.mapper.PrinterDishDAO;
import com.xjh.service.domain.*;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.service.printers.models.TextModel;
import com.xjh.service.remote.RemoteService;
import com.xjh.service.vo.PrePaidCard;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.hutool.core.util.NumberUtil.mul;
import static com.xjh.common.enumeration.EnumPayMethod.*;
import static com.xjh.common.utils.CommonUtils.formatScale;
import static com.xjh.common.utils.CommonUtils.tryDecodeBase64;

@Singleton
public class OrderPrinterHelper {
    @Inject
    OrderService orderService;
    @Inject
    DeskKeyDAO deskKeyDAO;
    @Inject
    RemoteService remoteService;
    @Inject
    ConfigService configService;
    @Inject
    OrderDishesService orderDishesService;
    @Inject
    StoreService storeService;
    @Inject
    DeskService deskService;
    @Inject
    DishesService dishesService;
    @Inject
    DishesPackageService dishesPackageService;
    @Inject
    DishesPriceDAO dishesPriceDAO;
    @Inject
    OrderPayService orderPayService;
    @Inject
    PrinterDishDAO printerDishDAO;

    public List<Object> buildOrderPrintData(DeskOrderParam param) {
        // 可折扣的菜品信息
        Predicate<OrderDishes> discountableChecker = orderDishesService.discountableChecker();
        List<Object> array = new ArrayList<>();
        Order order = orderService.getOrder(param.getOrderId());
        StoreVO store = storeService.getStore().getData();
        Desk desk = deskService.getById(order.getDeskId());
        array.add(simpleLineText("订单编号:" + order.getCreateTime() + "" + order.getOrderId()));
        array.add(simpleLineText("门店名称:" + store.getName()));
        array.add(simpleLineText("开台时间:" + DateBuilder.base(order.getCreateTime()).timeStr()));
        array.add(simpleLineText("桌台信息: 小句号料理-" + desk.getDeskName()));
        array.add(simpleLineText("用餐人数:" + order.getOrderCustomerNums()));
        array.add(simpleLineText("点菜员:点菜员1"));
        array.add(crlf()); // 换行
        // 订单菜品
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(param.getOrderId());
        // 过滤退菜记录
        orderDishesList = CommonUtils.filter(orderDishesList, OrderDishes.isReturnDishes().negate());

        List<OrderDishes> discountableList = CommonUtils.filter(orderDishesList, discountableChecker);
        List<OrderDishes> nonDiscountableList = CommonUtils.filter(orderDishesList, discountableChecker.negate());
        DoubleAdder sumPrice = new DoubleAdder();
        array.addAll(dishesItems("普通菜品", nonDiscountableList, sumPrice));
        array.add(crlf()); // 换行
        array.addAll(dishesItems("特价菜及酒水", discountableList, sumPrice));
        // 支付信息
        List<OrderPay> orderPays = orderPayService.selectByOrderId(param.getOrderId());
        OrderOverviewVO billView = orderService.buildOrderOverview(order, orderDishesList, orderPays).getData();
        array.addAll(paymentInfos(orderPays, sumPrice.doubleValue(), billView));
        // 二维码
        array.addAll(qrCode(store, desk));

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

    private TextModel simpleLineText(String text) {
        return TextModel.builder().name("一行简单文本").comType(EnumComType.TEXT.type).sampleContent(text).size(1).behindEnterNum(1).build();
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
//        return TextModel.builder()
//                .name("换行")
//                .comType(EnumComType.TEXT.type)
//                .size(1)
//                .sampleContent("")
//                .behindEnterNum(1)
//                .build();
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

    private List<Object> dishesItems(String title, List<OrderDishes> orderDishesList, DoubleAdder sumAdder) {
        List<Object> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderDishesList)) {
            return list;
        }
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
        JSONArray columnAligns = asArray(EnumAlign.LEFT.type, EnumAlign.RIGHT.type, EnumAlign.RIGHT.type, EnumAlign.RIGHT.type);
        details.put("columnAligns", columnAligns);
        // 内容
        List<Integer> dishesIds = CommonUtils.collect(orderDishesList, OrderDishes::getDishesId);
        Map<Integer, Dishes> dishesMap = dishesService.getByIdsAsMap(dishesIds);
        double sumPrices = 0;
        JSONArray rows = new JSONArray();
        for (OrderDishes orderDishes : orderDishesList) {
            Dishes dishes = dishesMap.get(orderDishes.getDishesId());
            sumPrices += orderDishes.sumOrderDishesPrice();
            rows.add(asArray(dishes.getDishesName(), formatMoney(orderDishes.sumOrderDishesPrice()), orderDishes.getOrderDishesNums(), orderDishes.sumOrderDishesPrice()));
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

    private List<JSONObject> paymentInfos(List<OrderPay> orderPays, double sumPrice, OrderOverviewVO bill) {
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

    private List<JSONObject> qrCode(StoreVO store, Desk desk) {
        ConfigurationBO cfg = configService.loadConfiguration();
        DeskKey deskKey = deskKeyDAO.getByDeskId(desk.getDeskId());
        String deskKeyString = deskKey != null ? deskKey.getDeskKey() : "";
        String deskCode = LocalDeskCode.getDeskCode(desk.getDeskName());

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
        jsonObject.put("Text1", cfg.getTickedUrl() + "?d=" + deskCode + "&s=" + store.getStoreId() + "&k=" + deskKeyString);
        jsonObject.put("Text2", cfg.getPublicAddress());
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


    public List<Object> buildKitchenPrintData0(Order order, SubOrder subOrder, List<OrderDishes> subOrderContainDishes) {
        Desk desk = deskService.getById(order.getDeskId());

        List<Object> data = new ArrayList<>();

        JSONArray data0 = getOrderJsonArray80(subOrder, desk, subOrderContainDishes);
        data.addAll(data0);

        return data;

    }
    public List<Object> buildKitchenPrintData(Order order, SubOrder subOrder, OrderDishes orderDishes, Dishes dishes) {
        Desk desk = deskService.getById(order.getDeskId());

        List<Object> data = new ArrayList<>();

        boolean addDishesFlag = false;
        Result<List<SubOrder>> subOrders = orderService.findSubOrders(order.getOrderId());
        if(CommonUtils.sizeOf(subOrders.getData()) > 1){
            addDishesFlag = true;
        }

        JSONArray jSONArray = getOrderJsonArray80(subOrder,
                orderDishes, dishes,
                0, desk, addDishesFlag,
                dishes.getDishesName(),
                null,
                false);
        data.addAll(jSONArray);
        return data;
    }


    private JSONArray getOrderJsonArray80(SubOrder subOrder, Desk desk, List<OrderDishes> subOrderContainDishes) {
        StoreVO store = storeService.getStore().getData();
        Integer storeId = store.getStoreId();
        ConfigurationBO cfg = configService.loadConfiguration();
        JSONArray jsonArray = new JSONArray();

        Order order = orderService.getOrder(subOrder.getOrderId());

        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(order.getOrderId());
        List<OrderDishes> orderDishesNoReteurn = orderDishesList.stream().filter(OrderDishes.isReturnDishes().negate()).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "桌台:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 4);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);
//        System.out.println("api.print.task.PrintTaskOrderSample.getOrderJsonArray()" + jsonArray.toString());

        jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", desk.getBelongDeskType().getTypeName() + "-" + desk.getDeskName());
        jsonObject.put("SampleContent", desk.getDeskName());
        jsonObject.put("Size", 2);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线1");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "单号");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "单号:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "单号值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", subOrder.getSerialNumber());
        jsonObject.put("SampleContent", subOrder.getCreatetime() + subOrder.getSubOrderId());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "点菜员");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "点菜员:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "点菜员值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", subOrder.getSubOrderAccount().getAccountNickName());
        jsonObject.put("SampleContent", CurrentAccount.currentAccountCode());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "下单时间");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "下单时间:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "下单时间值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", DateBuilder.base(subOrder.getCreatetime()).timeStr());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "订单总金额");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "订单总金额:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);


        jsonObject = new JSONObject();
        jsonObject.put("Name", "订单总金额值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", orderManager.getOrderDishesTotalPriceExcludeReturn(order.getOrderId()) + "");
        jsonObject.put("SampleContent", orderService.sumBillAmount(orderDishesNoReteurn));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "商品名称");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "数量");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "数量");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 24);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "单价");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "单价");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 6);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线2");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        //表添加模块
        jsonObject = new JSONObject();
        jsonObject.put("Name", "点菜菜单表");
        jsonObject.put("ComType", EnumComType.TABLE.type);

        JSONArray columnNames = new JSONArray();
        columnNames.add("");
        columnNames.add("");
        columnNames.add("");
        jsonObject.put("columnNames", columnNames);

        JSONArray columnWidths = new JSONArray();
        columnWidths.add(33);
        columnWidths.add(6);
        columnWidths.add(11);
        jsonObject.put("columnWidths", columnWidths);

        JSONArray rows = new JSONArray();

        //获取合并菜
        // List<OrderDishes> subOrderContainDishes = orderManager.getSubOrderDishesesMerge(subOrder.getSubOrderId());
//        List<OrderDishes> subOrderContainDishes = subOrder.getSubOrderContainDishes();
        for(OrderDishes orderDishes : subOrderContainDishes ){
            // DishesPrice dishesPrice = dishesManager.getDishesPriceOne(orderDishes.getDishesPriceId());
            DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
            String dishesPriceString = "";
            if (dishesPrice != null)
            {
                dishesPriceString = "(" + dishesPrice.getDishesPriceName() + ")";
            }
            // if (orderDishes.getIfDishesPackage() == OrderDishes.ORDER_TYPE_NO_DISHESPACKAGE)
            if (orderDishes.getIfDishesPackage() == EnumIsPackage.NO.code)
            {
                Dishes dishes = dishesService.getById(orderDishes.getDishesId());
                if(dishes.getIfNeedPrint() == 1){
                    JSONArray oneRow = new JSONArray();
                    String orderDishesDetailedName = "";
                    if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.SEND.type)
                    {
                        orderDishesDetailedName += "(赠)";
                    }
                    else if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.TASTED.type)
                    {
                        orderDishesDetailedName += "(试吃)";
                    }
                    // orderDishesDetailedName += dishes.getDishesName() + orderDishes.getOrderDishesOptions() + dishesPriceString;
                    orderDishesDetailedName += dishes.getDishesName() + dishesPriceString;
                    oneRow.add(orderDishesDetailedName);
                    oneRow.add(orderDishes.getOrderDishesNums() + "");
                    oneRow.add(formatMoney((double)orderDishes.getOrderDishesPrice()) + "");
                    rows.add(oneRow);
                }
            }
            else if(orderDishes.getIfDishesPackage() == 2)
            {
                // DishesPackageNew orderDishesPackage = dishesPackageManagerNew.getOneDishesPackages(orderDishes.getDishesId());
                DishesPackage dishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                JSONArray oneRow = new JSONArray();
                oneRow.add("(套)" + dishesPackage.getDishesPackageName());
                oneRow.add(orderDishes.getOrderDishesNums() + "");
                oneRow.add(formatMoney((double)orderDishes.getOrderDishesPrice()) + "");
                rows.add(oneRow);
                List<OrderPackageDishes> orderPackageDisheses = new ArrayList<>();//orderpa.getByDishesPackageId(orderDishes.getOrderDishesId());
                for (OrderPackageDishes orderPackageDishes : orderPackageDisheses)
                {
                    Dishes dishes = dishesService.getById(orderPackageDishes.getDishesid());
                    String dishesPriceSonString = "";
                    if (orderPackageDishes.getDishespriceid()>0)
                    {
                        DishesPrice dishesPriceSon = dishesPriceDAO.queryByPriceId(orderPackageDishes.getDishespriceid());

                        if (dishesPriceSon != null)
                        {
                            dishesPriceSonString = "(" + dishesPriceSon.getDishesPriceName() + ")";
                        }
                    }
                    JSONArray oneRow1 = new JSONArray();
                    oneRow1.add(dishes.getDishesName() + orderPackageDishes.getOrderDishesOptionsString() + dishesPriceSonString);
                    oneRow1.add("");
                    oneRow1.add("");
                    rows.add(oneRow1);
                }
            }
            else
            {
                DishesPackage orderDishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                JSONArray oneRow = new JSONArray();
                oneRow.add("(套)" + orderDishesPackage.getDishesPackageName());
                oneRow.add(orderDishes.getOrderDishesNums() + "");
                oneRow.add(formatMoney((double)orderDishes.getOrderDishesPrice()) + "");
                rows.add(oneRow);
                List<Dishes> disheses = dishesPackageService.queryPackageDishes(orderDishesPackage.getDishesPackageId());
                for (Dishes dishes : disheses)
                {
                    JSONArray oneRow1 = new JSONArray();
                    oneRow1.add(dishes.getDishesName());
                    oneRow1.add("");
                    oneRow1.add("");
                    rows.add(oneRow1);
                }
            }

        }
        jsonObject.put("rows", rows);

        JSONArray columnAligns = new JSONArray();
        columnAligns.add(EnumAlign.LEFT.type);
        columnAligns.add(EnumAlign.CENTER.type);
        columnAligns.add(EnumAlign.RIGHT.type);
        jsonObject.put("columnAligns", columnAligns);

        jsonObject.put("Size", 2);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线3");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        DeskKey deskKey = deskKeyDAO.getByDeskId(desk.getDeskId());
        String deskKeyString = deskKey != null ? deskKey.getDeskKey() : "";
        String deskCode = LocalDeskCode.getDeskCode(desk.getDeskName());

        jsonObject = new JSONObject();
        jsonObject.put("Name", "双二维码");
        jsonObject.put("ComType", EnumComType.QRCODE2.type);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonObject.put("Width", 560);
        jsonObject.put("Height", 260);
        jsonObject.put("QrWidth", 250);
        jsonObject.put("LeftPadding1", 30);
        jsonObject.put("LeftPadding2", 30);
        jsonObject.put("Text1", cfg.getTickedUrl()+"?d="+deskCode+"&s="+storeId+"&k="+deskKeyString);
        jsonObject.put("Text2", cfg.getPublicAddress());
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "微信扫一扫");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "微信扫一扫,加菜,结账");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 3);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "关注微信公众号");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "关注微信公众号");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 6);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    public JSONArray getOrderJsonArray80(SubOrder subOrder,
                                          OrderDishes orderDishes,
                                          Dishes dishes, int i,
                                          Desk desk,
                                          boolean addDishesFlag,
                                          String realDishesName,
                                          Desk oldDesk,
                                          boolean ifChangeDesk) {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "桌台信息:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);
//        System.out.println("api.print.task.PrintTaskOrderSample.getOrderJsonArray()" + jsonArray.toString());

        jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        if (ifChangeDesk) {
            jsonObject.put("SampleContent", "小句号料理-"+oldDesk.getDeskName() + "-转到-" + desk.getDeskName());
        } else {
            // jsonObject.put("SampleContent", desk.getBelongDeskType().getTypeName() + "-" + desk.getDeskName());
            jsonObject.put("SampleContent", desk.getDeskName());
        }
        jsonObject.put("Size", 2);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "名称:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 1);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品名称值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        String orderDishesDetailedName = "";
        if (addDishesFlag) {
            orderDishesDetailedName += "(加)";
        }
        if (ifChangeDesk) {
            orderDishesDetailedName += "(复)";
        }
        Integer isPackage = OrElse.orGet(orderDishes.getIfDishesPackage(), EnumIsPackage.NO.code);
        if(isPackage == EnumIsPackage.YES_NEW.code || isPackage == EnumIsPackage.YES.code) {
            orderDishesDetailedName += "(套)";
        }
        if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.SEND.type)
        {
            orderDishesDetailedName += "(赠）";
        }
        else if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.TASTED.type)
        {
            orderDishesDetailedName += "(试吃）";
        }
        orderDishesDetailedName += realDishesName;
        DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
        if (dishesPrice != null)
        {
            orderDishesDetailedName += "(" + dishesPrice.getDishesPriceName() + ")";
        }
        jsonObject.put("SampleContent", orderDishesDetailedName);
        jsonObject.put("Size", 2);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 2);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "下单时间");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "下单时间:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "下单时间值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", DateBuilder.base(subOrder.getCreatetime()).timeStr());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "打印队列时间");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "打印队列时间:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "打印队列时间值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", DateBuilder.now().timeStr());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

//        {"Size":20,"Content":"二维码样例内容","ComType":4,"FrontEnterNum":0,"Name":"5","BehindEnterNum":0}
//        jsonObject = new JSONObject();
//        jsonObject.put("Name", "二维码");
//        jsonObject.put("ComType", TicketCom.TYPE_QRCODE);
//        jsonObject.put("Content", "退菜地址");
//        jsonObject.put("Size", 50);
//        jsonObject.put("FrontEnterNum", 0);
//        jsonObject.put("BehindEnterNum", 1);
//        jsonArray.add(jsonObject);
        return jsonArray;
    }

    private OrderDishes getOrderDishExist(List<OrderDishes> orderDishesesMerge, OrderDishes orderDish) {
        loop1:
        for (int i = 0; i < orderDishesesMerge.size(); i++) {
            OrderDishes aod = orderDishesesMerge.get(i);
            if (aod.getOrderDishesSaletype() == orderDish.getOrderDishesSaletype() && aod.getIfDishesPackage() == orderDish.getIfDishesPackage() && aod.getDishesId() == orderDish.getDishesId() && Math.abs(aod.getOrderDishesPrice() - orderDish.getOrderDishesPrice()) < 0.001 && Math.abs(aod.getOrderDishesDiscountPrice() - orderDish.getOrderDishesDiscountPrice()) < 0.001) {

                if (orderDish.getIfDishesPackage() == EnumIsPackage.YES.code || orderDish.getIfDishesPackage() == EnumIsPackage.YES_NEW.code) {
                    return aod;
                } else {
//                    JSONObject dishesAttributeA = dishAttributeConvert(aod.getDishesAttributes());
//                    JSONObject dishesAttributeB = dishAttributeConvert(orderDish.getDishesAttributes());
//                    Set<String> keySetA = dishesAttributeA.keySet();
//                    Set<String> keySetB = dishesAttributeB.keySet();
//                    if (keySetA.size() == keySetB.size())
//                    {
//                        loop2:
//                        for (String key : keySetA)
//                        {
//                            //B没有这个属性，或者属性值不匹配，则要重新寻找
//                            if (!(dishesAttributeB.containsKey(key) && dishesAttributeA.getString(key).equals(dishesAttributeB.getString(key))))
//                            {
//                                continue loop1;
//                            }
//                        }
//                        return aod;
//                    }
                }
            }
        }
        return null;
    }

    private List<OrderDishes> sortOrderDish(List<OrderDishes> orderDishesesOrign) {
        List<OrderDishes> orderDishesMerge = new ArrayList<>();
        for (int i = 0; i < orderDishesesOrign.size(); i++) {
            OrderDishes aod = orderDishesesOrign.get(i);
            OrderDishes orderDishExist = getOrderDishExist(orderDishesMerge, aod);
            if (orderDishExist != null) {
//                OrderDishesDefault orderDishExist2 = (OrderDishesDefault) orderDishExist;
//                orderDishExist2.setOrderDishesNums(orderDishExist.getOrderDishesNums() + aod.getOrderDishesNums());
            } else {
                orderDishesMerge.add(aod);
            }
        }
        return orderDishesMerge;
    }

    private static String resolveOrderDishesOptions(String attr){
        if(CommonUtils.isBlank(attr)){
            return "";
        }
        String str = attr;
        try{
            str = tryDecodeBase64(attr);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if(str.startsWith("[")) {
            JSONArray arr = JSONArray.parseArray(str);
            StringBuilder s = new StringBuilder();
            for(Object obj : arr){
                if(s.length() > 0){
                    s.append(",");
                }
                s.append(obj.toString());
            }
            return s.toString();
        }
        return str;
    }

    public JSONArray buildCheckOutPrintData(Order order) {
        ConfigurationBO cfg = configService.loadConfiguration();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        jsonObject = new JSONObject();
        jsonObject.put("Name", "订单编号");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "订单编号:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "单号值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", order.getCreateTime() + order.getOrderId());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "门店名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "门店名称:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        StoreVO store = storeService.getStore().getData();
        jsonObject = new JSONObject();
        jsonObject.put("Name", "门店名称值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", store.getName());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "开台时间");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "开台时间:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "开台时间值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", DateBuilder.base(order.getCreateTime()).timeStr());
//        jsonObject.put("SampleContent", "dkkdkdkkdkdjjfj");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台信息");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "桌台信息:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        Desk orderDesk = deskService.getById(order.getDeskId());
        jsonObject = new JSONObject();
        jsonObject.put("Name", "桌台信息值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", orderDesk.getBelongDeskType().getTypeName() + "-" + orderDesk.getDeskName());
        jsonObject.put("SampleContent", "小句号料理-"+orderDesk.getDeskName());
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "用餐人数");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "用餐人数:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "用餐人数值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", order.getOrderCustomerNums() + "");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "点菜员");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "点菜员:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "点菜员值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", order.getOrderAccount().getAccountNickName());
        jsonObject.put("SampleContent", "点菜员哈哈");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 2);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "普通菜品");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "普通菜品");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "商品名称");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "商品单价");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "单价");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 19);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "数量");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "数量");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 4);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "小计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "小计");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 4);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线1");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);
        ////////////////////////////////////////////////////////////////

        //表添加模块
        jsonObject = new JSONObject();
        jsonObject.put("Name", "结账菜单表");
        jsonObject.put("ComType", EnumComType.TABLE.type);

        JSONArray columnNames = new JSONArray();
        columnNames.add("");
        columnNames.add("");
        columnNames.add("");
        columnNames.add("");
        jsonObject.put("columnNames", columnNames);

        JSONArray columnWidths = new JSONArray();
        columnWidths.add(50);
        columnWidths.add(15);
        columnWidths.add(15);
        columnWidths.add(20);
        jsonObject.put("columnWidths", columnWidths);

        JSONArray rows = new JSONArray();
        // List<OrderDishes> orderDishesesMergesExcludeReturn = orderManager.getOrderDishesesMergeExcludeReturn(order.getOrderId());
        List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(order.getOrderId());
        List<OrderDishes> orderDishesesMergesExcludeReturn = orderDishesList.stream().filter(OrderDishes.isReturnDishes().negate()).collect(Collectors.toList());
        orderDishesesMergesExcludeReturn = sortOrderDish(orderDishesesMergesExcludeReturn);//对所有相同的菜进行合并，不管他们有没有设置合并
        boolean hasNoDiscountDishesFalg = false;  //不打折菜标记
        double discountDishesSum = 0;  //打折菜总计
        double noDiscountDishesSum = 0;
        Set<Integer> discountableDishes = storeService.getStoreDiscountableDishesIds();
        for (OrderDishes orderDishes : orderDishesesMergesExcludeReturn) {
//            DishesPrice dishesPrice = dishesManager.getDishesPriceOne(orderDishes.getDishesPriceId());
            DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
            Dishes dishes = dishesService.getById(orderDishes.getDishesId());
            String dishesPriceString = "";
            if (dishesPrice != null) {
                dishesPriceString = "(" + dishesPrice.getDishesPriceName() + ")";
            }
            if (orderDishes.getIfDishesPackage() == EnumIsPackage.NO.code) {
                if (discountableDishes.contains(orderDishes.getDishesId()) == false) {
                    hasNoDiscountDishesFalg = true;
                } else {
                    JSONArray oneRow = new JSONArray();
                    String orderDishesDetailedName = "";
                    if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.SEND.type) {
                        orderDishesDetailedName += "(赠)";
                    } else if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.TASTED.type) {
                        orderDishesDetailedName += "(试吃)";
                    }
                    orderDishesDetailedName += dishes.getDishesName() + resolveOrderDishesOptions(orderDishes.getOrderDishesOptions()) + dishesPriceString;
                    if (orderDishesDetailedName.length() > 12) {
                        orderDishesDetailedName = orderDishesDetailedName.substring(0, 12);
                    }
                    oneRow.add(orderDishesDetailedName);
                    oneRow.add(orderDishes.getOrderDishesPrice() + "");
                    oneRow.add(orderDishes.getOrderDishesNums() + "");
                    oneRow.add(formatMoney(mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue()));
                    rows.add(oneRow);
                    discountDishesSum += mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue();
                }

            } else {
                hasNoDiscountDishesFalg = true;
            }

        }
        jsonObject.put("rows", rows);

        JSONArray columnAligns = new JSONArray();
        columnAligns.add(EnumAlign.LEFT.type);
        columnAligns.add(EnumAlign.RIGHT.type);
        columnAligns.add(EnumAlign.RIGHT.type);
        columnAligns.add(EnumAlign.RIGHT.type);
        jsonObject.put("columnAligns", columnAligns);

        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

//////////////////////////////////////////////////////////////
        jsonObject = new JSONObject();
        jsonObject.put("Name", "分割线2");
        jsonObject.put("ComType", EnumComType.LINE.type);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "普通菜品合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "普通菜品合计:" + formatScale(discountDishesSum, 2));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 27);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 2);
        jsonArray.add(jsonObject);

        //如果有不打折的菜或者套餐的话，就要有常规菜的表
        if (hasNoDiscountDishesFalg) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "特价菜以及酒水");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "特价菜以及酒水");
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "商品名称2");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "商品名称");
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "商品单价2");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "单价");
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 19);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "数量2");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "数量");
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "小计2");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "小计");
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "分割线3");
            jsonObject.put("ComType", EnumComType.LINE.type);
            jsonObject.put("Size", 1);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);
            ////////////////////////////////////////////////////////////////

            //表添加模块
            jsonObject = new JSONObject();
            jsonObject.put("Name", "结账菜单表2");
            jsonObject.put("ComType", EnumComType.TABLE.type);

            JSONArray columnNames2 = new JSONArray();
            columnNames2.add("");
            columnNames2.add("");
            columnNames2.add("");
            columnNames2.add("");
            jsonObject.put("columnNames", columnNames2);

            JSONArray columnWidths2 = new JSONArray();
            columnWidths2.add(50);
            columnWidths2.add(15);
            columnWidths2.add(15);
            columnWidths2.add(20);
            jsonObject.put("columnWidths", columnWidths2);

            JSONArray rows2 = new JSONArray();
//            List<OrderDishes> orderDishesesMergesExcludeReturn2 = orderManager.getOrderDishesesMergeExcludeReturn(order.getOrderId());
//            orderDishesesMergesExcludeReturn2 = sortOrderDish(orderDishesesMergesExcludeReturn2);
            List<OrderDishes> orderDishesList2 = orderDishesService.selectByOrderId(order.getOrderId());
            List<OrderDishes> orderDishesesMergesExcludeReturn2 = orderDishesList.stream().filter(OrderDishes.isReturnDishes().negate()).collect(Collectors.toList());
            orderDishesesMergesExcludeReturn2 = sortOrderDish(orderDishesesMergesExcludeReturn);//对所有相同的菜进行合并，不管他们有没有设置合并
            for (OrderDishes orderDishes : orderDishesesMergesExcludeReturn2) {
                // DishesPrice dishesPrice = dishesManager.getDishesPriceOne(orderDishes.getDishesPriceId());
                DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
                Dishes dishes = dishesService.getById(orderDishes.getDishesId());
                String dishesPriceString = "";
                if (dishesPrice != null) {
                    dishesPriceString = "(" + dishesPrice.getDishesPriceName() + ")";
                }
                if (orderDishes.getIfDishesPackage() == EnumIsPackage.NO.code) {
                    if (discountableDishes.contains(orderDishes.getDishesId()) == false) {
                        JSONArray oneRow = new JSONArray();
                        String orderDishesDetailedName = "";
                        if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.SEND.type) {
                            orderDishesDetailedName += "(赠)";
                        } else if (orderDishes.getOrderDishesSaletype() == EnumOrderSaleType.TASTED.type) {
                            orderDishesDetailedName += "(试吃)";
                        }
                        orderDishesDetailedName += dishes.getDishesName() + resolveOrderDishesOptions(orderDishes.getOrderDishesOptions()) + dishesPriceString;
                        if (orderDishesDetailedName.length() > 12) {
                            orderDishesDetailedName = orderDishesDetailedName.substring(0, 12);
                        }
                        oneRow.add(orderDishesDetailedName);
                        oneRow.add(orderDishes.getOrderDishesPrice() + "");
                        oneRow.add(orderDishes.getOrderDishesNums() + "");
                        // oneRow.add((Arith.round(Arith.mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()), 2)) + "");
                        oneRow.add((formatScale(mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue(), 2)) + "");
                        rows2.add(oneRow);
                        noDiscountDishesSum += mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue();
                    }
                } else if (orderDishes.getIfDishesPackage() == EnumIsPackage.YES_NEW.code) {
//                    DishesPackageNew dishesPackage = dishesPackageManagerNew.getOneDishesPackages(orderDishes.getDishesId());
                    DishesPackage dishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                    JSONArray oneRow = new JSONArray();
                    String orderDishesDetailedName = "(套)";
                    orderDishesDetailedName += dishesPackage.getDishesPackageName();
                    if (orderDishesDetailedName.length() > 12) {
                        orderDishesDetailedName = orderDishesDetailedName.substring(0, 12);
                    }
                    oneRow.add(orderDishesDetailedName);
                    oneRow.add(orderDishes.getOrderDishesPrice() + "");
                    oneRow.add(orderDishes.getOrderDishesNums() + "");
                    oneRow.add((formatScale(mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue(), 2)) + "");
                    rows2.add(oneRow);

//                    List<OrderPackageDishes> orderPackageDisheses = orderManager.getOrderPackageDisheses(orderDishes.getOrderDishesId());
                    List<OrderPackageDishes> orderPackageDisheses = new ArrayList<>();
                    for (OrderPackageDishes orderPackageDishes : orderPackageDisheses) {
                        Dishes dishes2 = dishesService.getById(orderPackageDishes.getDishesid());
                        String dishesPriceSonString = "";
                        if (orderPackageDishes.getDishespriceid() > 0) {
                            DishesPrice dishesPriceSon = dishesPriceDAO.queryByPriceId(orderPackageDishes.getDishespriceid());

                            if (dishesPriceSon != null) {
                                dishesPriceSonString = "(" + dishesPriceSon.getDishesPriceName() + ")";
                            }
                        }
                        JSONArray oneRow1 = new JSONArray();
                        oneRow1.add(dishes2.getDishesName() + orderPackageDishes.getOrderDishesOptionsString() + dishesPriceSonString);
                        oneRow1.add("");
                        oneRow1.add("");
                        oneRow1.add("");
                        rows2.add(oneRow1);
                    }
                    noDiscountDishesSum += mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue();
                } else {
                    // DishesPackage dishesPackage = orderDishes.getOrderDishesPackage();
                    DishesPackage dishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                    JSONArray oneRow = new JSONArray();
                    String orderDishesDetailedName = "(套)";
                    orderDishesDetailedName += dishesPackage.getDishesPackageName();
                    if (orderDishesDetailedName.length() > 12) {
                        orderDishesDetailedName = orderDishesDetailedName.substring(0, 12);
                    }
                    oneRow.add(orderDishesDetailedName);
                    oneRow.add(orderDishes.getOrderDishesPrice() + "");
                    oneRow.add(orderDishes.getOrderDishesNums() + "");
                    oneRow.add((formatScale(mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue(), 2)) + "");
                    rows2.add(oneRow);

                    // List<Dishes> disheses = dishesPackage.getDisheses();
                    List<Dishes> disheses = dishesPackageService.queryPackageDishes(dishesPackage.getDishesPackageId());
                    for (Dishes dishes3 : disheses) {
                        JSONArray oneRow1 = new JSONArray();
                        oneRow1.add(dishes3.getDishesName());
                        oneRow1.add("");
                        oneRow1.add("");
                        oneRow1.add("");
                        rows2.add(oneRow1);
                    }
                    noDiscountDishesSum += mul(orderDishes.getOrderDishesPrice(), orderDishes.getOrderDishesNums()).doubleValue();
                }
            }
            jsonObject.put("rows", rows2);

            JSONArray columnAligns2 = new JSONArray();
            columnAligns2.add(EnumAlign.LEFT.type);
            columnAligns2.add(EnumAlign.RIGHT.type);
            columnAligns2.add(EnumAlign.RIGHT.type);
            columnAligns2.add(EnumAlign.RIGHT.type);
            jsonObject.put("columnAligns", columnAligns2);

            jsonObject.put("Size", 1);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            //////////////////////////////////////////////////////////////
            jsonObject = new JSONObject();
            jsonObject.put("Name", "分割线4");
            jsonObject.put("ComType", EnumComType.LINE.type);
            jsonObject.put("Size", 1);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "特价菜以及酒水合计");
            jsonObject.put("ComType", EnumComType.TEXT.type);
            jsonObject.put("SampleContent", "特价菜以及酒水合计:" + formatMoney(noDiscountDishesSum));
            jsonObject.put("Size", 1);
            jsonObject.put("FrontLen", 21);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 2);
            jsonArray.add(jsonObject);
        }

        jsonObject = new JSONObject();
        jsonObject.put("Name", "订单合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "订单合计:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "订单合计值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", orderService.sumBillOriAmount(orderDishesesMergesExcludeReturn));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "已付");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "已付:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "已付值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", order.getOrderHadpaid() + "");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        double methodCashTotal = 0; //现金总额
        double methodBankcardTotal = 0; //银行卡总额
        double methodCouponTotal = 0; //代金券总额
        double methodStoreCardTotal = 0; //储值卡总额
        double methodWechatTotal = 0; //微信总额
        double methodAlipayTotal = 0; //支付宝总额
        double methodMeituanTotal = 0; //美团券总额
        double methodKouBeiTotal = 0; //口碑商家
        double methodPublicSignalTotal = 0; //公众号
        double methodWechatCouponTotal = 0; //公众号优惠
        double methodStoreReduceTotal = 0; //店铺满减
        double methodPackageTotal = 0; //套餐买单总额
        double methodOtherTotal = 0; //其它支付总额
        double methodTinyLifeCouponTotal = 0;//微生活代金券
        double methodTinyLifeIntegralDeductionTotal = 0;//微生活积分抵扣
        double methodTinyLifeStorecardTotal = 0;//微生活储值卡
        double methodWeChatUnionpayPayMentTotal = 0;//微信银联支付

        int numMeituan = 0;
        int numKoubei = 0;

        //外卖
        double methodTakeOutTotal = 0;
        int numTakeOut = 0;
        //银联pos
        double methodUnionpayPosTotal = 0;
        //交行活动
        double methodTrafficActivitiesTotal = 0;
        int numTrafficActivities = 0;
        //招行活动
        double methodMerchantsActivitiesTotal = 0;
        int numMerchantsActivities = 0;
        //商场活动
        double methodMarketActivitiesTotal = 0;
        int numMarketActivities = 0;
        //美团闪惠
        double methodMeituanShanhuiTotal = 0;
        int numMeituanShanhui = 0;

        //新添加的东西---------begin
        List<OrderPay> orderPays = orderPayService.selectByOrderId(order.getOrderId());
        List<OrderPay> orderPaysPreCard = new ArrayList<>();//专门用来存储用储值卡支付过的记录
        for (int j = 0; j < orderPays.size(); j++) {
            OrderPay orderPay = orderPays.get(j);
            int paymentMethod = orderPay.getPaymentMethod();
            if (orderPay.getPaymentStatus() == EnumPayStatus.PAID.code) {
                if (paymentMethod == EnumPayMethod.CASH.code) {
                    methodCashTotal = methodCashTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.BANKCARD.code) {
                    methodBankcardTotal = methodBankcardTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.VOUCHER.code) {
                    methodCouponTotal = methodCouponTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.STORECARD.code) {
                    methodStoreCardTotal = methodStoreCardTotal + orderPay.getAmount();
                    orderPaysPreCard.add(orderPay);
                } else if (paymentMethod == EnumPayMethod.WECHAT.code) {
                    methodWechatTotal = methodWechatTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.ALIPAY.code) {
                    methodAlipayTotal = methodAlipayTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.MEITUAN_COUPON.code) {
                    methodMeituanTotal = methodMeituanTotal + orderPay.getAmount();
                    numMeituan = numMeituan + orderPay.getVoucherNums();
                } else if (paymentMethod == EnumPayMethod.KOUBEI.code) {
                    methodKouBeiTotal = methodKouBeiTotal + orderPay.getAmount();
                    numKoubei = numKoubei + orderPay.getVoucherNums();
                } else if (paymentMethod == EnumPayMethod.WECHAT_OFFICIAL.code) {
                    methodPublicSignalTotal = methodPublicSignalTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.WECHAT_COUPON.code) {
                    methodWechatCouponTotal = methodWechatCouponTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.STORE_REDUCTION.code) {
                    methodStoreReduceTotal = methodStoreReduceTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.MEITUAN_PACKAGE.code) {
                    methodPackageTotal = methodPackageTotal + orderPay.getAmount();
                } else if (paymentMethod == EnumPayMethod.OHTER.code) {
                    methodOtherTotal = methodOtherTotal + orderPay.getAmount();
                } else if (paymentMethod == TINY_LIFE_COUPON.code)//微生活代金券
                {
                    methodTinyLifeCouponTotal = methodTinyLifeCouponTotal + orderPay.getAmount();
                    ;
                } else if (paymentMethod == TINY_LIFE_INTEGRAL_DEDUCTION.code)//微生活积分抵扣
                {
                    methodTinyLifeIntegralDeductionTotal = methodTinyLifeIntegralDeductionTotal + orderPay.getAmount();
                } else if (paymentMethod == TINY_LIFE_STORECARD.code)//微生活储值卡
                {
                    methodTinyLifeStorecardTotal = methodTinyLifeStorecardTotal + orderPay.getAmount();
                } else if (paymentMethod == WECHAT_UNIONPAY_PAYMEN.code)//微信银联支付
                {
                    methodWeChatUnionpayPayMentTotal = methodWeChatUnionpayPayMentTotal + orderPay.getAmount();
                } else if (paymentMethod == 外卖.code)//外卖
                {
                    methodTakeOutTotal = methodTakeOutTotal + orderPay.getAmount();
                    numTakeOut = numTakeOut + orderPay.getVoucherNums();
                } else if (paymentMethod == UNIONPAY_POS.code)//银联pos
                {
                    methodUnionpayPosTotal = methodUnionpayPosTotal + orderPay.getAmount();
                } else if (paymentMethod == TRAFFIC_ACTIVITIES.code)//交行活动
                {
                    methodTrafficActivitiesTotal = methodTrafficActivitiesTotal + orderPay.getAmount();
                    numTrafficActivities = numTrafficActivities + orderPay.getVoucherNums();
                } else if (paymentMethod == MERCHANTS_ACTIVITIES.code)//招行活动
                {
                    methodMerchantsActivitiesTotal = methodMerchantsActivitiesTotal + orderPay.getAmount();
                    numMerchantsActivities = numMerchantsActivities + orderPay.getVoucherNums();
                } else if (paymentMethod == MARKET_ACTIVITIES.code)//商场活动
                {
                    methodMarketActivitiesTotal = methodMarketActivitiesTotal + orderPay.getAmount();
                    numMarketActivities = numMarketActivities + orderPay.getVoucherNums();
                } else if (paymentMethod == MEITUAN_SHANHUI.code)//美团闪惠
                {
                    methodMeituanShanhuiTotal = methodMeituanShanhuiTotal + orderPay.getAmount();
                    numMeituanShanhui = numMeituanShanhui + orderPay.getVoucherNums();
                }
            }
        }

        if (methodCashTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+现金");
            jsonObject.put("SampleContent", "+现金:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+现金值");
            jsonObject.put("SampleContent", formatMoney(methodCashTotal));
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }

        if (methodBankcardTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+银行卡");
            jsonObject.put("SampleContent", "+银行卡:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+银行卡值");
            jsonObject.put("SampleContent", formatMoney(methodBankcardTotal));
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodWechatTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微信");
            jsonObject.put("SampleContent", "+微信:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微信值");
            jsonObject.put("SampleContent", formatMoney(methodWechatTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodAlipayTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+支付宝");
            jsonObject.put("SampleContent", "+支付宝:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+支付宝值");
            jsonObject.put("SampleContent", formatMoney(methodAlipayTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodStoreCardTotal > 0) {
            String preCardValue = formatMoney(methodStoreCardTotal) + "";
            try {
                //如果有多张储值卡，多次支付的话，这里只显示1张储值卡的余额
                OrderPay orderPay1 = orderPaysPreCard.get(0);
//                CardManager cardManager = (CardManager) parameterPackage.getObject(CardManager.class.getName());
//                PrePaidCard prePaidCard = cardManager.getOnePrePaidCard(orderPay1.getCardNumber());
                PrePaidCard prePaidCard = remoteService.getOnePrePaidCard(store.getStoreId(), orderPay1.getCardNumber()).getData();
                if (prePaidCard != null) {
                    preCardValue += "(余额:" + prePaidCard.getBalance() + ")";
                }
            } catch (Exception e) {
                // parameterPackage.getLogManager().submitException(e);
                throw new RuntimeException(e);
            }

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+储值卡");
            jsonObject.put("SampleContent", "+储值卡:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+储值卡值");
            jsonObject.put("SampleContent", preCardValue);
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodMeituanTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+美团券");
            jsonObject.put("SampleContent", "+美团券:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+美团券值");
            jsonObject.put("SampleContent", formatMoney(methodMeituanTotal) + "(" + numMeituan + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodCouponTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+代金券");
            jsonObject.put("SampleContent", "+代金券:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+代金券值");
            jsonObject.put("SampleContent", formatMoney(methodCouponTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodKouBeiTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+口碑商家");
            jsonObject.put("SampleContent", "+口碑商家:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+口碑商家值");
            jsonObject.put("SampleContent", formatMoney(methodKouBeiTotal) + "(" + numKoubei + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodTakeOutTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+外卖");
            jsonObject.put("SampleContent", "+外卖:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+外卖值");
            jsonObject.put("SampleContent", formatMoney(methodTakeOutTotal) + "(" + numTakeOut + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodUnionpayPosTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+银联pos机");
            jsonObject.put("SampleContent", "+银联pos机:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+银联pos机值");
            jsonObject.put("SampleContent", formatMoney(methodUnionpayPosTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodTrafficActivitiesTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+交行活动");
            jsonObject.put("SampleContent", "+交行活动:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+交行活动值");
            jsonObject.put("SampleContent", formatMoney(methodTrafficActivitiesTotal) + "(" + numTrafficActivities + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodMerchantsActivitiesTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+招行活动");
            jsonObject.put("SampleContent", "+招行活动:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+招行活动值");
            jsonObject.put("SampleContent", formatMoney(methodMerchantsActivitiesTotal) + "(" + numMerchantsActivities + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodMarketActivitiesTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+商场活动");
            jsonObject.put("SampleContent", "+商场活动:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+商场活动值");
            jsonObject.put("SampleContent", formatMoney(methodMarketActivitiesTotal) + "(" + numMarketActivities + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodMeituanShanhuiTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+美团闪惠");
            jsonObject.put("SampleContent", "+美团闪惠:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+美团闪惠值");
            jsonObject.put("SampleContent", formatMoney(methodMeituanShanhuiTotal) + "(" + numMeituanShanhui + ")");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodPublicSignalTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+公众号");
            jsonObject.put("SampleContent", "+公众号:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+公众号值");
            jsonObject.put("SampleContent", formatMoney(methodPublicSignalTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodWechatCouponTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+公众号优惠");
            jsonObject.put("SampleContent", "+公众号优惠:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+公众号优惠值");
            jsonObject.put("SampleContent", formatMoney(methodWechatCouponTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodStoreReduceTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+店铺满减");
            jsonObject.put("SampleContent", "+店铺满减:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+店铺满减值");
            jsonObject.put("SampleContent", formatMoney(methodStoreReduceTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodPackageTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+套餐买单");
            jsonObject.put("SampleContent", "+套餐买单:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+套餐买单值");
            jsonObject.put("SampleContent", formatMoney(methodPackageTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodOtherTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+其它优惠");
            jsonObject.put("SampleContent", "+其它优惠:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+其它优惠值");
            jsonObject.put("SampleContent", formatMoney(methodOtherTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodTinyLifeCouponTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活代金券");
            jsonObject.put("SampleContent", "+微生活代金券:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活代金券值");
            jsonObject.put("SampleContent", formatMoney(methodTinyLifeCouponTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodTinyLifeIntegralDeductionTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活积分折扣");
            jsonObject.put("SampleContent", "+微生活积分折扣:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活积分折扣值");
            jsonObject.put("SampleContent", formatMoney(methodTinyLifeIntegralDeductionTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodTinyLifeStorecardTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活存储卡");
            jsonObject.put("SampleContent", "+微生活存储卡:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微生活代存储卡值");
            jsonObject.put("SampleContent", formatMoney(methodTinyLifeStorecardTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }
        if (methodWeChatUnionpayPayMentTotal > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微信银联支付");
            jsonObject.put("SampleContent", "+微信银联支付:");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 0);
            jsonObject.put("FrontLen", 4);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("Name", "+微信银联支付值");
            jsonObject.put("SampleContent", formatMoney(methodWeChatUnionpayPayMentTotal) + "");
            jsonObject.put("FrontEnterNum", 0);
            jsonObject.put("BehindEnterNum", 1);
            jsonObject.put("FrontLen", 0);
            jsonObject.put("BehindLen", 0);
            jsonObject.put("Size", 1);
            jsonObject.put("ComType", 1);
            jsonArray.add(jsonObject);
        }

        //新添加的东西
        jsonObject = new JSONObject();
        jsonObject.put("Name", "抹零");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "抹零:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "抹零值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", order.getOrderErase() + "");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "折扣合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "折扣合计:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "折扣合计值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
//        jsonObject.put("SampleContent", orderManager.getOrderDiscountPrice(order.getOrderId()) + "");
        jsonObject.put("SampleContent", formatMoney(orderService.sumDiscountPrice(orderDishesesMergesExcludeReturn)));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "应收合计");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "应收合计:");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "应收合计值");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        // jsonObject.put("SampleContent", orderManager.getOrderNeedPay(order.getOrderId()) + "");
        jsonObject.put("SampleContent", orderService.notPaidBillAmount(order));
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        Desk desk = deskService.getById(order.getDeskId());
        DeskKey deskKey = deskKeyDAO.getByDeskId(desk.getDeskId());
        String deskKeyString = deskKey != null ? deskKey.getDeskKey() : "";
        String deskCode = LocalDeskCode.getDeskCode(desk.getDeskName());
        // String storeId = LocalServerConfig.storeId;

        jsonObject = new JSONObject();
        jsonObject.put("Name", "双二维码");
        jsonObject.put("ComType", EnumComType.QRCODE2.type);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonObject.put("Width", 560);
        jsonObject.put("Height", 260);
        jsonObject.put("QrWidth", 250);
        jsonObject.put("LeftPadding1", 30);
        jsonObject.put("LeftPadding2", 30);
        jsonObject.put("Text1", cfg.getTickedUrl() + "?d=" + deskCode + "&s=" + store.getStoreId() + "&k=" + deskKeyString);
        jsonObject.put("Text2", cfg.getPublicAddress());
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "微信扫一扫");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "微信扫一扫,加菜,结账");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 3);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", "关注微信公众号");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", "关注微信公众号");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 6);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        jsonArray.add(jsonObject);

//        jsonObject = new JSONObject();
//        jsonObject.put("Name", "二维码");
//        jsonObject.put("ComType", TicketCom.TYPE_QRCODE);
//        jsonObject.put("Content", "http://weixin.qq.com/r/GC7m-pzEj8Hwrdid93sK");
//        jsonObject.put("Size", 50);
//        jsonObject.put("FrontEnterNum", 0);
//        jsonObject.put("BehindEnterNum", 1);
//        jsonArray.add(jsonObject);
        return jsonArray;
    }

}
