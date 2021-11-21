package com.xjh.common.enumeration;

public enum EnumPayMethod {
    UNKNOWN(-1, "其他"),
    CASH(1, "现金"),
    BANKCARD(2, "银行卡"),
    COUPON(3, "优惠券"),
    STORECARD(4, "存储卡"),
    WECHAT(5, "微信"),
    ALIPAY(6, "支付宝"),
    MEITUAN_COUPON(7, "美团代金券"),
    KOUBEI(8, "口碑收单"),
    MEITUAN_PACKAGE(12, "美团套餐买单"),
    WANDA_PACKAGE(15, "假日套餐补差价"),
    POS(23, "银联POS机"),
    ;
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
