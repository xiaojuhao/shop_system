package com.xjh.service.domain;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.*;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.common.valueobject.CartVO;
import com.xjh.common.valueobject.OrderDiscountVO;
import com.xjh.dao.dataobject.*;
import com.xjh.dao.mapper.*;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.domain.model.SendOrderRequest;
import com.xjh.service.printers.OrderPrinterHelper;
import com.xjh.service.printers.PrintResult;
import com.xjh.service.printers.Printer;
import com.xjh.service.printers.PrinterImpl;
import com.xjh.service.ws.NotifyService;
import com.xjh.service.ws.SocketUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.xjh.common.utils.CommonUtils.jsonToObj;

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
    OrderPrinterHelper orderPrinterHelper;
    @Inject
    DishesService dishesService;
    @Inject
    PrinterDishDAO printerDishDAO;
    @Inject
    PrinterDAO printerDAO;
    @Inject
    PrinterTaskDAO printerTaskDAO;
    @Inject
    OrderDishesDAO orderDishesDAO;
    @Inject
    OrderService orderService;
    @Inject
    DeskService deskService;
    @Inject
    CartDAO cartDAO;
    @Inject
    DishesPriceDAO dishesPriceDAO;
    @Inject
    OrderDishesService orderDishesService;
    @Inject
    NotifyService notifyService;

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
            for (CartItemVO it : contentItems) {
                if (Objects.equals(it.getDishesId(), item.getDishesId())) {
                    it.setNums(it.getNums() + item.getNums());
                    merged = true;
                    break;
                }
            }
            // 新菜品，添加新的记录
            if (!merged) {
                contentItems.add(item);
            }
            cart.setContents(contentItems);
            // Result<String> rs = CartStore.saveCart(cart);
            Result<String> rs = cartDAO.save(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
            // 通知到前段
            notifyService.cartAddOneRecord(deskId, item);

            return Result.success(cart);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("添加购物车失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public int cartSize(CartVO cartVO){
        int total = 0;
        if(cartVO == null || cartVO.getContents() == null){
            return total;
        }
        for(CartItemVO item : cartVO.getContents()){
            total += CommonUtils.parseInt(item.getNums(), 0);
        }
        return total;
    }

    public Result<CartVO> updateCart(Integer deskId, CartVO cart) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
//            CartVO oldCart = getCart(deskId).getData();
//            Set<Integer> chg = cmpCartNum(cart, oldCart);
            // Result<String> rs = CartStore.saveCart(cart);
            Result<String> rs = cartDAO.save(cart);
            if (!rs.isSuccess()) {
                return Result.fail("添加购物车失败,保存数据库失败");
            }
//            if (!chg.isEmpty()) {
//                notifyCenter.notifyFrontAddCart(deskId, cart.getContents().get(firstOf(chg)));
//            }
            return Result.success(cart);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新购物失败," + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public List<CartItemVO> getCartItems(Integer deskId) {
        // CartVO cart = CartStore.getCart(deskId);
        CartVO cart = cartDAO.getDeskCard(deskId);
        List<CartItemVO> items = cart.getContents();
        if (items == null) {
            return new ArrayList<>();
        } else {
            return items;
        }
    }

    public Result<CartVO> getCart(Integer deskId) {
        try {
            // CartVO cart = CartStore.getCart(deskId);
            CartVO cart = cartDAO.getDeskCard(deskId);
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
            if (!subInsertRs.isSuccess()) {
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
            Logger.info("赠送菜品: 订单号:" + orderId + ", 菜品:" + request.getDishesName() + "(" + request.getDishesId() + ")");

            NotifyService.notifyCartCleared(request.getDeskId());

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
            Desk desk = deskService.getById(deskId);

            Integer orderId = param.getOrderId();

            Order order = orderDAO.selectByOrderId(orderId).getData();
            if (order == null) {
                return Result.fail("订单号不存在:" + orderId);
            }
            Result<CartVO> cartRs = getCart(deskId);
            // 条件校验
            if (!cartRs.isSuccess() || CommonUtils.isEmpty(cartRs.getData().getContents())) {
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
            if (!subInsertRs.isSuccess()) {
                return Result.fail(subInsertRs.getMsg());
            }
            // order dishes
            List<OrderDishes> orderDishes = new ArrayList<>();
            for (CartItemVO item : cartRs.getData().getContents()) {
                int ifPackage = OrElse.orGet(item.getIfDishesPackage(), 0);
                if (ifPackage == 1 || ifPackage == 2) {
                    orderDishes.addAll(buildPackageCartItemVO(item, order, subInsertRs.getData()));
                } else {
                    orderDishes.addAll(buildCommonCartItemVO(item, order, subInsertRs.getData()));
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

            // 通知前端更新购物车
            SocketUtils.delay(() -> NotifyService.notifyCartCleared(deskId), 0);


            printKitchen0(order, subOrder, desk, orderDishes);

            // 后厨打印
            printKitchen(order, subOrder, orderDishes);

            return Result.success("下单成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("下单失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    private void printKitchen0(Order order, SubOrder subOrder, Desk desk, List<OrderDishes> subOrderContainDishes) throws Exception {
        PrinterTaskDO task = printerTaskDAO.selectByPrintTaskName("api.print.task.PrintTaskOrderSample");
        if (task == null) {
            AlertBuilder.ERROR("打印机配置错误,请检查");
            return;
        }
        JSONObject taskContent = JSON.parseObject(Base64.decodeStr(task.getPrintTaskContent()));
        JSONArray array = JSON.parseArray(taskContent.getString("printerSelectStrategy"));
        if (array == null) {
            AlertBuilder.ERROR("打印机配置错误,请检查2");
            return;
        }
        Integer printerId = null;
        for (int i = 0; i < array.size(); i++) {
            JSONObject conf = array.getJSONObject(i);
            if (CommonUtils.eq(conf.getInteger("deskTypeId"), desk.getBelongDeskType())) {
                printerId = conf.getInteger("printerId");
            }
        }
        PrinterDO dd = printerDAO.selectByPrinterId(printerId);
        if (dd == null) {
            AlertBuilder.ERROR("打印机配置错误,请检查3");
            return;
        }
        List<Object> tickets = orderPrinterHelper.buildKitchenPrintData0(order, subOrder, subOrderContainDishes);
        PrinterImpl printerImpl = new PrinterImpl(dd);
        PrintResult rs = printerImpl.print(tickets, true);
        Logger.info(JSON.toJSONString(rs));
    }

    private void printKitchen(Order order, SubOrder subOrder, List<OrderDishes> orderDishes) throws Exception {
        // 打印小票
        for (OrderDishes d : orderDishes) {
            Dishes dishes = dishesService.getById(d.getDishesId());
            // 不需要打印
            if(dishes == null || OrElse.orGet(dishes.getIfNeedPrint(), 0) == 0){
                continue;
            }
            PrinterDishDO printer = printerDishDAO.queryByDishesId(dishes.getDishesId());
            if(printer == null){
                continue;
            }
            PrinterDO dd = printerDAO.selectByPrinterId(printer.getPrinterId());
            if (dd == null) {
                continue;
            }
            List<Object> tickets = orderPrinterHelper.buildKitchenPrintData(order, subOrder, d, dishes);
            PrinterImpl printerImpl = new PrinterImpl(dd);
            PrintResult rs = printerImpl.print(tickets, true);
            Logger.info(JSON.toJSONString(rs));
        }
    }

    private List<OrderDishes> buildCommonCartItemVO(CartItemVO item, Order order, Integer subOrderId) {
        Predicate<OrderDishes> discountChecker = orderDishesService.discountableChecker();
        DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(item.getDishesPriceId());
        List<OrderDishes> list = new ArrayList<>();
        Dishes dishes = dishesDAO.getById(item.getDishesId());
        OrderDishes example = new OrderDishes();
        example.setDishesId(dishes.getDishesId());
        example.setIfDishesPackage(0);
        double discountRate = 1L;
        String discountStr = "";
        if(discountChecker.test(example)) {
            OrderDiscountVO discount = jsonToObj(order.getOrderDiscountInfo(), OrderDiscountVO.class);
            if (discount != null) {
                discountRate = discount.getRate() > 0 ? discount.getRate() : 1;
            }
            discountStr = JSONObject.toJSONString(discount);
        }
        for (int i = 0; i < item.getNums(); i++) {
            OrderDishes d = new OrderDishes();
            d.setOrderId(order.getOrderId());
            d.setSubOrderId(subOrderId);
            d.setDishesId(item.getDishesId());
            d.setDishesTypeId(dishes.getDishesTypeId());
            d.setCreatetime(DateBuilder.now().mills());
            d.setIfDishesPackage(0);
            d.setOrderDishesIfchange(0);
            d.setOrderDishesIfrefund(0);
            d.setOrderDishesNums(1);
            d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
            String options = JSONObject.toJSONString(OrElse.orGet(item.getDishesAttrs(), new ArrayList<>()));
            d.setOrderDishesOptions(Const.KEEP_BASE64 ? Base64.encode(options) : options);
            if (dishesPrice != null) {
                d.setDishesPriceId(dishesPrice.getDishesPriceId());
                d.setOrderDishesPrice(dishesPrice.getDishesPrice());
                d.setOrderDishesDiscountPrice(dishesPrice.getDishesPrice() * discountRate);
            } else {
                d.setDishesPriceId(0);
                d.setOrderDishesPrice(dishes.getDishesPrice());
                d.setOrderDishesDiscountPrice(dishes.getDishesPrice() * discountRate);
            }
            d.setOrderDishesDiscountInfo(Const.KEEP_BASE64 ? Base64.encode(discountStr) : discountStr);

            list.add(d);
        }
        return list;
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


    private List<OrderDishes> buildPackageCartItemVO(CartItemVO item, Order order, Integer subOrderId) {
        List<OrderDishes> list = new ArrayList<>();
        DishesPackage dishes = dishesPackageDAO.getByDishesPackageId(item.getDishesId());
//        OrderDiscountVO discount = jsonToObj(order.getOrderDiscountInfo(), OrderDiscountVO.class);
        double discountRate = 1L;
//        if(discount != null){
//            discountRate = discount.getRate() > 0 ? discount.getRate() : 1;
//        }
        String discountStr = "";//JSONObject.toJSONString(discount);
        for (int i = 0; i < OrElse.orGet(item.getNums(), 1); i++) {
            OrderDishes d = new OrderDishes();
            d.setOrderId(order.getOrderId());
            d.setSubOrderId(subOrderId);
            d.setDishesId(item.getDishesId());
            d.setDishesPriceId(0);
            d.setDishesTypeId(dishes.getDishesPackageType());
            d.setOrderDishesPrice(dishes.getDishesPackagePrice());
            d.setOrderDishesDiscountPrice(dishes.getDishesPackagePrice() * discountRate);
            d.setCreatetime(DateBuilder.now().mills());
            d.setIfDishesPackage(1);
            d.setOrderDishesIfchange(0);
            d.setOrderDishesIfrefund(0);
            d.setOrderDishesNums(1);
            d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
            d.setOrderDishesOptions(Const.KEEP_BASE64 ? Base64.encode("[]") : "[]");
            d.setOrderDishesDiscountInfo(Const.KEEP_BASE64 ? Base64.encode(discountStr) : discountStr);

            list.add(d);
        }

        return list;
    }


    public Result<String> clearCart(Integer deskId) {
        // return CartStore.clearCart(deskId);
        return cartDAO.clearCart(deskId);
    }


    public BigDecimal sumCartPrice(CartVO cart) {
        double allPrice = 0;
        if (cart == null || CommonUtils.isEmpty(cart.getContents())) {
            return BigDecimal.ZERO;
        }
        for (CartItemVO cartItem : cart.getContents()) {
            int dishesId = cartItem.getDishesId();
            if (cartItem.getIfDishesPackage() == EnumIsPackage.YES.code) {
                DishesPackage dishesPackage = dishesPackageDAO.getByDishesPackageId(dishesId);
                int nums = cartItem.getNums();
                allPrice = allPrice + dishesPackage.getDishesPackagePrice() * nums;
            } else if (cartItem.getIfDishesPackage() == EnumIsPackage.YES_NEW.code) {
                DishesPackage dishesPackage = dishesPackageDAO.getByDishesPackageId(dishesId);
                int nums = cartItem.getNums();
                allPrice = allPrice + dishesPackage.getDishesPackagePrice() * nums;
            } else {
                Dishes dishes = dishesDAO.getById(dishesId);
                int nums = cartItem.getNums();
                DishesPrice dishesPrice = dishesPriceDAO.queryByPriceId(cartItem.getDishesPriceId());
                if (dishesPrice != null) {
                    allPrice = (float) (allPrice + dishesPrice.getDishesPrice() * nums);
                } else {
                    allPrice = allPrice + dishes.getDishesPrice() * nums;
                }
            }

        }
        return BigDecimal.valueOf(allPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
