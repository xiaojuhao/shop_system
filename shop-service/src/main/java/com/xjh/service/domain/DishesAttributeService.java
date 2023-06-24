package com.xjh.service.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.JSONBuilder;
import com.xjh.common.utils.Result;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.dao.dataobject.DishesAttribute;
import com.xjh.dao.mapper.DishesAttributeDAO;
import org.apache.commons.codec.binary.Base64;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class DishesAttributeService {
    @Inject
    DishesAttributeDAO dishesAttributeDAO;

    public List<DishesAttributeVO> selectAll() {
        List<DishesAttribute> list = dishesAttributeDAO.selectList(new DishesAttribute());
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public List<DishesAttributeVO> getByAttrIds(Collection<Integer> attributeIds) {
        List<DishesAttributeVO> list = new ArrayList<>();
        if (attributeIds == null) {
            return list;
        }
        attributeIds.forEach(id -> {
            DishesAttributeVO v = getByAttrId(id);
            if (v != null) {
                list.add(v);
            }
        });
        return list;
    }

    public DishesAttributeVO getByAttrId(Integer attributeId) {
        if (attributeId == null) {
            return null;
        }
        DishesAttribute cond = new DishesAttribute();
        cond.setDishesAttributeId(attributeId);
        List<DishesAttribute> list = dishesAttributeDAO.selectList(cond);
        DishesAttribute attr = list.stream().findFirst().orElse(null);
        if (attr != null) {
            return toVO(attr);
        } else {
            return null;
        }
    }

    public Result<Integer> updateById(DishesAttributeVO attr) {
        return dishesAttributeDAO.updateById(toDD(attr));
    }

    public Result<Integer> addNew(DishesAttributeVO vo) {
        return dishesAttributeDAO.insert(toDD(vo));
    }

    public Result<Integer> deleteById(DishesAttributeVO vo) {
        return dishesAttributeDAO.deleteById(toDD(vo));
    }

    public DishesAttribute toDD(DishesAttributeVO vo) {
        boolean isSync = vo.getIsSync();
        boolean isValueRadio = vo.getIsValueRadio();
        DishesAttribute dd = new DishesAttribute();
        dd.setDishesAttributeId(vo.getDishesAttributeId());
        dd.setDishesAttributeName(vo.getDishesAttributeName());
        dd.setDishesAttributeMarkInfo(vo.getDishesAttributeMarkInfo());
        dd.setIsSync(isSync ? 1 : 0);
        dd.setIsValueRadio(isValueRadio ? 1 : 0);
        JSONObject valueObj = JSONBuilder.toJSON(dd);
        valueObj.put("isSync", isSync);
        valueObj.put("isValueRadio", isValueRadio);
        // allAttributeValues
        JSONArray allAttributeValues = new JSONArray();
        CommonUtils.forEach(vo.getAllAttributeValues(), a -> {
            JSONObject v = new JSONObject();
            v.put("attributeValue", a.getAttributeValue());
            allAttributeValues.add(v);
        });
        valueObj.put("allAttributeValues", allAttributeValues);
        // selectedAttributeValues
        JSONArray selectedAttributeValues = new JSONArray();
        CommonUtils.forEach(vo.getSelectedAttributeValues(), a -> {
            JSONObject v = new JSONObject();
            v.put("attributeValue", a.getAttributeValue());
            selectedAttributeValues.add(v);
        });
        valueObj.put("selectedAttributeValues", selectedAttributeValues);
        dd.setDishesAttributeObj(valueObj.toJSONString());
        return dd;
    }


    public DishesAttributeVO toVO(DishesAttribute attr) {
        DishesAttributeVO vo = new DishesAttributeVO();
        vo.setDishesAttributeId(attr.getDishesAttributeId());
        vo.setDishesAttributeName(attr.getDishesAttributeName());
        vo.setDishesAttributeMarkInfo(attr.getDishesAttributeMarkInfo());
        vo.setIsSync(false);
        if (attr.getIsSync() != null && attr.getIsSync() == 1) {
            vo.setIsSync(true);
        }
        vo.setIsValueRadio(false);
        if (attr.getIsValueRadio() != null && attr.getIsValueRadio() == 1) {
            vo.setIsValueRadio(true);
        }
        vo.setAllAttributeValues(new ArrayList<>());
        vo.setSelectedAttributeValues(new ArrayList<>());
        // allAttributeValues

        JSONObject attrObj = JSONBuilder.toJSON(decodeBase64(attr.getDishesAttributeObj()));
        if (attrObj.containsKey("allAttributeValues")) {
            JSONArray values = attrObj.getJSONArray("allAttributeValues");
            for (int i = 0; i < values.size(); i++) {
                JSONObject v = values.getJSONObject(i);
                DishesAttributeValueVO vv = new DishesAttributeValueVO();
                vv.setAttributeValue(v.getString("attributeValue"));
                vo.getAllAttributeValues().add(vv);
            }
        }
        // selectedAttributeValues
        if (attrObj.containsKey("selectedAttributeValues")) {
            JSONArray values = attrObj.getJSONArray("selectedAttributeValues");
            for (int i = 0; i < values.size(); i++) {
                JSONObject v = values.getJSONObject(i);
                DishesAttributeValueVO vv = new DishesAttributeValueVO();
                vv.setAttributeValue(v.getString("attributeValue"));
                vo.getSelectedAttributeValues().add(vv);
            }
        }
        return vo;
    }

    private String decodeBase64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (str.contains("{")) {
            return str;
        }
        return new String(Base64.decodeBase64(str));
    }
}
