/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.util.List;

import javax.print.event.PrintEvent;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface Printer {
    public static final int STATUS_NORMAL = 1;    //正常
    public static final int STATUS_COLSE = 0;     //关闭

    public static final int PRINTSTATUS_PRINTING = 0;  //正在打印中
    public static final int PRINTSTATUS_NORMAL = 1;    //正常
    public static final int PRINTSTATUS_NOPAPER = 2;   //缺纸
    public static final int PRINTSTATUS_TRAPPEDPAPER = 3;  //卡纸
    public static final int PRINTSTATUS_UNKNOW = 4;    //未知
    public static final int PRINTSTATUS_SOCKETTIMEOUT = 5;    //socket超时

    public static final int TYPE_80 = 1;
    public static final int TYPE_58 = 0;


    public int getId();

    public String getName();

    public String getIp();

    public int getPort();

    public String getInfoMark();

    public int getStatus();

    public int getPrinterType();

    public long getAddTime();

    public int checkPrinter() throws Exception;

    public PrintResult print(JSONArray jSONArray, boolean isVoicce) throws Exception;

    public PrintResult print(JSONArray jSONArray, boolean isVoicce, PrintEvent printEvent) throws Exception;

    public PrintResult print(JSONArray jSONArray, boolean isVoicce, List<PrintEvent> printEvents) throws Exception;


}
