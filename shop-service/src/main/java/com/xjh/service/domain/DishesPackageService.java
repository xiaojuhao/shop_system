package com.xjh.service.domain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.query.DishesPackageQuery;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class DishesPackageService {
    @Inject
    DishesPackageDAO dishesPackageDAO;
    @Inject
    DishesPackageDishesDAO dishesPackageDishesDAO;
    @Inject
    DishesDAO dishesDAO;

    public DishesPackage getByDishesPackageId(Integer id) {
        if (id == null) {
            return null;
        }
        return dishesPackageDAO.getByDishesPackageId(id);
    }

    public List<Dishes> queryPackageDishes(Integer packageId){
        List<DishesPackageDishes> dishesPackageDishes = dishesPackageDishesDAO.getByDishesPackageId(packageId);
        List<Dishes> dishes = new ArrayList<>();
        for(DishesPackageDishes d : dishesPackageDishes){
            dishes.add(dishesDAO.getById(d.getDishesId()));
        }
        return dishes;
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
