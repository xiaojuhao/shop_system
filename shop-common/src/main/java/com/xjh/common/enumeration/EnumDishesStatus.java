package com.xjh.common.enumeration;

public enum EnumDishesStatus {
    ON(1, "上架"),

    OFF(0, "下架"),

    ;
    public int status;
    EnumDishesStatus(int status, String remark){
        this.status = status;
    }


}
