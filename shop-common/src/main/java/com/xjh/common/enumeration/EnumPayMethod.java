package com.xjh.common.enumeration;

public enum EnumPayMethod {
    UNKNOWN(-1, "其他", false),
    CASH(1, "现金", false),
    BANKCARD(2, "银行卡", false),
    VOUCHER(3, "代金券", true),
    STORECARD(4, "储值卡", false),
    WECHAT(5, "微信支付", false),
    ALIPAY(6, "支付宝支付", false),
    MEITUAN_COUPON(7, "美团代金券", true),
    KOUBEI(8, "口碑商家", false),
    WECHAT_OFFICIAL(9, "公众号", false),
    WECHAT_COUPON(10, "公众号的代金券", true),
    STORE_REDUCTION(11, "店铺减免", false),
    MEITUAN_PACKAGE(12, "美团套餐买单", true),
    OHTER(13, "其它支付", false),
    WANDA_COUPON(14, "万达券", true),
    WANDA_PACKAGE(15, "万达套餐券", true),
    SELFHELP_PRICE(16, "自助差价补齐", true),
    TINY_LIFE_COUPON(17, "微生活代金券", true),
    TINY_LIFE_INTEGRAL_DEDUCTION(18, "微生活积分抵扣", true),
    TINY_LIFE_STORECARD(19, "微生活储值卡", true),
    WECHAT_UNIONPAY_PAYMEN(20, "微信银联支付", false),
    外卖(21, "外卖", true),
    UNIONPAY_POS(22, "银联POS机", false),
    TRAFFIC_ACTIVITIES(23, "交行活动", false),
    MERCHANTS_ACTIVITIES(24, "招行活动", false),
    MARKET_ACTIVITIES(25, "商场活动", false),
    MEITUAN_SHANHUI(26, "美团闪惠", false),
    ;
    public int code;
    public String name;
    public boolean showActual;

    EnumPayMethod(int code, String name, boolean showActual) {
        this.code = code;
        this.name = name;
        this.showActual = showActual;
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
