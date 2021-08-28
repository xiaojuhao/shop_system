package com.xjh.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.xjh.common.valueobject.DishesImgVO;

import cn.hutool.core.codec.Base64;

public class DishesImgUtils {
    public static List<DishesImgVO> resolveImgs(String base64Imgs) {
        if (CommonUtils.isBlank(base64Imgs)) {
            return new ArrayList<>();
        }
        List<DishesImgVO> imgs = new ArrayList<>();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = Base64.decodeStr(base64Imgs);
            List<DishesImgVO> arr = JSONArray.parseArray(json, DishesImgVO.class);
            if (arr != null) {
                imgs.addAll(arr);
            }
        }
        return imgs;
    }
}
