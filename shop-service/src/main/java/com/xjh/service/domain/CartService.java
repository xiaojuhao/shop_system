package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumSubOrderType;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.domain.model.SendOrderRequest;
import com.xjh.service.store.CartStore;

import cn.hutool.core.codec.Base64;

@Singleton
public class CartService {

    @Inject
    SubOrderDAO subOrderDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    DishesDAO dishesDAO;
    @Inject
    DishesPackageDAO dishesPackageDAO;
    @Inject
    OrderDishesDAO orderDishesDAO;
    @Inject
    OrderService orderService;
    @Inject
    DeskService deskService;
    @Inject
    DishesPriceDAO dishesPriceDAO;

    public Result<CartVO> addItem(Integer deskId, CartItemVO item) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Desk desk = deskService.getById(deskId);
            if (desk == null) {
                return Result.fail("桌号" + deskId + "不存在");
            }
            if (EnumDeskStatus.of(desk.getStatus()) == EnumDeskStatus.FREE) {
                return Result.fail("桌号" + deskId + "未开台");
            }
            CartVO cart = new CartVO();
            cart.setDeskId(deskId);
            List<CartItemVO> contentItems = getCartItems(deskId);
            boolean merged = false;
            // 如果购物车里面已经存在了菜品，则合并
            for(CartItemVO it : contentItems){
                if(Objects.equals(it.getDishesId(), item.getDishesId())){
                    it.setNums(it.getNums() + item.getNums());
                    merged = true;
                    break;
                }
            }
            // 新菜品，添加新的记录
            if(!merged) {
                contentItems.add(item);
            }
            cart.setContents(contentItems);
            Result<String> rs = CartStore.saveCart(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
            return Result.success(cart);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("添加购物车失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public Result<CartVO> updateCart(Integer deskId, CartVO cart) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Result<String> rs = CartStore.saveCart(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
            return Result.success(cart);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新购物失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public List<CartItemVO> getCartItems(Integer deskId) {
        CartVO cart = CartStore.getCart(deskId);
        List<CartItemVO> items = cart.getContents();
        if (items == null) {
            return new ArrayList<>();
        } else {
            return items;
        }
    }

    public Result<CartVO> getCart(Integer deskId) {
        try {
            CartVO cart = CartStore.getCart(deskId);
            return Result.success(cart);
        } catch (Exception ex) {
            Logger.error("getCartOfDesk:" + ex.getMessage());
            return Result.fail(ex.getMessage());
        }
    }

    public Result<String> createSendOrder(SendOrderRequest request) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Integer orderId = request.getOrderId();
            Order order = orderDAO.selectByOrderId(orderId).getData();
            // 子订单
            // Integer subOrderId = subOrderService.createSubOrderId();
            SubOrder subOrder = new SubOrder();
            // subOrder.setSubOrderId(subOrderId);
            subOrder.setOrderId(orderId);
            subOrder.setOrderType(EnumSubOrderType.ORDINARY.getType());
            subOrder.setSubOrderStatus(0);
            subOrder.setAccountId(CurrentAccount.currentAccountId());
            subOrder.setCreatetime(DateBuilder.now().mills());
            Result<Integer> subInsertRs = subOrderDAO.insert(subOrder);
            if(!subInsertRs.isSuccess()){
                return Result.fail(subInsertRs.getMsg());
            }
            // order dishes
            List<OrderDishes> orderDishes = new ArrayList<>();
            orderDishes.add(buildSendOrderDishes(request, orderId, subInsertRs.getData()));
            for (OrderDishes d : orderDishes) {
                orderDishesDAO.insert(d);
            }
            double notPaid = orderService.notPaidBillAmount(order);
            double hadPaid = OrElse.orGet(order.getOrderHadpaid(), 0D);
            if (hadPaid > 0 && notPaid > 0) {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            } else if (hadPaid < 0.01 && notPaid > 0.01) {
                order.setOrderStatus(EnumOrderStatus.UNPAID.status);
            }
            // 更新订单状态
            orderService.updateByOrderId(order);
            Logger.info("赠送菜品: 订单号:" + orderId +
                    ", 菜品:" + request.getDishesName() + "(" + request.getDishesId() + ")");
            return Result.success("下单成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("下单失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public Result<String> createOrder(PlaceOrderFromCartReq param) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Integer deskId = param.getDeskId();
            Integer orderId = param.getOrderId();
            CartVO cartVO = CartStore.getCart(deskId);
            Order order = orderDAO.selectByOrderId(orderId).getData();
            if (order == null) {
                return Result.fail("订单号不存在:" + orderId);
            }
            // 条件校验
            if (cartVO.getContents() == null || cartVO.getContents().size() == 0) {
                Logger.error("购物车空:" + JSON.toJSONString(param));
                return Result.fail("购物车空");
            }
            // 子订单
            // Integer subOrderId = subOrderService.createSubOrderId();
            SubOrder subOrder = new SubOrder();
            // subOrder.setSubOrderId(subOrderId);
            subOrder.setOrderId(orderId);
            subOrder.setOrderType(EnumSubOrderType.ORDINARY.getType());
            subOrder.setSubOrderStatus(0);
            subOrder.setAccountId(CurrentAccount.currentAccountId());
            subOrder.setCreatetime(DateBuilder.now().mills());
            Result<Integer> subInsertRs = subOrderDAO.insert(subOrder);
            if(!subInsertRs.isSuccess()){
                return Result.fail(subInsertRs.getMsg());
            }
            // order dishes
            List<OrderDishes> orderDishes = new ArrayList<>();
            for (CartItemVO item : cartVO.getContents()) {
                int ifPackage = OrElse.orGet(item.getIfDishesPackage(), 0);
                if (ifPackage == 1 || ifPackage == 2) {
                    orderDishes.add(buildPackageCartItemVO(item, orderId, subInsertRs.getData()));
                } else {
                    orderDishes.add(buildCommonCartItemVO(item, orderId, subInsertRs.getData()));
                }
            }
            for (OrderDishes d : orderDishes) {
                orderDishesDAO.insert(d);
            }
            double notPaid = orderService.notPaidBillAmount(order);
            double hadPaid = OrElse.orGet(order.getOrderHadpaid(), 0D);
            if (hadPaid > 0 && notPaid > 0) {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            } else if (hadPaid < 0.01 && notPaid > 0.01) {
                order.setOrderStatus(EnumOrderStatus.UNPAID.status);
            }
            // 清空购物车
            clearCart(deskId);
            // 更新订单状态
            orderService.updateByOrderId(order);
            // 餐桌状态
            EnumOrderStatus orderStatus = EnumOrderStatus.of(order.getOrderStatus());
            if (orderStatus == EnumOrderStatus.UNPAID || orderStatus == EnumOrderStatus.PARTIAL_PAID) {
                Desk updateDesk = new Desk();
                updateDesk.setStatus(EnumDeskStatus.IN_USE.status());
                updateDesk.setDeskId(order.getDeskId());
                deskService.updateDeskByDeskId(updateDesk);
            }
            return Result.success("下单成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("下单失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    private OrderDishes buildCommonCartItemVO(CartItemVO item, Integer orderId, Integer subOrderId) {
        DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(item.getDishesPriceId());

        Dishes dishes = dishesDAO.getById(item.getDishesId());
        OrderDishes d = new OrderDishes();
        d.setOrderId(orderId);
        d.setSubOrderId(subOrderId);
        d.setDishesId(item.getDishesId());
        if (dishesPrice != null) {
            d.setDishesPriceId(dishesPrice.getDishesPriceId());
            d.setOrderDishesPrice(dishesPrice.getDishesPrice());
            d.setOrderDishesDiscountPrice(dishesPrice.getDishesPrice());
        } else {
            d.setDishesPriceId(0);
            d.setOrderDishesPrice(dishes.getDishesPrice());
            d.setOrderDishesDiscountPrice(dishes.getDishesPrice());
        }
        d.setDishesTypeId(dishes.getDishesTypeId());
        d.setCreatetime(DateBuilder.now().mills());
        d.setIfDishesPackage(0);
        d.setOrderDishesIfchange(0);
        d.setOrderDishesIfrefund(0);
        d.setOrderDishesNums(item.getNums());
        d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
        if (item.getDishesAttrs() != null) {
            d.setOrderDishesOptions(Base64.encode(JSONObject.toJSONString(item.getDishesAttrs())));
        } else {
            d.setOrderDishesOptions(Base64.encode("[]"));
        }
        d.setOrderDishesDiscountInfo(Base64.encode(""));

        return d;
    }

    private OrderDishes buildSendOrderDishes(SendOrderRequest item, Integer orderId, Integer subOrderId) {
        Dishes dishes = dishesDAO.getById(item.getDishesId());
        OrderDishes d = new OrderDishes();
        d.setOrderId(orderId);
        d.setSubOrderId(subOrderId);
        d.setDishesId(item.getDishesId());
        d.setDishesPriceId(0);
        d.setDishesTypeId(dishes.getDishesTypeId());
        d.setOrderDishesPrice(0D);
        d.setOrderDishesDiscountPrice(0D);
        d.setCreatetime(DateBuilder.now().mills());
        d.setIfDishesPackage(0);
        d.setOrderDishesIfchange(0);
        d.setOrderDishesIfrefund(0);
        d.setOrderDishesNums(1);
        d.setOrderDishesSaletype(EnumOrderSaleType.SEND.type);
        d.setOrderDishesOptions(Base64.encode("[]"));
        d.setOrderDishesDiscountInfo(Base64.encode(""));

        return d;
    }


    private OrderDishes buildPackageCartItemVO(CartItemVO item, Integer orderId, Integer subOrderId) {
        DishesPackage dishes = dishesPackageDAO.getById(item.getDishesId());
        OrderDishes d = new OrderDishes();
        d.setOrderId(orderId);
        d.setSubOrderId(subOrderId);
        d.setDishesId(item.getDishesId());
        d.setDishesPriceId(0);
        d.setDishesTypeId(dishes.getDishesPackageType());
        d.setOrderDishesPrice(dishes.getDishesPackagePrice());
        d.setOrderDishesDiscountPrice(dishes.getDishesPackagePrice());
        d.setCreatetime(DateBuilder.now().mills());
        d.setIfDishesPackage(1);
        d.setOrderDishesIfchange(0);
        d.setOrderDishesIfrefund(0);
        d.setOrderDishesNums(OrElse.orGet(item.getNums(), 1));
        d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
        d.setOrderDishesOptions(Base64.encode("[]"));
        d.setOrderDishesDiscountInfo(Base64.encode(""));

        return d;
    }


    public Result<String> clearCart(Integer deskId) {
        return CartStore.clearCart(deskId);
    }
}
