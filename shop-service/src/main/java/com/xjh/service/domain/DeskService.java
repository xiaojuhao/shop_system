package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.service.domain.model.CreateOrderParam;
import com.xjh.service.domain.model.OpenDeskParam;

@Singleton
public class DeskService {
    @Inject
    OrderService orderService;
    @Inject
    DeskDAO deskDAO;
    @Inject
    CartService cartService;

    public Desk getById(Integer id) {
        try {
            return deskDAO.getById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Result<String> openDesk(OpenDeskParam param) {
        try {
            int deskId = param.getDeskId();
            Desk desk = deskDAO.getById(deskId);
            if (desk == null) {
                return Result.fail("桌号不存在:" + deskId);
            }
            // 开桌
            Integer orderId = orderService.createNewOrderId();
            desk.setOrderId(orderId);
            deskDAO.useDesk(desk);
            // 下单
            CreateOrderParam createOrderParam = new CreateOrderParam();
            createOrderParam.setOrderId(orderId);
            createOrderParam.setDeskId(deskId);
            createOrderParam.setCustomerNum(param.getCustomerNum());
            Order order = orderService.createOrder(createOrderParam);
            LogUtils.info("下单成功: " + JSON.toJSONString(order));
            return Result.success("下单成功");
        } catch (Exception ex) {
            LogUtils.info("开桌失败" + ex.getMessage());
            return Result.fail("开桌失败:" + ex.getMessage());
        }
    }

    public Result<String> closeDesk(Integer deskId) {
        try {
            int i = deskDAO.freeDesk(deskId);
            if (i == 0) {
                return Result.fail("关台失败, 更新数据库失败");
            } else {
                // 清空购物车
                cartService.clearCart(deskId);
                return Result.success("关台成功");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("关台失败," + ex.getMessage());
        }
    }

    public List<Desk> getAllDesks() {
        List<Desk> desks = new ArrayList<>();
        try {
            desks.addAll(deskDAO.select(new Desk()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }
}
