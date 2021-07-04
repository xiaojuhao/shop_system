package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.DeskDAO;
import com.xjh.dao.mapper.InfoDAO;
import com.xjh.service.domain.model.CreateOrderParam;
import com.xjh.service.domain.model.OpenDeskParam;

@Singleton
public class DeskService {
    @Inject
    OrderService orderService;
    @Inject
    DeskDAO deskDAO;
    @Inject
    InfoDAO infoDAO;

    public Desk getById(Integer id) {
        try {
            return deskDAO.getById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void openDesk(OpenDeskParam param) {
        try {
            int deskId = param.getDeskId();
            Desk desk = deskDAO.getById(deskId);
            if (desk == null) {
                AlertBuilder.ERROR("开桌失败", "桌号不存在:" + deskId);
                return;
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
        } catch (Exception ex) {
            LogUtils.info("开桌失败" + ex.getMessage());
            AlertBuilder.ERROR("下单失败", "开桌失败:" + ex.getMessage());
        }
    }

    public void closeDesk(Integer deskId) {
        try {
            deskDAO.freeDesk(deskId);
        } catch (Exception ex) {
            ex.printStackTrace();
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
