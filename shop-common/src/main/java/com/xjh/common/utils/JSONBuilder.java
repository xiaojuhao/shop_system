package com.xjh.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JSONBuilder {
    public static JSONObject toJSON(Object obj) {
        if (obj == null) {
            return new JSONObject();
        }
        if (obj instanceof String && CommonUtils.isBlank(obj.toString())) {
            return new JSONObject();
        }
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        if (obj instanceof String) {
            return JSON.parseObject((String) obj);
        }
        return JSON.parseObject(JSON.toJSONString(obj));
    }
}
