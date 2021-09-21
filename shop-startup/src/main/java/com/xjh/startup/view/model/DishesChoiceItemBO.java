package com.xjh.startup.view.model;

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
    Integer ifPackage;
}
