/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import com.alibaba.fastjson.JSONArray;

/**
 * @author liangh
 */
public class PrintResultImpl implements PrintResult {
    private Printer printer;
    private JSONArray jSONArray;
    private boolean isSuccess;
    private int resultCode = PrintResult.INIT;

    public PrintResultImpl(Printer printer, JSONArray jSONArray) {
        this.printer = printer;
        this.jSONArray = jSONArray;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public int getResultCode() {
        return resultCode;
    }

    @Override
    public Printer getPrinter() {
        return printer;
    }

    @Override
    public JSONArray getPrintContent() {
        return jSONArray;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
