package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumIsPackage;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.service.domain.CartService;
import com.xjh.service.ws.SocketUtils;
import com.xjh.service.ws.WsApiType;
import com.xjh.service.ws.WsAttachment;
import com.xjh.startup.foundation.ws.WsHandler;
import com.xjh.startup.view.OrderDishesChoiceView;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@WsApiType("updateCartDishes")
public class UpdateCartDishesHandler implements WsHandler {
    @Inject
    CartService cartService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        WsAttachment attachment = ws.getAttachment();

        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "updateCartDishes_ACK");

        try {
            int deskId = msg.getInteger("tables_id");
            // 购物车里面每个条目的ID(数组的下标）
            int cartDishesId = msg.getInteger("cartDishesId");
            Result<CartVO> cartVOResult = cartService.getCart(deskId);
            int num = msg.getInteger("num");
            CartVO cartVO = cartVOResult.getData();
            // 删除条目
            if (num <= 0) {
                List<CartItemVO> contents = cartVO.getContents();
                contents.remove(cartDishesId);
                cartVO.setContents(contents);
                Result<CartVO> rs = cartService.updateCart(deskId, cartVO);
                if (rs.isSuccess()) {
                    jSONObjectReturn.put("status", 0);

                    JSONObject nextMsg = new JSONObject();
                    nextMsg.put("API_TYPE", "removeDishesFromCart");
                    nextMsg.put("deskId", deskId);
                    nextMsg.put("operateAccount", attachment.getAccountUser());
                    nextMsg.put("cartDishesId", cartDishesId);
                    nextMsg.put("cartDishesesNums", cartVO.sumDishesNum());
                    nextMsg.put("totalPrice", cartService.sumCartPrice(cartVO));
                    attachment.addNext(() -> SocketUtils.sendMsg(deskId, nextMsg));

                } else {
                    jSONObjectReturn.put("status", 1);
                    jSONObjectReturn.put("msg", "更新失败");
                }
            } else {
                List<CartItemVO> contents = cartVO.getContents();
                CartItemVO cartItem = contents.get(cartDishesId);
                cartItem.setNums(num);

                Result<CartVO> rs = cartService.updateCart(deskId, cartVO);
                if (rs.isSuccess()) {
                    jSONObjectReturn.put("status", 0);

                    JSONObject nextMsg = new JSONObject();
                    nextMsg.put("API_TYPE", "updateCartDishes");
                    nextMsg.put("deskId", deskId);
                    nextMsg.put("dishesId", cartItem.getDishesId());
                    if (cartItem.getIfDishesPackage() == EnumIsPackage.YES.code) {
                        nextMsg.put("type", "packages");
                    } else if (cartItem.getIfDishesPackage() == EnumIsPackage.YES_NEW.code) {
                        nextMsg.put("type", "packages");
                    } else {
                        nextMsg.put("type", "dishes");
                    }
                    nextMsg.put("num", cartItem.getNums());
                    nextMsg.put("cartDishesId", cartDishesId);
                    nextMsg.put("operateAccount", attachment.getAccountUser());
                    nextMsg.put("cartDishesesNums", cartVO.sumDishesNum());
                    nextMsg.put("totalPrice", cartService.sumCartPrice(cartVO));
                    //
                    attachment.addNext(() -> SocketUtils.sendMsg(deskId, nextMsg));

                } else {
                    jSONObjectReturn.put("status", 1);
                    jSONObjectReturn.put("msg", "更新失败");
                }
            }

            OrderDishesChoiceView.refreshCartSize(deskId);

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
