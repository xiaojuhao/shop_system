package com.xjh.dao.query;

import lombok.Data;

import java.util.List;

@Data
public class OrderPayQuery {
    Integer orderId;
    Integer paymentStatus;
    List<Integer> excludePayMethods;
}
