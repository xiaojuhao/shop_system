package com.xjh.service.domain;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesTypeDAO;

@Singleton
public class DishesTypeService {
    @Inject
    DishesTypeDAO dishesTypeDAO;

    public List<DishesType> loadAllTypes() {
        return dishesTypeDAO.selectAll();
    }

    public Map<Integer, DishesType> dishesTypeMap() {
        return CommonUtils.listToMap(loadAllTypes(), DishesType::getTypeId);
    }
}
