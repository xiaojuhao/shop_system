package com.xjh.startup.view.model;

import lombok.Data;

@Data
public class DishesQueryCond {
    int pageNo = 1;
    int pageSize = 18;
    String dishesName;
    Integer dishesTypeId;
    Integer ifPackage;
}
