package com.xjh.common.enumeration;

public enum EnumPayAction {
    DO_PAY(1),

    CANCEL_PAY(0);


    public int code;

    EnumPayAction(int code){
        this.code = code;
    }
}
