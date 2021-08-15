package com.xjh.ws.handler;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderService;

@Singleton
public class GetPayInfoHandler {
    @Inject
    DeskService deskService;
    @Inject
    OrderService orderService;

    public JSONObject handle(JSONObject msg) {
        int deskId = msg.getInteger("tables_id");
        String openid = msg.getString("openid");
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "getPayInfo_ACK");
        jSONObjectReturn.put("weChatCouponCanUse", 0);
        jSONObjectReturn.put("weChatMemberDiscountPrice", 0);

        try {
            Desk desk = deskService.getById(deskId);
            Order order = orderService.getOrder(desk.getOrderId());
            double amount = orderService.notPaidBillAmount(order);
            boolean storeReduceifCanUse = false;//this.orderManager.getIfCanUseStoreReduce(order);
            double fullReducePrice = 0.0D;
            boolean wechatCouponCanUse = false; //this.orderManager.checkWechatCouponCanUse(order);
            boolean wechatCouponCanUseReal = false;
            //            OrderCoupon orderCoupon = this.orderManager.getOrderCouponCanUse(order.getOrderId());
            //            if (wechatCouponCanUse && orderCoupon != null) {
            //                if (Arith.round(Arith.sub((double)this.orderManager.getOrderCanDiscountPrice(order.getOrderId()), orderCoupon.getCondition()), 2) >= 0.0D) {
            //                    wechatCouponCanUseReal = true;
            //                    storeReduceifCanUse = false;
            //                } else {
            //                    jSONObjectReturn.put("weChatCouponCanUse", -1);
            //                }
            //            }

            //            if (storeReduceifCanUse) {
            //                float fullReduceDishesPrice = this.orderManager.getFullReduceDishesPrice(order);
            //                fullReducePrice = (double)this.orderManager.getOrderFullReduce(order.getOrderId(), fullReduceDishesPrice);
            //            }

            //            if (!wechatCouponCanUseReal && fullReducePrice == 0.0D) {
            //                boolean wechatMemberDiscountCanUse = this.orderManager.checkWechatMemberDiscountCanUse(order);
            //                if (wechatMemberDiscountCanUse) {
            //                    double weChatMemberDiscount = this.getWeChatMemberDiscount(openid);
            //                    if (weChatMemberDiscount > 0.0D && weChatMemberDiscount < 1.0D) {
            //                        OrderDiscount orderDiscount = new OrderDiscount(parameterPackage, (float)weChatMemberDiscount, "会员折扣", OrderDiscount.MEMBERDISCOUNT, -1, "", OrderDiscount.STATUSENABLE, 1, 0L, 0L);
            //                        float newDiscountPrice = this.placeOrderManager.getNewDiscount(desk, orderDiscount);
            //                        jSONObjectReturn.put("weChatMemberDiscountPrice", Arith.round((double)newDiscountPrice, 2));
            //                    }
            //                }
            //            }

            jSONObjectReturn.put("needPay", amount);
            jSONObjectReturn.put("fullReducePrice", fullReducePrice);
            jSONObjectReturn.put("status", 0);
            return jSONObjectReturn;
        } catch (Exception ex) {
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", "请求失败，请到收银台支付");
            return jSONObjectReturn;
        }
    }
}
