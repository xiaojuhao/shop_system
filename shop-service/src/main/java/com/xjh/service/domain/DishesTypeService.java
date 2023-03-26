package com.xjh.service.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesTypeDAO;

@Singleton
public class DishesTypeService {
    @Inject
    DishesTypeDAO dishesTypeDAO;

    public int insert(DishesType newType) {
        return dishesTypeDAO.insert(newType);
    }

    public void updateByPK(DishesType update) {
         dishesTypeDAO.updateByPK(update);
    }

    public void deleteByPK(Integer typeId) {
        if (typeId == null) {
            return;
        }
        DishesType id = new DishesType();
        id.setTypeId(typeId);
        dishesTypeDAO.deleteByPK(id);
    }

    public List<DishesType> selectList(DishesType cond) {
        List<DishesType> list = dishesTypeDAO.selectList(cond);
        list.sort(Comparator.comparing(a -> OrElse.orGet(a.getSortby(), 0)));
        return list;
    }

    public List<DishesType> loadAllTypesValid() {
        return dishesTypeDAO.selectAllValid();
    }

    public Map<Integer, DishesType> dishesTypeMap() {
        return CommonUtils.listToMap(loadAllTypesValid(), DishesType::getTypeId);
    }

    public static String toDishesTypeName(Map<Integer, DishesType> typeMap, Integer typeId){
        DishesType type = typeMap.get(typeId);
        if(type == null){
            return "未知";
        }
        return type.getTypeName();
    }
}
