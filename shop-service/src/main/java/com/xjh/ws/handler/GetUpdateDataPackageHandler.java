package com.xjh.ws.handler;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesTypeUpdateDAO;
import com.xjh.dao.mapper.DishesUpdateDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("getUpdateDataPackage")
public class GetUpdateDataPackageHandler implements WsHandler {
    @Inject
    DishesService dishesService;
    @Inject
    DishesUpdateDAO dishesUpdateDAO;
    @Inject
    DishesTypeUpdateDAO dishesTypeUpdateDAO;
    @Inject
    DishesTypeService dishesTypeService;

    private long lastUpdatTimeNew = 0;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        long lastUpdatTime = 0;
        if (msg.containsKey("lastUpdateTime")) {
            lastUpdatTime = msg.getLong("lastUpdateTime");
        }
        lastUpdatTimeNew = lastUpdatTime;
        JSONObject resp = new JSONObject();
        resp.put("API_TYPE", "getUpdateDataPackage_ACK");
        resp.put("maxUpdateTime", lastUpdatTimeNew);
        if (msg.containsKey("h5SessionId")) {
            resp.put("h5SessionId", msg.getIntValue("h5SessionId"));
        }
        //
        resp.put("dishes", getDishesInfo(lastUpdatTime));
        resp.put("dishestype", getDishesType(lastUpdatTime));
        return resp;
    }


    private JSONArray getDishesInfo(long lastUpdatTime) {
        List<Dishes> disheses = dishesService.getAllDishes();
        Map<Integer, DishesType> dishesTypeMap = dishesTypeService.dishesTypeMap();
        JSONArray jSONArray = new JSONArray();
        for (Dishes dishes : disheses) {
            long lastUpdateTimeNow = dishesUpdateDAO.getDishesUpdateLastUpdateTime(dishes.getDishesId());
            if (lastUpdateTimeNow > lastUpdatTime) {
                if (lastUpdateTimeNow > lastUpdatTimeNew) {
                    lastUpdatTimeNew = lastUpdateTimeNow;
                }

                JSONObject jSONObjectDishes = new JSONObject();
                DishesType dishesType = dishesTypeMap.get(dishes.getDishesTypeId());
                if (dishesType != null) {
                    jSONObjectDishes.put("dishes_type_id", dishesType.getTypeId());
                    jSONObjectDishes.put("dishes_type_name", dishesType.getTypeName());
                    jSONObjectDishes.put("if_refund", dishesType.getIfRefund());
                }
                jSONObjectDishes.put("description", dishes.getDishesDescription());
                jSONObjectDishes.put("date_end", "");
                jSONObjectDishes.put("dishes_name", dishes.getDishesName());
                jSONObjectDishes.put("status", dishesService.getDishesCurrentStatus(dishes));
                jSONObjectDishes.put("imgurl", "");
                jSONObjectDishes.put("imgNum", dishesService.resolveImgs(dishes).size());
                jSONObjectDishes.put("dishes_id", dishes.getDishesId());
                jSONObjectDishes.put("dishes_spell", "");
                jSONObjectDishes.put("price", CommonUtils.formatMoney(dishes.getDishesPrice()));

                List<DishesPrice> dishesPriceList = dishesService.queryDishesPrice(dishes.getDishesId());
                jSONObjectDishes.put("priceList", dishesPriceList);

                JSONArray jSONArrayDishesAttributes = new JSONArray();
                //                List<DishesAttribute> dishesAttributes = dishesManager.getDishesAttributes(dishes);
                //                JSONArray jSONArrayDishesAttributes = new JSONArray();
                //                for (int j = 0; j < dishesAttributes.size(); j++) {
                //                    DishesAttribute dishesAttribute = dishesAttributes.get(j);
                //                    jSONArrayDishesAttributes.put(dishesAttribute.getDishesAttributeName());
                //                    List<DishesAttributeValue> dishesAttributeValues = dishesAttribute.getAllAttributeValues();
                //                    JSONArray jSONArrayDishesAttributeValue = new JSONArray();
                //                    for (int k = 0; k < dishesAttributeValues.size(); k++) {
                //                        DishesAttributeValue dishesAttributeValue = dishesAttributeValues.get(k);
                //                        jSONArrayDishesAttributeValue.add(dishesAttributeValue.value());
                //                    }
                //                    jSONArrayDishesAttributes.add(jSONArrayDishesAttributeValue);
                //                }
                jSONObjectDishes.put("options", jSONArrayDishesAttributes);
                jSONObjectDishes.put("vipprice", 0);
                jSONObjectDishes.put("sku", dishes.getDishesId());
                jSONObjectDishes.put("date_available", dishes.getCreatTime());
                jSONObjectDishes.put("send", "1");
                jSONArray.add(jSONObjectDishes);
            }
        }
        return jSONArray;
    }

    private JSONArray getDishesType(long lastUpdatTime) {
        List<DishesType> dishesTypes = dishesTypeService.loadAllTypes();
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < dishesTypes.size(); i++) {
            DishesType dishesType = dishesTypes.get(i);
            long lastUpdateTimeNow = dishesTypeUpdateDAO.getDishesTypeLastUpdateTime(dishesType.getTypeId());
            if (lastUpdateTimeNow > lastUpdatTime) {
                if (lastUpdateTimeNow > lastUpdatTimeNew) {
                    lastUpdatTimeNew = lastUpdateTimeNow;
                }
                JSONObject jSONObjectDishesType = new JSONObject();
                jSONObjectDishesType.put("date_added", dishesType.getCreatTime());
                jSONObjectDishesType.put("dishes_type_name", dishesType.getTypeName());
                jSONObjectDishesType.put("dishes_type_id", dishesType.getTypeId());
                jSONObjectDishesType.put("status", dishesService.getDishesTypeCurrentStatus(dishesType));
                jSONObjectDishesType.put("sort_order", dishesType.getSortby());
                jSONObjectDishesType.put("if_refund", dishesType.getIfRefund());
                jSONObjectDishesType.put("hidden_h5", dishesType.getHiddenH5());
                jSONObjectDishesType.put("hidden_flat", dishesType.getHiddenFlat());
                jSONArray.add(jSONObjectDishesType);
            }
        }
        return jSONArray;
    }
}
