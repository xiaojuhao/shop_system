package com.xjh.common.enumeration;

public enum EnumDishesStatus {
    ON(1, "上架"),

    OFF(0, "下架"),

    ;
    public int status;
    public String remark;

    EnumDishesStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public static EnumDishesStatus of(Integer status) {
        if (status == null) {
            return ON;
        }
        for (EnumDishesStatus e : EnumDishesStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return ON;
    }

}
