package com.xjh.common.model;

import lombok.Data;

@Data
public class DishesChoiceItemBO {
    Integer orderId;
    Integer deskId;
    String deskName;
    String img;
    Integer dishesId;
    Integer dishesPackageId;
    String dishesName;
    Double dishesPrice;
    Integer dishesPriceId;
    Integer ifPackage;
    Integer num;
}
