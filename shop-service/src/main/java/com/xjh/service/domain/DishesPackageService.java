package com.xjh.service.domain;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.query.DishesPackageQuery;

@Singleton
public class DishesPackageService {
    @Inject
    DishesPackageDAO dishesPackageDAO;

    public DishesPackage getByDishesPackageId(Integer id) {
        if (id == null) {
            return null;
        }
        return dishesPackageDAO.getByDishesPackageId(id);
    }

    public List<DishesPackage> selectList(DishesPackage cond) {
        return dishesPackageDAO.selectList(cond);
    }

    public List<DishesPackage> selectAll() {
        return dishesPackageDAO.selectList(new DishesPackage());
    }

    public List<DishesPackage> pageQuery(DishesPackageQuery cond) {
        return dishesPackageDAO.pageQuery(cond);
    }

    public Result<Integer> save(DishesPackage dishesPackage) {
        if (dishesPackage.getDishesPackageId() == null) {
            return dishesPackageDAO.insert(dishesPackage);
        } else {
            return dishesPackageDAO.updateById(dishesPackage);
        }
    }
}
