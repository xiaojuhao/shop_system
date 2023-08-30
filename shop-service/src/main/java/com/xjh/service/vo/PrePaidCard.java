package com.xjh.service.vo;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.ReflectionUtils;
import lombok.Data;

@Data
public class PrePaidCard {
    String prePaidCardId;
    String code;
    Integer condition;
    Double balance;
    int status;
    String customerName;
    String customerPone;
    Long createTime;
    Long startTime;
    Long endTime;

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        for(ReflectionUtils.PropertyDescriptor pd : ReflectionUtils.resolvePDList(this.getClass())){
            json.put(pd.getField().getName(), pd.readValue(this));
        }
        return json;
    }
}
