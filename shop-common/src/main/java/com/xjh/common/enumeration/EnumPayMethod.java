package com.xjh.common.enumeration;

public enum EnumPayMethod {
    UNKNOWN(-1, "未知"),
    CASH(1, "现金"),
    BANKCARD(2, "银行卡"),
    COUPON(3, "优惠券"),
    STORECARD(4, "存储卡"),
    WECHAT(5, "微信"),
    ALIPAY(6, "支付宝"),
    MEITUAN(7, "美团"),
    KOUBEI(8, "口碑"),
    POS(23, "POS机");
    public int code;
    public String name;

    EnumPayMethod(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumPayMethod of(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (EnumPayMethod e : EnumPayMethod.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
