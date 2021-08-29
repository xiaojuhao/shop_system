package com.xjh.ws.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.enumeration.EnumDeskPhysicalStatus;
import com.xjh.common.enumeration.EnumDeskType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.OrderDishesService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.OrderBillVO;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

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
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "checkDeskInfo_ACK");
        try {
            int deskId = msg.getInteger("tables_id");
            Desk desk = deskService.getById(deskId);
            if (desk == null) {
                throw new Exception("桌子不存在");
            }
            EnumDeskType deskType = EnumDeskType.of(desk.getBelongDeskType());
            jSONObjectReturn.put("deskType", deskType.name);
            jSONObjectReturn.put("deskCreateTime", desk.getOrderCreateTime());
            jSONObjectReturn.put("deskId", desk.getDeskId());
            jSONObjectReturn.put("deskName", desk.getDeskName());
            jSONObjectReturn.put("deskMaxPersonNum", desk.getMaxPerson());
            EnumDesKStatus deskUseStatus = EnumDesKStatus.of(desk.getStatus());
            jSONObjectReturn.put("deskUseStatus", deskUseStatus);

            int deskPhysicalStatus = desk.getPhysicalStatus();
            jSONObjectReturn.put("deskPhysicalStatus", deskPhysicalStatus);
            EnumDeskPhysicalStatus physicalStatus = EnumDeskPhysicalStatus.of(deskPhysicalStatus);
            Order order = orderService.getOrder(desk.getOrderId());
            List<OrderDishes> orderDishesList = orderDishesService.selectByOrderId(desk.getOrderId());
            if (physicalStatus != EnumDeskPhysicalStatus.DISABLE) {
                if (deskUseStatus != EnumDesKStatus.FREE && deskUseStatus != EnumDesKStatus.PRESERVED
                        && order != null) {
                    JSONObject jSONObjectOrder = new JSONObject();
                    int orderId = order.getOrderId();
                    jSONObjectOrder.put("orderId", orderId);
                    // jSONObjectOrder.put("watertNickName", order.getOrderRecommender());
                    jSONObjectOrder.put("createtime", order.getCreateTime());
                    jSONObjectOrder.put("customerNums", order.getOrderCustomerNums());
                    jSONObjectOrder.put("customerTelphone", order.getOrderCustomerTelphone());
                    jSONObjectOrder.put("discountName", order.getDiscountReason());
                    // jSONObjectOrder.put("discountValue", order.getFullReduceDishesPrice();
                    jSONObjectOrder.put("erase", order.getOrderErase());
                    jSONObjectOrder.put("hadpaid", order.getOrderHadpaid());
                    jSONObjectOrder.put("recommender", order.getOrderRecommender());
                    jSONObjectOrder.put("reduction", order.getOrderReduction());
                    jSONObjectOrder.put("refund", order.getOrderRefund());

                    OrderBillVO billVO = orderService.calcOrderBill(order, orderDishesList).getData();
                    EnumOrderStatus orderStatus = EnumOrderStatus.of(order.getOrderStatus());
                    String status = orderStatus.remark;
                    jSONObjectOrder.put("status", status);
                    jSONObjectOrder.put("statusCode", orderStatus);
                    jSONObjectOrder.put("totalPrice", CommonUtils.formatMoney(billVO.getTotalPrice()));
                    jSONObjectOrder.put("needPayPrice", CommonUtils.formatMoney(billVO.getOrderNeedPay()));
                    jSONObjectOrder.put("returnDishesPrice", CommonUtils.formatMoney(billVO.getReturnAmount()));
                    jSONObjectOrder.put("discountPrice", CommonUtils.formatMoney(billVO.getDiscountAmount()));
                    jSONObjectReturn.put("orderInfo", jSONObjectOrder);

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
                    //                        jSONObjectReturn.put("orderCoupon", orderCouponJson);
                    //                    }

                    JSONArray jSONArrayDishesArray = new JSONArray();
                    List<OrderDishes> orderDisheses = orderDishesService.selectByOrderId(orderId);
                    if (orderDisheses == null) {
                        orderDisheses = new ArrayList<>();
                    }
                    for (int i = 0; i < orderDisheses.size(); i++) {
                        JSONObject jSONObjectOrderDishes = new JSONObject();
                        OrderDishes orderDishes = orderDisheses.get(i);
                        int dishesId = orderDishes.getDishesId();

                        if (orderDishes.getIfDishesPackage() == 1) {
                            jSONObjectOrderDishes.put("type", "dishesPackage");
                            jSONObjectOrderDishes.put("dishesPackageId", dishesId);
                            DishesPackage dishesPackage = dishesPackageService.getById(orderDishes.getDishesId());
                            jSONObjectOrderDishes.put("dishesPackageName", dishesPackage.getDishesPackageName());
                        } else if (orderDishes.getIfDishesPackage() == 2) {
                            jSONObjectOrderDishes.put("type", "dishesPackage");
                            jSONObjectOrderDishes.put("dishesPackageId", dishesId);
                            DishesPackage dishesPackage = dishesPackageService.getById(orderDishes.getDishesId());
                            jSONObjectOrderDishes.put("dishesPackageName", dishesPackage.getDishesPackageName());
                        } else {
                            jSONObjectOrderDishes.put("type", "dishes");
                            jSONObjectOrderDishes.put("dishesId", dishesId);
                            Dishes dishes = dishesService.getById(dishesId);
                            DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(orderDishes.getDishesPriceId());
                            String dishesPriceString = "";
                            if (dishesPrice != null) {
                                dishesPriceString = "(" + dishesPrice.getDishesPriceName() + ")";
                            }
                            jSONObjectOrderDishes.put("dishesName", dishes.getDishesName() + dishesPriceString);

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
                            jSONObjectOrderDishes.put("dishesAttribute", jSONObjectDishesAttribute);
                        }

                        jSONObjectOrderDishes.put("price", CommonUtils.formatMoney(orderDishes.getOrderDishesPrice()));

                        jSONObjectOrderDishes.put("discountPrice", CommonUtils.formatMoney(orderDishes.getOrderDishesDiscountPrice()));
                        jSONObjectOrderDishes.put("num", orderDishes.getOrderDishesNums());

                        jSONObjectOrderDishes.put("ifrefund", (orderDishes.getOrderDishesIfrefund() == 1));
                        // jSONObjectOrderDishes.put("saleType", orderDishes.getOrderDishesSaleType());
                        jSONObjectOrderDishes.put("dishesStatus", orderDishes.getOrderDishesStatus());

                        jSONArrayDishesArray.add(jSONObjectOrderDishes);
                    }
                    // jSONArrayDishesArray = sortDishesArray(jSONArrayDishesArray);
                    jSONObjectReturn.put("orderDishesArray", jSONArrayDishesArray);
                }
            }

            jSONObjectReturn.put("status", 0);
        } catch (Exception e) {
            jSONObjectReturn = new JSONObject();
            jSONObjectReturn.put("API_TYPE", "checkDeskInfo_ACK");
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", e.getMessage());
        }
        if (msg.containsKey("h5SessionId")) {
            jSONObjectReturn.put("h5SessionId", msg.getIntValue("h5SessionId"));
        }
        return jSONObjectReturn;
    }
}
