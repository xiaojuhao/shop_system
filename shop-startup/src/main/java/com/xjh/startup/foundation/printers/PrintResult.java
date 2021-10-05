package com.xjh.startup.foundation.printers;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface PrintResult {

    public boolean isSuccess();

    public int getResultCode();

    public Printer getPrinter();//返回由哪个打印机打印得该结果

    public JSONArray getPrintContent();
}
