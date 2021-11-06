package com.xjh.common.utils;

import java.util.List;

import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;

public class DishesAttributeHelper {
    public static String generateSelectedAttrDigest(List<DishesAttributeVO> attrs) {
        if (CommonUtils.isEmpty(attrs)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (DishesAttributeVO attr : attrs) {
            List<String> selectedAttrs = CommonUtils.collect(attr.getSelectedAttributeValues(), DishesAttributeValueVO::getAttributeValue);
            if (selectedAttrs.size() > 0) {
                sb.append("(").append(String.join(",", selectedAttrs)).append(")");
            }
        }
        return sb.toString();
    }
}
