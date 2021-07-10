package com.xjh.service.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.reqmodel.PageCond;

@Singleton
public class DishesService {
    @Inject
    DishesDAO dishesDAO;

    public Dishes getById(Integer id) {
        return dishesDAO.getById(id);
    }

    public List<Dishes> getByIds(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return dishesDAO.getByIds(ids);
    }

    public List<Dishes> selectList(Dishes cond) {
        return dishesDAO.selectList(cond);
    }

    public List<Dishes> pageQuery(Dishes cond, PageCond page) {
        return dishesDAO.pageQuery(cond, page);
    }
}
