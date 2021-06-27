package com.xjh.service.domain;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.store.DeskKvDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Cart;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.mapper.CartDAO;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.dao.mapper.SubOrderDAO;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;

import cn.hutool.core.codec.Base64;

@Singleton
public class CartService {
    @Inject
    CartDAO cartDAO;
    @Inject
    SubOrderDAO subOrderDAO;
    @Inject
    OrderDAO orderDAO;
    @Inject
    DishesDAO dishesDAO;
    @Inject
    OrderDishesDAO orderDishesDAO;

    public CartVO addItem(Integer deskId, CartItemVO item) throws Exception {
        Cart cart = new Cart();
        cart.setDeskId(deskId);
        List<CartItemVO> contentItems = selectByDeskId(deskId);
        contentItems.add(item);
        cart.setContents(Base64.encode(JSON.toJSONString(contentItems)));
        int i = cartDAO.save(cart);
        if (i == 0) {
            return null;
        }
        return CartVO.from(cart);
    }

    public List<CartItemVO> selectByDeskId(Integer deskId) throws Exception {
        Cart cart = cartDAO.selectByDeskId(deskId);
        if (cart != null) {
            String contents = cart.getContents();
            if (CommonUtils.isNotBlank(contents)) {
                contents = Base64.decodeStr(contents);
                return JSONArray.parseArray(contents, CartItemVO.class);
            }
        }
        return new ArrayList<>();
    }

    public void createOrder(PlaceOrderFromCartReq param) throws Exception {
        Integer deskId = param.getDeskId();
        Integer orderId = param.getOrderId();
        Cart cart = cartDAO.selectByDeskId(deskId);
        CartVO cartVO = CartVO.from(cart);
        Order order = orderDAO.selectByOrderId(orderId);
        // 条件校验
        if (cartVO.getContents() == null || cartVO.getContents().size() == 0) {
            LogUtils.error("购物车空:" + JSON.toJSONString(param));
            return;
        }
        Integer subOrderId = createNewId();
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
            Dishes dishes = dishesDAO.getById(item.getDishesId());
            OrderDishes d = new OrderDishes();
            orderDishes.add(d);
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
            d.setOrderDishesNums(1);
            d.setOrderDishesNums(item.getNums());
            d.setOrderDishesSaletype(EnumOrderSaleType.NORMAL.type);
            d.setOrderDishesOptions("");
            d.setOrderDishesDiscountInfo("");
            orderDishesDAO.insert(d);
        }

        cartDAO.clearCart(deskId);
    }

    public Integer createNewId() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        LocalDateTime today = DateBuilder.now().dateTime();
        String todayStr = DateBuilder.today().format("yyyyMMdd");
        int diffDays = (int) DateBuilder.diffDays(start, today);
        if (diffDays <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + today);
        }
        int nextId = nextId("subOrderId:sequence:" + todayStr);
        // 前16位保存时间，后16位保存序列号
        return diffDays << 18 | (nextId % 262143);
    }

    public synchronized int nextId(String group) {
        String key = "sequence_" + group;
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = DeskKvDatabase.getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
        int newId = 0;
        try {
            if (status == OperationStatus.SUCCESS) {
                String value = new String(theData.getData());
                newId = CommonUtils.parseInt(value, 1);
            } else if (status == OperationStatus.NOTFOUND) {
                newId = 1;
            }
            DatabaseEntry newData = new DatabaseEntry(String.valueOf(newId + 1).getBytes(StandardCharsets.UTF_8));
            db.put(txn, theKey, newData);
        } catch (Exception ex) {
            LogUtils.error("获取订单ID失败:" + group + "," + ex.getMessage());
            throw new RuntimeException("获取订单ID序列失败");
        }
        txn.commit();
        return newId;
    }
}
