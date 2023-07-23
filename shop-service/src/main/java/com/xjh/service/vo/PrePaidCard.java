package com.xjh.service.vo;

import com.alibaba.fastjson.JSONObject;
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

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
