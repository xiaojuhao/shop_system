package com.xjh.dao.query;

import lombok.Data;

@Data
public class PageQueryOrderReq {
    Integer pageNo;
    Integer pageSize;
    Integer orderId;
    Long startTime;
    Long endTime;
}
