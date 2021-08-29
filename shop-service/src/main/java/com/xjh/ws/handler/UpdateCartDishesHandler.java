package com.xjh.ws.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("updateCartDishes")
public class UpdateCartDishesHandler implements WsHandler {
    @Inject
    CartService cartService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "updateCartDishes_ACK");

        try {
            int deskId = msg.getInteger("tables_id");
            int cartDishesId = msg.getInteger("cartDishesId");
            Result<CartVO> cartVOResult = cartService.getCartOfDesk(deskId);
            int num = msg.getInteger("num");
            CartVO cartVO = cartVOResult.getData();
            if (num <= 0) {
                List<CartItemVO> contents = CommonUtils.filter(cartVO.getContents(), it -> it.getDishesId().equals(cartDishesId));
                cartVO.setContents(contents);
                Result<CartVO> rs = cartService.updateCart(deskId, cartVO);
                if (rs.isSuccess()) {
                    jSONObjectReturn.put("status", 0);
                } else {
                    jSONObjectReturn.put("status", 1);
                    jSONObjectReturn.put("msg", "更新失败");
                }
            } else {
                CommonUtils.forEach(cartVO.getContents(), it -> {
                    if (it.getDishesId() == cartDishesId) {
                        it.setNums(num);
                    }
                });

                //                if (cartDishes.getIfDishesPackage() == OrderDishes.ORDER_TYPE_NO_DISHESPACKAGE) {
                //                    if (jSONObject.has("dishesAttribute")) {
                //                        JSONObject jSONObjectDishesAttributes = jSONObject.getJSONObject("dishesAttribute");
                //
                //                        if (cartDishesId < cartDisheses.size() == false) {
                //                            throw new Exception("cartDishesId错误");
                //                        }
                //
                //                        List<DishesAttribute> dishesAttributes = cartDishes.getDishesAttribute();
                //                        for (int i = 0; i < dishesAttributes.size(); i++) {
                //                            DishesAttribute dishesAttribute = dishesAttributes.get(i);
                //                            if (jSONObjectDishesAttributes.has(dishesAttribute.getDishesAttributeName())) {
                //                                dishesAttribute.selectAttributeValue(jSONObjectDishesAttributes.getString(dishesAttribute.getDishesAttributeName()));
                //                            }
                //                        }
                //                    }
                //                }

                Result<CartVO> rs = cartService.updateCart(deskId, cartVO);
                if (rs.isSuccess()) {
                    jSONObjectReturn.put("status", 0);
                } else {
                    jSONObjectReturn.put("status", 1);
                    jSONObjectReturn.put("msg", "更新失败");
                }
            }

        } catch (Exception e) {
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        if (msg.containsKey("h5SessionId")) {
            jSONObjectReturn.put("h5SessionId", msg.getInteger("h5SessionId"));
        }
        return jSONObjectReturn;
    }
}
