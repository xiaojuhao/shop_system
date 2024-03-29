package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.enumeration.EnumDeskType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.*;
import com.xjh.service.domain.*;
import com.xjh.service.store.ImageHelper;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

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
    DishesPackageUpdateDAO dishesPackageUpdateDAO;
    @Inject
    DishesTypeService dishesTypeService;
    @Inject
    DishesPackageService dishesPackageService;
    @Inject
    DishesPackageDishesDAO dishesPackageDishesDAO;
    @Inject
    DeskService deskService;
    @Inject
    DishesDAO dishesDAO;
    @Inject
    DeskDAO deskDAO;
    @Inject
    OrderService orderService;

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
        resp.put("package", getDishesPackage(lastUpdatTime));
        resp.put("tables", getDesks(lastUpdatTime));
        resp.put("tablestype", getDeskTypes(lastUpdatTime));
        return resp;
    }


    private JSONArray getDishesInfo(long lastUpdatTime) {
        List<Dishes> disheses = dishesService.getAllDishes();
        Map<Integer, DishesType> dishesTypeMap = dishesTypeService.dishesTypeMap();
        JSONArray jSONArray = new JSONArray();
        for (Dishes dishes : disheses) {
            long lastUpdateTimeNow = System.currentTimeMillis();//dishesUpdateDAO.getDishesUpdateLastUpdateTime(dishes.getDishesId());
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
        List<DishesType> dishesTypes = dishesTypeService.loadAllTypesValid();
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < dishesTypes.size(); i++) {
            DishesType dishesType = dishesTypes.get(i);
            long lastUpdateTimeNow = System.currentTimeMillis();//dishesTypeUpdateDAO.getDishesTypeLastUpdateTime(dishesType.getTypeId());
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

    private JSONArray getDishesPackage(long lastUpdatTime) {

        List<DishesPackage> dishesPackages = dishesPackageService.selectAll();

        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < dishesPackages.size(); i++) {
            DishesPackage dishesPackage = dishesPackages.get(i);
            long lastUpdateTimeNow = System.currentTimeMillis();//dishesPackageUpdateDAO.getLastUpdateTime(dishesPackage.getDishesPackageId());
            if (lastUpdateTimeNow > lastUpdatTime) {
                if (lastUpdateTimeNow > lastUpdatTimeNew) {
                    lastUpdatTimeNew = lastUpdateTimeNow;
                }
                JSONObject jSONObjectDishesPackage = new JSONObject();
                jSONObjectDishesPackage.put("imgurl", "");
                jSONObjectDishesPackage.put("imgNum", ImageHelper.resolveImgs(dishesPackage.getDishesPackageImg()).size());
                jSONObjectDishesPackage.put("price", dishesPackage.getDishesPackagePrice());
                jSONObjectDishesPackage.put("package_name", dishesPackage.getDishesPackageName());
                jSONObjectDishesPackage.put("date_end", "");
                jSONObjectDishesPackage.put("package_id", dishesPackage.getDishesPackageId());
                jSONObjectDishesPackage.put("sku", dishesPackage.getDishesPackageId());
                jSONObjectDishesPackage.put("date_available", dishesPackage.getCreatTime());

                DishesPackageDishes cond = new DishesPackageDishes();
                cond.setDishesPackageId(dishesPackage.getDishesPackageId());
                List<DishesPackageDishes> disheses = dishesPackageDishesDAO.selectList(cond);
                JSONArray jSONArrayDishes = new JSONArray();

                for (int j = 0; j < disheses.size(); j++) {
                    DishesPackageDishes pkgDishes = disheses.get(j);
                    Dishes dishes = dishesDAO.getById(pkgDishes.getDishesId());
                    if (dishes != null) {
                        JSONObject jSONObjectDishes = new JSONObject();
                        jSONObjectDishes.put("imgurl", "");
                        jSONObjectDishes.put("price", dishes.getDishesPrice());
                        jSONObjectDishes.put("description", dishes.getDishesDescription());
                        jSONObjectDishes.put("vipprice", 0);
                        jSONObjectDishes.put("dishes_name", dishes.getDishesName());
                        jSONObjectDishes.put("sku", dishes.getDishesId());
                        jSONArrayDishes.add(jSONObjectDishes);
                    }
                }

                jSONObjectDishesPackage.put("dishes", jSONArrayDishes);
                jSONArray.add(jSONObjectDishesPackage);
            }
        }
        return jSONArray;
    }

    private JSONArray getDesks(long lastUpdatTime) {
        List<Desk> desks = deskService.getAllDesks();
        JSONArray jSONArrayDesk = new JSONArray();
        for (int i = 0; i < desks.size(); i++) {
            Desk desk = desks.get(i);
            long lastUpdateTimeNow = System.currentTimeMillis();//deskDAO.getDeskLastUpdateTime(desk.getDeskId());
            if (lastUpdateTimeNow > lastUpdatTime) {
                if (lastUpdateTimeNow > lastUpdatTimeNew) {
                    lastUpdatTimeNew = lastUpdateTimeNow;
                }

                int meal_number = 0;
                int pay_status = EnumOrderStatus.UNPAID.status;
                int status = desk.getStatus();
                if (status == EnumDeskStatus.IN_USE.status() || status == EnumDeskStatus.PAID.status()) {
                    Order order = orderService.getOrder(desk.getOrderId());
                    if (order != null) {
                        meal_number = order.getOrderCustomerNums();
                        pay_status = order.getOrderStatus();
                    }
                }

                JSONObject jSONObjectDesk = new JSONObject();
                jSONObjectDesk.put("tables_id", desk.getDeskId());
                jSONObjectDesk.put("pay_status", pay_status);
                jSONObjectDesk.put("tables_number", desk.getDeskName());
                jSONObjectDesk.put("nuclear_num", desk.getMaxPerson());
                jSONObjectDesk.put("ifpack", "0");
                jSONObjectDesk.put("use_status", desk.getStatus());
                jSONObjectDesk.put("tables_type_name", EnumDeskType.of(desk.getBelongDeskType()).name);
                jSONObjectDesk.put("meal_number", meal_number);
                jSONArrayDesk.add(jSONObjectDesk);
            }
        }

        return jSONArrayDesk;
    }

    private JSONArray getDeskTypes(long lastUpdatTime) {
        JSONArray jSONArrayDeskType = new JSONArray();
        for (EnumDeskType deskType : EnumDeskType.values()) {
            long lastUpdateTimeNow = 0;
            if (lastUpdateTimeNow > lastUpdatTime) {
                if (lastUpdateTimeNow > lastUpdatTimeNew) {
                    lastUpdatTimeNew = lastUpdateTimeNow;
                }
                JSONObject jSONObjectDeskType = new JSONObject();
                jSONObjectDeskType.put("store_id", "");
                // jSONObjectDeskType.put("date_added", deskType.getCreateTime());
                jSONObjectDeskType.put("tables_type_id", deskType.code);
                jSONObjectDeskType.put("tables_type_name", deskType.name);
                jSONObjectDeskType.put("status", "1");
                jSONArrayDeskType.add(jSONObjectDeskType);
            }
        }
        return jSONArrayDeskType;
    }
}
