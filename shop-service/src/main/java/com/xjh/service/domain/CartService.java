package com.xjh.service.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.domain.model.SendOrderRequest;
import com.xjh.service.store.CartStore;

import cn.hutool.core.codec.Base64;

@Singleton
public class CartService {
    //    @Inject
    //    CartDAO cartDAO;
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

    public Result<CartVO> addItem(Integer deskId, CartItemVO item) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Desk desk = deskService.getById(deskId);
            if (desk == null) {
                return Result.fail("桌号" + deskId + "不存在");
            }
            if (EnumDesKStatus.of(desk.getStatus()) == EnumDesKStatus.FREE) {
                return Result.fail("桌号" + deskId + "未开台");
            }
            Cart cart = new Cart();
            cart.setDeskId(deskId);
            List<CartItemVO> contentItems = selectByDeskId(deskId);
            boolean exists = false;
            for (CartItemVO vo : contentItems) {
                if (Objects.equals(vo.getDishesId(), item.getDishesId())) {
                    vo.setNums(vo.getNums() + item.getNums());
                    exists = true;
                }
            }
            if (!exists) {
                contentItems.add(item);
            }
            cart.setContents(Base64.encode(JSON.toJSONString(contentItems)));
            Result<String> rs = CartStore.saveCart(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
            return Result.success(CartVO.from(cart));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("添加购物车失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public Result<CartVO> updateCart(Integer deskId, CartVO vo) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Cart cart = new Cart();
            cart.setDeskId(deskId);
            cart.setContents(Base64.encode(JSON.toJSONString(vo.getContents())));
            Result<String> rs = CartStore.saveCart(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
            return Result.success(CartVO.from(cart));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新购物失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public List<CartItemVO> selectByDeskId(Integer deskId) throws Exception {
        Cart cart = CartStore.getCart(deskId);
        if (cart != null) {
            String contents = cart.getContents();
            if (CommonUtils.isNotBlank(contents)) {
                contents = Base64.decodeStr(contents);
                return JSONArray.parseArray(contents, CartItemVO.class);
            }
        }
        return new ArrayList<>();
    }

    public Result<CartVO> getCartOfDesk(Integer deskId) {
        try {
            Cart cart = CartStore.getCart(deskId);
            return Result.success(CartVO.from(cart));
        } catch (Exception ex) {
            Logger.error("getCartOfDesk:" + ex.getMessage());
            return Result.fail(ex.getMessage());
        }
    }

    public Result<String> createSendOrder(SendOrderRequest request) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            Integer orderId = request.getOrderId();
            Order order = orderDAO.selectByOrderId(orderId);
            // 子订单
            Integer subOrderId = createSubOrderId();
            SubOrder subOrder = new SubOrder();
            subOrder.setSubOrderId(subOrderId);
            subOrder.setOrderId(orderId);
            subOrder.setOrderType(0);
            subOrder.setSubOrderStatus(0);
            subOrder.setAccountId(0);
            subOrder.setCreatetime(DateBuilder.now().mills());
            int subInsertRs = subOrderDAO.insert(subOrder);

            // order dishes
            List<OrderDishes> orderDishes = new ArrayList<>();
            orderDishes.add(buildSendOrderDishes(request, orderId, subOrderId));
            for (OrderDishes d : orderDishes) {
                orderDishesDAO.insert(d);
            }
            double notPaid = orderService.notPaidBillAmount(order);
            double hadPaid = CommonUtils.orElse(order.getOrderHadpaid(), 0D);
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
            Cart cart = CartStore.getCart(deskId);
            CartVO cartVO = CartVO.from(cart);
            Order order = orderDAO.selectByOrderId(orderId);
            if (order == null) {
                return Result.fail("订单号不存在:" + orderId);
            }
            // 条件校验
            if (cartVO.getContents() == null || cartVO.getContents().size() == 0) {
                Logger.error("购物车空:" + JSON.toJSONString(param));
                return Result.fail("购物车空");
            }
            // 子订单
            Integer subOrderId = createSubOrderId();
            SubOrder subOrder = new SubOrder();
            subOrder.setSubOrderId(subOrderId);
            subOrder.setOrderId(orderId);
            subOrder.setOrderType(0);
            subOrder.setSubOrderStatus(0);
            subOrder.setAccountId(0);
            subOrder.setCreatetime(DateBuilder.now().mills());
            int subInsertRs = subOrderDAO.insert(subOrder);

            // order dishes
            List<OrderDishes> orderDishes = new ArrayList<>();
            for (CartItemVO item : cartVO.getContents()) {
                int ifPackage = CommonUtils.orElse(item.getIfDishesPackage(), 0);
                if (ifPackage == 1 || ifPackage == 2) {
                    orderDishes.add(buildPackageCartItemVO(item, orderId, subOrderId));
                } else {
                    orderDishes.add(buildCommonCartItemVO(item, orderId, subOrderId));
                }
            }
            for (OrderDishes d : orderDishes) {
                orderDishesDAO.insert(d);
            }
            double notPaid = orderService.notPaidBillAmount(order);
            double hadPaid = CommonUtils.orElse(order.getOrderHadpaid(), 0D);
            if (hadPaid > 0 && notPaid > 0) {
                order.setOrderStatus(EnumOrderStatus.PARTIAL_PAID.status);
            } else if (hadPaid < 0.01 && notPaid > 0.01) {
                order.setOrderStatus(EnumOrderStatus.UNPAID.status);
            }
            // 清空购物车
            clearCart(deskId);
            // 更新订单状态
            orderService.updateByOrderId(order);
            return Result.success("下单成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("下单失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    private OrderDishes buildCommonCartItemVO(CartItemVO item, Integer orderId, Integer subOrderId) {
        Dishes dishes = dishesDAO.getById(item.getDishesId());
        OrderDishes d = new OrderDishes();
        d.setOrderId(orderId);
        d.setSubOrderId(subOrderId);
        d.setDishesId(item.getDishesId());
        d.setDishesPriceId(0);
        d.setDishesTypeId(dishes.getDishesTypeId());
        d.setOrderDishesPrice(dishes.getDishesPrice());
        d.setOrderDishesDiscountPrice(dishes.getDishesPrice());
        d.setCreatetime(DateBuilder.now().mills());
        d.setIfDishesPackage(0);
        d.setOrderDishesIfchange(0);
        d.setOrderDishesIfrefund(0);
        d.setOrderDishesNums(item.getNums());
        d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
        d.setOrderDishesOptions(Base64.encode("[]"));
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
        d.setOrderDishesNums(CommonUtils.orElse(item.getNums(), 1));
        d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
        d.setOrderDishesOptions(Base64.encode("[]"));
        d.setOrderDishesDiscountInfo(Base64.encode(""));

        return d;
    }


    public Result<String> clearCart(Integer deskId) {
        return CartStore.clearCart(deskId);
    }

    public Integer createSubOrderId() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        String timeStr = DateBuilder.today().format("yyyyMMddHH");
        int diffHours = (int) DateBuilder.diffHours(start, DateBuilder.base(timeStr).dateTime());
        if (diffHours <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + timeStr);
        }
        int nextId = nextId(timeStr);
        if (nextId >= 2 << 15) {
            throw new RuntimeException("循环次数已用完:" + timeStr);
        }
        // 前17位保存时间，后15位保存序列号
        int id = (diffHours << 15) | nextId;
        Logger.info("创建子订单号: " + diffHours + "," + nextId + "," + id);
        return id;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("subOrderId:sequence:" + group);
    }
}
