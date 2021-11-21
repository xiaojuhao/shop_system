package com.xjh.dao.query;

import java.util.List;

import lombok.Data;

@Data
public class OrderPayQuery {
    Integer orderId;
    Integer paymentStatus;
    List<Integer> excludePayMethods;
}
