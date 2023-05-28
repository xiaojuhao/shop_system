package com.xjh.ws.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.*;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import com.xjh.common.valueobject.OrderOverviewVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.service.domain.*;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsAttachment;
import com.xjh.ws.WsHandler;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@WsApiType("checkDeskInfo")
public class CheckDeskInfoHandler implements WsHandler {
    @Inject
    DeskService deskService;
    @Inject
    OrderService orderService;
    @Inject
    OrderDishesService orderDishesService;
    @Inject
    DishesPackageService dishesPackageService;
    @Inject
    DishesService dishesService;
    @Inject
    DishesPriceDAO dishesPriceDAO;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject respMsg = new JSONObject();
        respMsg.put("API_TYPE", "checkDeskInfo_ACK");
        try {
            int deskId = msg.getInteger("tables_id");
            WsAttachment attachment = ws.getAttachment();
            attachment.setDeskId(deskId);
            Desk desk = deskService.getById(deskId);
            if (desk == null) {
                throw new Exception("桌子不存在");
            }
            EnumDeskStatus deskUseStatus = EnumDeskStatus.of(desk.getStatus());

            EnumDeskType deskType = EnumDeskType.of(desk.getBelongDeskType());
            respMsg.put("deskType", deskType.name);
            respMsg.put("deskCreateTime", desk.getOrderCreateTime());
            respMsg.put("deskId", desk.getDeskId());
            respMsg.put("deskName", desk.getDeskName());
            respMsg.put("deskMaxPersonNum", desk.getMaxPerson());
            respMsg.put("deskUseStatus", deskUseStatus.status());

            int deskPhysicalStatus = desk.getPhysicalStatus();
            respMsg.put("deskPhysicalStatus", deskPhysicalStatus);
            EnumDeskPhysicalStatus physicalStatus = EnumDeskPhysicalStatus.of(deskPhysicalStatus);
            Order order = orderService.getOrder(desk.getOrderId());
            List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(desk.getOrderId());
            if (physicalStatus != EnumDeskPhysicalStatus.DISABLE) {
                if (deskUseStatus != EnumDeskStatus.FREE && deskUseStatus != EnumDeskStatus.PRESERVED && order != null) {
                    JSONObject orderInfo = new JSONObject();
                    int orderId = order.getOrderId();
                    EnumOrderStatus orderStatus = EnumOrderStatus.of(order.getOrderStatus());

                    JSONObject cashArray = new JSONObject();
                    JSONObject coupons = new JSONObject();
                    coupons.put("cashArray", cashArray);
                    orderInfo.put("coupons", coupons);
                    orderInfo.put("orderId", orderId);
                    orderInfo.put("watertNickName", order.getOrderRecommender());
                    orderInfo.put("createtime", order.getCreateTime());
                    orderInfo.put("customerNums", order.getOrderCustomerNums());
                    orderInfo.put("customerTelphone", order.getOrderCustomerTelphone());
                    orderInfo.put("discountName", OrElse.orGet(order.getDiscountReason(), "无"));
                    orderInfo.put("discountValue", order.getFullReduceDishesPrice());
                    orderInfo.put("erase", CommonUtils.parseInt(order.getOrderErase(), 0));
                    orderInfo.put("hadpaid", order.getOrderHadpaid());
                    orderInfo.put("recommender", order.getOrderRecommender());
                    orderInfo.put("reduction", order.getOrderReduction());
                    orderInfo.put("refund", CommonUtils.parseInt(order.getOrderRefund(), 0));
                    orderInfo.put("status", orderStatus.remark);
                    orderInfo.put("statusCode", orderStatus.status);

                    OrderOverviewVO billVO = orderService.buildOrderOverview(order, orderDishesList, null).getData();
                    orderInfo.put("totalPrice", CommonUtils.decimalMoney(billVO.getTotalPrice()));
                    orderInfo.put("needPayPrice", CommonUtils.decimalMoney(billVO.getOrderNeedPay()));
                    orderInfo.put("returnDishesPrice", CommonUtils.decimalMoney(billVO.getReturnDishesPrice()));
                    orderInfo.put("discountPrice", CommonUtils.decimalMoney(billVO.getDiscountAmount()));
                    respMsg.put("orderInfo", orderInfo);

                    //查找订单关联的微信券
                    //                    OrderCoupon orderCoupon = orderManager.getOrderCoupon(orderId);
                    //                    JSONObject orderCouponJson = new JSONObject();
                    //                    if (orderCoupon != null) {
                    //                        orderCouponJson.put("couponType", orderCoupon.getCouponType());
                    //                        orderCouponJson.put("couponId", orderCoupon.getCouponId());
                    //                        orderCouponJson.put("name", orderCoupon.getName());
                    //                        orderCouponJson.put("condition", orderCoupon.getCondition());
                    //                        orderCouponJson.put("amount", orderCoupon.getAmount());
                    //                        orderCouponJson.put("rate", orderCoupon.getRate());
                    //                        orderCouponJson.put("serialNumberId", orderCoupon.getSerialNumberId());
                    //                        orderCouponJson.put("status", orderCoupon.getStatus());
                    //                        respMsg.put("orderCoupon", orderCouponJson);
                    //                    }


                    List<OrderDishes> orderDisheses = orderDishesService.selectByOrderId(orderId);
                    if (orderDisheses == null) {
                        orderDisheses = new ArrayList<>();
                    }
                    JSONArray orderDishesArray = new JSONArray();
                    for (int i = 0; i < orderDisheses.size(); i++) {
                        JSONObject od = new JSONObject();
                        OrderDishes orderDishes = orderDisheses.get(i);
                        int dishesId = orderDishes.getDishesId();

                        if (orderDishes.getIfDishesPackage() == 1) {
                            od.put("type", "dishesPackage");
                            od.put("dishesPackageId", dishesId);
                            DishesPackage dishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                            od.put("dishesPackageName", dishesPackage.getDishesPackageName());
                        } else if (orderDishes.getIfDishesPackage() == 2) {
                            od.put("type", "dishesPackage");
                            od.put("dishesPackageId", dishesId);
                            DishesPackage dishesPackage = dishesPackageService.getByDishesPackageId(orderDishes.getDishesId());
                            od.put("dishesPackageName", dishesPackage.getDishesPackageName());
                        } else {
                            od.put("type", "dishes");
                            od.put("dishesId", dishesId);
                            Dishes dishes = dishesService.getById(dishesId);
                            DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
                            String dishesPriceString = "";
                            if (dishesPrice != null) {
                                dishesPriceString = "(" + dishesPrice.getDishesPriceName() + ")";
                            }
                            od.put("dishesName", dishes.getDishesName() + dishesPriceString);

                            //                            List<DishesAttribute> dishesAttributes = orderDishes.getDishesAttributes();
                            //System.out.println("api.server.OrderHandle.checkDeskInfo()" + orderDishes.getOrderDishesOptions());
                            JSONObject jSONObjectDishesAttribute = new JSONObject();
                            //System.out.println("api.server.OrderHandle.checkDeskInfo()jSONObjectDishesAttribute.length()=["+jSONObjectDishesAttribute.length()+"]-\r\n-["+dishes.getDishesName()+"]");
                            //                            for (int j = 0; j < dishesAttributes.size(); j++) {
                            //                                DishesAttribute dishesAttribute = dishesAttributes.get(j);
                            //                                String attributeName = dishesAttribute.getDishesAttributeName();
                            //                                List<DishesAttributeValue> dishesAttributeValues = dishesAttribute.getCurrentAttributeValues();
                            //                                String attributeValue = "";
                            //                                for (int k = 0; k < dishesAttributeValues.size(); k++) {
                            //                                    DishesAttributeValue dishesAttributeValue = dishesAttributeValues.get(k);
                            //                                    if (attributeValue.length() > 0) {
                            //                                        attributeValue = attributeValue + "," + dishesAttributeValue.value();
                            //                                    } else {
                            //                                        attributeValue = dishesAttributeValue.value();
                            //                                    }
                            //                                }
                            //                                jSONObjectDishesAttribute.put(attributeName, attributeValue);
                            //                            }
                            od.put("dishesAttribute", jSONObjectDishesAttribute);
                        }

                        od.put("price", CommonUtils.decimalMoney(orderDishes.getOrderDishesPrice()));

                        od.put("discountPrice", CommonUtils.decimalMoney(orderDishes.getOrderDishesDiscountPrice()));
                        od.put("num", orderDishes.getOrderDishesNums());

                        od.put("ifrefund", (orderDishes.getOrderDishesIfrefund() == 0));
                        od.put("saleType", EnumOrderSaleType.of(orderDishes.getOrderDishesSaletype()).type);
                        od.put("dishesStatus", orderDishes.getOrderDishesStatus());

                        orderDishesArray.add(od);
                    }
                    // jSONArrayDishesArray = sortDishesArray(jSONArrayDishesArray);
                    respMsg.put("orderDishesArray", orderDishesArray);
                }
            }

            respMsg.put("status", 0);
        } catch (Exception e) {
            respMsg = new JSONObject();
            respMsg.put("API_TYPE", "checkDeskInfo_ACK");
            respMsg.put("status", 1);
            respMsg.put("msg", e.getMessage());
        }
        if (msg.containsKey("h5SessionId")) {
            respMsg.put("h5SessionId", msg.getIntValue("h5SessionId"));
        }
        return respMsg;
    }
}
