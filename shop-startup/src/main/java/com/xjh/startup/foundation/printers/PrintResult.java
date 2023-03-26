/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.printers;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * @author liangh
 */
@Data
public class PrintResult {
    private Printer printer;
    private JSONArray jSONArray;
    private boolean isSuccess;
    private int resultCode = StatusUtil.INIT;

    public PrintResult(Printer printer, JSONArray jSONArray) {
        this.printer = printer;
        this.jSONArray = jSONArray;
    }

    public JSONArray getPrintContent() {
        return jSONArray;
    }

    public void toFailure(int code) {
        setSuccess(false);
        setResultCode(code);
    }

    public void toSuccess(int code) {
        setSuccess(false);
        setResultCode(code);
    }
}
