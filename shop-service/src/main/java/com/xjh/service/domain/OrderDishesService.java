package com.xjh.service.domain;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.xjh.common.enumeration.EnumOrderSaleType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DishesAttributeHelper;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.xjh.service.domain.model.DishesSaleStatModel;
import com.xjh.service.domain.model.DishesSaleStatReq;
import com.xjh.service.domain.model.DishesTypeSaleStatModel;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.xjh.common.utils.CommonUtils.stringify;
import static com.xjh.common.utils.DateBuilder.base;
import static com.xjh.service.domain.DishesTypeService.toDishesTypeName;

@Singleton
public class OrderDishesService {
    @Inject
    OrderDishesDAO orderDishesDAO;
    @Inject
    StoreService storeService;
    @Inject
    DishesService dishesService;
    @Inject
    DishesTypeService dishesTypeService;

    public Result<Integer> updatePrimaryKey(OrderDishes update) {
        try {
            if (update == null) {
                return Result.fail("入参错误");
            }
            int i = orderDishesDAO.updateByPK(update);
            if (i == 0) {
                return Result.fail("更新失败");
            } else {
                return Result.success(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public List<OrderDishes> selectBySubOrderId(Integer subOrderId) {
        if (subOrderId == null) {
            return new ArrayList<>();
        }
        OrderDishes c = new OrderDishes();
        c.setSubOrderId(subOrderId);
        return orderDishesDAO.select(c);
    }

    public List<OrderDishes> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        OrderDishes c = new OrderDishes();
        c.setOrderId(orderId);
        return orderDishesDAO.select(c);
    }

    public OrderDishes selectById(Integer orderDishesId){
        return CommonUtils.firstOf(selectByIdList(Lists.newArrayList(orderDishesId)));
    }

    public List<OrderDishes> selectByIdList(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return orderDishesDAO.selectByIds(ids);
    }

    public int returnOrderDishes(OrderDishes orderDishes) {
        try {
            OrderDishes update = new OrderDishes();
            update.setOrderDishesId(orderDishes.getOrderDishesId());
            update.setOrderDishesSaletype(EnumOrderSaleType.RETURN.type);
            update.setOrderDishesIfrefund(1);
            return orderDishesDAO.updateByPK(update);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public Predicate<OrderDishes> discountableChecker() {
        Set<Integer> discountableDishesIds = storeService.getStoreDiscountableDishesIds();
        return o -> discountableDishesIds.contains(o.getDishesId()) && o.getIfDishesPackage() == 0;
    }

    public String generateAttrDigest(OrderDishes orderDishes) {
        if (CommonUtils.isBlank(orderDishes.getOrderDishesOptions())) {
            return "";
        }
        String options = Base64.decodeStr(orderDishes.getOrderDishesOptions());
        List<DishesAttributeVO> attrs = JSONArray.parseArray(options, DishesAttributeVO.class);
        return DishesAttributeHelper.generateSelectedAttrDigest(attrs);
    }

    public Result<List<DishesSaleStatModel>> statSales(DishesSaleStatReq req) {
        String sql = "select dishesId,ifDishesPackage,dishesPriceId,sum(orderDishesNums) as count,sum(orderDishesPrice) as allPrice ";
        sql += "from  order_dishes_list where 1 = 1 ";
        if (req.getStartDate() != null) {
            sql += " and createtime >= " + base(req.getStartDate()).mills();
        }
        if (req.getEndDate() != null) {
            sql += " and createtime <= " + base(req.getEndDate()).mills();
        }
        sql += " GROUP BY dishesId,ifDishesPackage,dishesPriceId order by count desc";
        Result<List<DishesSaleStatModel>> rs = orderDishesDAO.query(sql, DishesSaleStatModel.class);
        if (CollectionUtils.isNotEmpty(rs.getData())) {
            for (DishesSaleStatModel m : rs.getData()) {
                m.setDishesName(stringify(m.getDishesId()));
                Dishes dishes = dishesService.getById(m.getDishesId());
                if (dishes != null) {
                    m.setDishesName(dishes.getDishesName());
                }
            }
        }
        return rs;
    }

    public Result<List<DishesTypeSaleStatModel>> statSalesType(DishesSaleStatReq req) {
        String sql = "select dishesTypeId,sum(orderDishesNums) as count,sum(orderDishesPrice) as allPrice ";
        sql += " from  order_dishes_list  where 1 = 1  ";
        if (req.getStartDate() != null) {
            sql += " and createtime >= " + base(req.getStartDate()).mills();
        }
        if (req.getEndDate() != null) {
            sql += " and createtime <= " + base(req.getEndDate()).mills();
        }
        sql += " GROUP BY dishesTypeId order by count desc";
        Result<List<DishesTypeSaleStatModel>> rs = orderDishesDAO.query(sql, DishesTypeSaleStatModel.class);

        if (CollectionUtils.isNotEmpty(rs.getData())) {
            Map<Integer, DishesType> typeMap = dishesTypeService.dishesTypeMap();
            for (DishesTypeSaleStatModel m : rs.getData()) {
                m.setDishesTypeName(toDishesTypeName(typeMap, m.getDishesTypeId()));
            }
        }

        return rs;
    }
}
