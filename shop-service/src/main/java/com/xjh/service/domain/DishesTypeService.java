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

    public int insert(DishesType newType) {
        return dishesTypeDAO.insert(newType);
    }

    public int updateByPK(DishesType update) {
        return dishesTypeDAO.updateByPK(update);
    }

    public int deleteByPK(Integer typeId) {
        if (typeId == null) {
            return 0;
        }
        DishesType id = new DishesType();
        id.setTypeId(typeId);
        return dishesTypeDAO.deleteByPK(id);
    }

    public List<DishesType> selectList(DishesType cond) {
        return dishesTypeDAO.selectList(cond);
    }

    public List<DishesType> loadAllTypesValid() {
        return dishesTypeDAO.selectAllValid();
    }

    public Map<Integer, DishesType> dishesTypeMap() {
        return CommonUtils.listToMap(loadAllTypesValid(), DishesType::getTypeId);
    }
}
