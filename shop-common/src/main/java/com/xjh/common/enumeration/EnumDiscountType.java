package com.xjh.common.enumeration;

public enum EnumDiscountType {
    NONE(0, "无"),

    STORE(1, "门店打折"),

    COUPON(2, "优惠券"),

    CARD(3, "折扣卡"),

    MANAGER(4, "店长打折"),

    MEMBER(5, "会员打折"),

    WECHAT(6, "微信打折");

    int code;

    EnumDiscountType(int code, String remark) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static EnumDiscountType of(Integer code) {
        if (code == null) {
            return NONE;
        }
        for (EnumDiscountType e : EnumDiscountType.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return NONE;
    }

}
