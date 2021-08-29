package com.xjh.common.enumeration;

public enum EnumDeskStatus {
    FREE(1, "空闲中"),
    IN_USE(2, "使用中"),
    PRESERVED(3, "已预约"),
    PAID(4, "已支付");

    int status;
    String remark;

    public String remark() {
        return remark;
    }

    public int status() {
        return status;
    }

    EnumDeskStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public static EnumDeskStatus of(Integer status) {
        if (status == null) {
            return FREE;
        }
        for (EnumDeskStatus e : EnumDeskStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return FREE;
    }
}
