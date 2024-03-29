package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumIsPackage;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.service.domain.CartService;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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
            CartVO cartVO = cartService.getCart(deskId).getData();
            JSONArray cartDisheses = new JSONArray();
            for (CartItemVO cartDishes : cartVO.getContents()) {
                if (cartDishes.getIfDishesPackage() != 2) {
                    JSONObject cartDishesItem = new JSONObject();
                    cartDishesItem.put("num", cartDishes.getNums());
                    cartDishesItem.put("dishesPriceId", cartDishes.getDishesPriceId());
                    if (cartDishes.getIfDishesPackage() == EnumIsPackage.YES.code) {
                        cartDishesItem.put("type", "dishesPackage");
                        cartDishesItem.put("dishesPackageId", cartDishes.getDishesId());
                    } else if (cartDishes.getIfDishesPackage() == EnumIsPackage.NO.code) {
                        cartDishesItem.put("type", "dishes");
                        cartDishesItem.put("dishesId", cartDishes.getDishesId());

                        List<DishesAttributeVO> dishesAttributes = cartDishes.getDishesAttrs();
                        JSONObject dishesAttr = new JSONObject();
                        for (DishesAttributeVO dishesAttribute : dishesAttributes) {
                            List<DishesAttributeValueVO> attrs = dishesAttribute.getAllAttributeValues();
                            if (CommonUtils.isNotEmpty(attrs)) {
                                dishesAttr.put(dishesAttribute.getDishesAttributeName(), attrs.get(0).getAttributeValue());
                            }
                        }
                        cartDishesItem.put("dishesAttribute", dishesAttr);
                    }
                    cartDisheses.add(cartDishesItem);
                }
            }

            jSONObjectReturn.put("cartDisheses", cartDisheses);
            jSONObjectReturn.put("cartDishesesNums", cartVO.sumDishesNum());
            jSONObjectReturn.put("totalPrice", cartService.sumCartPrice(cartVO));
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
