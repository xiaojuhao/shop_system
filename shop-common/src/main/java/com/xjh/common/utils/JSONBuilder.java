package com.xjh.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JSONBuilder {
    public static JSONObject toJSON(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj));
    }
}
