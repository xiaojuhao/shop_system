package com.xjh.dao.dataobject;

import lombok.Data;

@Data
public class OrderPackageDishes {
    private int orderpackagedishesid;
    private int orderdishesid;
    private int dishesid;
    private String dishesoptions;
    private int dishespriceid;
    private String orderDishesOptionsString;
}
