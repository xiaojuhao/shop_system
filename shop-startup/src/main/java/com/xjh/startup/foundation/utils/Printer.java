/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface Printer {
    int STATUS_NORMAL = 1;    //正常
    int STATUS_COLSE = 0;     //关闭

    int PRINTSTATUS_PRINTING = 0;  //正在打印中
    int PRINTSTATUS_NORMAL = 1;    //正常
    int PRINTSTATUS_NOPAPER = 2;   //缺纸
    int PRINTSTATUS_TRAPPEDPAPER = 3;  //卡纸
    int PRINTSTATUS_UNKNOW = 4;    //未知
    int PRINTSTATUS_SOCKETTIMEOUT = 5;    //socket超时

    int TYPE_80 = 1;
    int TYPE_58 = 0;


    int getId();

    String getName();

    String getIp();

    int getPort();

    String getInfoMark();

    int getStatus();

    int getPrinterType();

    long getAddTime();

    int checkPrinter() throws Exception;

    PrintResult print(JSONArray jSONArray, boolean isVoicce) throws Exception;


}
