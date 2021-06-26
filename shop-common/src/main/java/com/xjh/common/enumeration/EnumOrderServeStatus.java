package com.xjh.common.enumeration;

public enum EnumOrderServeStatus {
    START(0, "客人开台之后的状态，持续到用餐结束"),

    END(1, "已经完结订单，关台之后的状态");
    public int status;
    String remark;

    EnumOrderServeStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }
}
