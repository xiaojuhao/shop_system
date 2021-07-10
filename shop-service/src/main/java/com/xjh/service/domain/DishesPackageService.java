package com.xjh.service.domain;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.mapper.DishesPackageDAO;

@Singleton
public class DishesPackageService {
    @Inject
    DishesPackageDAO dishesPackageDAO;

    public DishesPackage getById(Integer id) {
        if (id == null) {
            return null;
        }
        return dishesPackageDAO.getById(id);
    }

    public List<DishesPackage> selectList(DishesPackage cond) {
        return dishesPackageDAO.selectList(cond);
    }
}
