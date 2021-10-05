package com.xjh.common.enumeration;

public enum EnumPayMethod {
    UNKNOWN(-1, "其他", null),
    CASH(1, "现金", null),
    BANKCARD(2, "银行卡", "交易编号"),
    COUPON(3, "优惠券", "交易编号"),
    STORECARD(4, "存储卡", "交易编号"),
    WECHAT(5, "微信", "交易编号"),
    ALIPAY(6, "支付宝", "交易编号"),
    MEITUAN(7, "美团收单", "交易编号"),
    KOUBEI(8, "口碑收单", "交易编号"),
    POS(23, "银联POS机", "交易编号"),
    ;
    public int code;
    public String name;
    public String cardNoAlias;

    EnumPayMethod(int code, String name, String cardNoAlias) {
        this.code = code;
        this.name = name;
        this.cardNoAlias = cardNoAlias;
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
