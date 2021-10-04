package com.xjh.startup.foundation.helper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.startup.foundation.constants.EnumComType;
import com.xjh.startup.view.model.DeskOrderParam;

@Singleton
public class OrderPrinterHelper {
    @Inject
    OrderService orderService;
    @Inject
    StoreService storeService;
    @Inject
    DeskService deskService;

    public JSONArray buildOrderPrintData(DeskOrderParam param) {
        JSONArray array = new JSONArray();
        Order order = orderService.getOrder(param.getOrderId());
        StoreVO store = storeService.getStore().getData();
        Desk desk = deskService.getById(order.getDeskId());
        array.add(simpleText("订单编号:" + order.getCreateTime() + "" + order.getOrderId()));
        array.add(simpleText("门店名称:" + store.getName()));
        array.add(simpleText("开台时间:" + DateBuilder.base(order.getCreateTime()).timeStr()));
        array.add(simpleText("桌台信息:" + desk.getBelongDeskType() + desk.getDeskName()));
        array.add(simpleText("用餐人数:" + order.getOrderCustomerNums()));
        array.add(simpleText("点菜员:点菜员1"));
        array.add(crlf()); // 换行
        array.add(simpleText("普通菜品"));
        return array;
    }

    private List<JSONObject> keyPair(String title, Object value) {
        List<JSONObject> pair = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", title + "-名称");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", title + ":");
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 0);
        pair.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Name", title + "-value");
        jsonObject.put("ComType", EnumComType.TEXT.type);
        jsonObject.put("SampleContent", value);
        jsonObject.put("Size", 1);
        jsonObject.put("FrontLen", 0);
        jsonObject.put("BehindLen", 0);
        jsonObject.put("FrontEnterNum", 0);
        jsonObject.put("BehindEnterNum", 1);
        pair.add(jsonObject);
        return pair;
    }

    private JSONObject simpleText(String text) {
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
        jsonObject.put("ComType", EnumComType.CRLF.type);
        return jsonObject;
    }
}
