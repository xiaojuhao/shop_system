package com.xjh.common.enumeration;

import lombok.Getter;

public enum EnumSubOrderType {
    ORDINARY(0, "普通点单"),
    H5(1, "H5订单"),
    ;
    @Getter
    int type;
    String remark;

    EnumSubOrderType(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public static EnumSubOrderType of(Integer subOrderType) {
        if (subOrderType == null) {
            return ORDINARY;
        }
        for (EnumSubOrderType e : EnumSubOrderType.values()) {
            if (e.type == subOrderType) {
                return e;
            }
        }
        return ORDINARY;
    }

}
