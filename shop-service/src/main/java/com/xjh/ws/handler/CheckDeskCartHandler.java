package com.xjh.ws.handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.service.domain.CartService;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("checkDeskCart")
public class CheckDeskCartHandler implements WsHandler {
    @Inject
    CartService cartService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "checkDeskCart_ACK");
        try {
            int deskId = msg.getInteger("tables_id");
            //Desk desk = desksManager.getOneDesk(deskId);
            CartVO cartVO = cartService.getCartOfDesk(deskId).getData();
            JSONArray jSONArray = new JSONArray();
            for (CartItemVO cartDishes : cartVO.getContents()) {
                //                if (cartDishes.getIfDishesPackage() != 2) {
                //                    JSONObject jSONObjectCartDishes = new JSONObject();
                //                    jSONObjectCartDishes.put("num", cartDishes.getNums());
                //                    jSONObjectCartDishes.put("dishesPriceId", cartDishes.getDishesPriceId());
                //                    if (cartDishes.getIfDishesPackage() == OrderDishes.ORDER_TYPE_DISHESPACKAGE) {
                //                        jSONObjectCartDishes.put("type", "dishesPackage");
                //                        jSONObjectCartDishes.put("dishesPackageId", cartDishes.getDishesId());
                //                    } else if (cartDishes.getIfDishesPackage() == OrderDishes.ORDER_TYPE_NO_DISHESPACKAGE) {
                //                        jSONObjectCartDishes.put("type", "dishes");
                //                        jSONObjectCartDishes.put("dishesId", cartDishes.getDishesId());
                //
                //                        List<DishesAttribute> dishesAttributes = cartDishes.getDishesAttribute();
                //
                //                        JSONObject jSONObjectDishesAttribute = new JSONObject();
                //                        for (int j = 0; j < dishesAttributes.size(); j++) {
                //                            DishesAttribute dishesAttribute = dishesAttributes.get(j);
                //                            List<DishesAttributeValue> dishesAttributeValues = dishesAttribute.getCurrentAttributeValues();
                //                            if (dishesAttributeValues != null) {
                //                                if (dishesAttributeValues.isEmpty() == false) {
                //                                    jSONObjectDishesAttribute.put(dishesAttribute.getDishesAttributeName(), dishesAttributeValues.get(0).value());
                //                                }
                //                            }
                //                        }
                //
                //                        jSONObjectCartDishes.put("dishesAttribute", jSONObjectDishesAttribute);
                //                    }
                //
                //                    jSONArray.add(jSONObjectCartDishes);
                //                }
            }

            jSONObjectReturn.put("cartDisheses", jSONArray);
            //            jSONObjectReturn.put("cartDishesesNums", orderManager.getCartDishesesNums(deskId));
            //            jSONObjectReturn.put("totalPrice", parameterPackage.formatPrice((double) orderManager.getCartDishesesAllPrice(deskId)));
            jSONObjectReturn.put("status", 0);
            if (msg.containsKey("h5SessionId")) {
                jSONObjectReturn.put("h5SessionId", msg.getInteger("h5SessionId"));
            }
        } catch (Exception e) {
            jSONObjectReturn = new JSONObject();
            jSONObjectReturn.put("API_TYPE", "checkDeskCart_ACK");
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        return jSONObjectReturn;
    }
}
