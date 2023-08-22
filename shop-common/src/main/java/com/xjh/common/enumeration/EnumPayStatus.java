package com.xjh.common.enumeration;

public enum EnumPayStatus {
    UNPAID(0, "未支付"),
    PAID(1, "已支付");
    public int code;
    public String name;

    EnumPayStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumPayStatus of(Integer code){
        for(EnumPayStatus e : EnumPayStatus.values()){
            if(code != null && code.equals(e.code)){
                return e;
            }
        }
        return null;
    }
}
