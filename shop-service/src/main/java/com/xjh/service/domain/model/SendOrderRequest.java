package com.xjh.service.domain.model;

import lombok.Data;

@Data
public class SendOrderRequest {
    Integer orderId;
    Integer deskId;
    String deskName;

    String img;
    Integer dishesId;
    Integer dishesPackageId;
    String dishesName;
    Double dishesPrice;
    Integer ifPackage;
}
