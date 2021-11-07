package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CurrentRequest;
import com.xjh.common.utils.Logger;
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
        if (id == null) {
            return null;
        }
        try {
            return deskDAO.getById(id).getData();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getDeskName(Integer deskId) {
        Desk desk = getById(deskId);
        return desk != null ? desk.getDeskName() : "未知";
    }

    public Result<Desk> getByName(String name) {
        if (CommonUtils.isBlank(name)) {
            return null;
        }
        Desk cond = new Desk();
        cond.setDeskName(name);
        Result<List<Desk>> deskRs = deskDAO.select(cond);
        if (CommonUtils.isNotEmpty(deskRs.getData())) {
            return Result.success(deskRs.getData().get(0));
        }
        return Result.fail("找不到数据:" + name);
    }

    public Result<String> openDesk(OpenDeskParam param) {
        Runnable clear = CurrentRequest.resetRequestId();
        try {
            int deskId = param.getDeskId();
            Result<Desk> deskRs = deskDAO.getById(deskId);
            if (!deskRs.isSuccess()) {
                return Result.fail("桌号不存在:" + deskId);
            }
            Desk desk = deskRs.getData();
            if (EnumDeskStatus.of(desk.getStatus()) != EnumDeskStatus.FREE) {
                return Result.fail("餐桌状态错误, 无法开台");
            }
            // 开桌
            Integer orderId = orderService.createNewOrderId();
            desk.setOrderId(orderId);
            deskDAO.useDesk(desk);
            // 下单
            CreateOrderParam createOrderParam = new CreateOrderParam();
            createOrderParam.setOrderId(orderId);
            createOrderParam.setDeskId(deskId);
            createOrderParam.setRecommender(param.getRecommender());
            createOrderParam.setCustomerNum(param.getCustomerNum());
            Order order = orderService.createOrder(createOrderParam);
            Logger.info("下单成功: " + JSON.toJSONString(order));
            return Result.success("下单成功");
        } catch (Exception ex) {
            Logger.info("开桌失败" + ex.getMessage());
            return Result.fail("开桌失败:" + ex.getMessage());
        } finally {
            clear.run();
        }
    }

    public Result<Integer> useDesk(Desk desk) {
        try {
            int i = deskDAO.useDesk(desk);
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Integer> updateDeskByDeskId(Desk desk) {
        try {
            int i = deskDAO.updateDeskByDeskId(desk);
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<String> closeDesk(Integer deskId) {
        Runnable clear = CurrentRequest.resetRequestId();
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
        } finally {
            clear.run();
        }
    }

    public List<Desk> getAllDesks() {
        List<Desk> desks = new ArrayList<>();
        try {
            desks.addAll(deskDAO.select(new Desk()).getData());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }
}
