package com.xjh.service.domain.model;

import lombok.Data;

@Data
public class CreateOrderParam {
    // Integer orderId;
    Integer deskId;
    Integer customerNum;
    String recommender;

}
