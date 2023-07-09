package com.xjh.service.domain;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.DishesGroupVO;
import com.xjh.dao.dataobject.DishesGroup;
import com.xjh.dao.dataobject.Store;
import com.xjh.dao.mapper.DishesGroupDAO;
import com.xjh.dao.mapper.StoreDAO;
import com.xjh.service.domain.model.StoreVO;

import javax.inject.Inject;
import java.util.*;

@Singleton
public class StoreService {
    @Inject
    StoreDAO storeDAO;
    @Inject
    DishesGroupDAO dishesGroupDAO;

    public Result<StoreVO> getStore() {
        try {
            List<Store> list = storeDAO.selectList(new Store());
            if (list.size() == 0) {
                return Result.fail("没有查询到任何store记录");
            }
            return Result.success(toVO(list.get(0)));
        } catch (Exception ex) {
            Logger.error("查询store异常:" + ex.getMessage());
            return Result.fail("查询store异常:" + ex.getMessage());
        }
    }

    public Set<Integer> getStoreDiscountableDishesIds() {
        Set<Integer> discountableDishesIds = new HashSet<>();
        StoreVO store = this.getStore().getData();
        if (store != null) {
            CommonUtils.forEach(store.getStoreDishesGroups(), g -> {
                if (g.getGroupIds() != null) {
                    discountableDishesIds.addAll(g.getGroupIds());
                }
            });
        }
        return discountableDishesIds;
    }

    public StoreVO toVO(Store store) throws Exception {
        StoreVO vo = new StoreVO();
        CopyUtils.copy(store, vo);
        if (CommonUtils.isNotBlank(store.getStoreDishesGroupIds())) {
            vo.setStoreDishesGroups(new ArrayList<>());
            String a = Base64.decodeStr(store.getStoreDishesGroupIds());
            List<Integer> arr = JSONArray.parseArray(a, Integer.class);
            for (Integer id : arr) {
                DishesGroup group = dishesGroupDAO.selectByDishesGroupId(id);
                if (group != null) {
                    DishesGroupVO groupVO = new DishesGroupVO();
                    groupVO.setDishesGroupId(group.getDishesGroupId());
                    groupVO.setDishesGroupName(group.getDishesGroupName());
                    groupVO.setCreateTime(group.getCreateTime());
                    groupVO.setGroupIds(new ArrayList<>());
                    if (CommonUtils.isNotBlank(group.getDishesGroupContent())) {
                        String json = group.getDishesGroupContent();
                        if (!json.contains("[") && !json.contains("{")) {
                            json = Base64.decodeStr(group.getDishesGroupContent());
                        }
                        groupVO.getGroupIds().addAll(JSONArray.parseArray(json, Integer.class));
                    }
                    vo.getStoreDishesGroups().add(groupVO);
                }
            }
        }

        if (CommonUtils.isNotBlank(store.getManagerDishesGroupIds())) {
            vo.setManagerDishesGroups(new ArrayList<>());
            String a = Base64.decodeStr(store.getManagerDishesGroupIds());
            List<Integer> arr = JSONArray.parseArray(a, Integer.class);
            for (Integer id : arr) {
                DishesGroup group = dishesGroupDAO.selectByDishesGroupId(id);
                if (group != null) {
                    DishesGroupVO groupVO = new DishesGroupVO();
                    groupVO.setDishesGroupId(group.getDishesGroupId());
                    groupVO.setDishesGroupName(group.getDishesGroupName());
                    groupVO.setCreateTime(group.getCreateTime());
                    groupVO.setGroupIds(new ArrayList<>());
                    if (CommonUtils.isNotBlank(group.getDishesGroupContent())) {
                        String json = group.getDishesGroupContent();
                        if (!json.contains("[") && !json.contains("{")) {
                            json = Base64.decodeStr(group.getDishesGroupContent());
                        }
                        groupVO.getGroupIds().addAll(JSONArray.parseArray(json, Integer.class));
                    }
                    vo.getManagerDishesGroups().add(groupVO);
                }
            }
        }
        return vo;
    }

    public boolean checkManagerPwd(String pwd) {
        StoreVO store = getStore().getData();
        if (store == null) {
            return false;
        }
        if(CommonUtils.isBlank(store.getOwnerPassword())){
            return true;
        }
        return CommonUtils.eq(pwd, store.getOwnerPassword());
    }
}
