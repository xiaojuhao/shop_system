package com.xjh.common.enumeration;

public enum EnumOrderSaleType {
    UNKNOWN(-1, "未知"),

    NORMAL(0, "普通菜品"),

    SEND(1, "赠送"),

    TASTED(2, "试吃"),

    RETURN(3, "退菜"),

    REFUND(4, "反结账");
    public int type;
    public String remark;

    EnumOrderSaleType(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public static EnumOrderSaleType of(Integer type) {
        if (type == null) {
            return UNKNOWN;
        }
        for (EnumOrderSaleType e : EnumOrderSaleType.values()) {
            if (e.type == type) {
                return e;
            }
        }
        return UNKNOWN;
    }

}
