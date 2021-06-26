package com.xjh.common.enumeration;

public enum EnumOrderStatus {
    UNKNOWN(0, "未知"),

    UNPAID(1, "未支付"),

    PAID(2, "已支付"),

    PARTIAL_PAID(3, "部分支付"),

    MERGE_DESK(4, "被合并餐桌"),

    ESCAPE(5, "逃单"),

    FREE(6, "免单"),

    CHANGE_DESK(7, "已转台");
    public int status;
    public String remark;

    EnumOrderStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public static EnumOrderStatus of(Integer status) {
        if (status == null) {
            return UNKNOWN;
        }
        for (EnumOrderStatus e : EnumOrderStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
