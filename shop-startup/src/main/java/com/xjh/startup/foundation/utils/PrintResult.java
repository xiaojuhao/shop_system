package com.xjh.startup.foundation.utils;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface PrintResult {
    //NORMAL ,NOPAPER,TRAPPEDPAPER,UNKNOW
    public int NORMAL = 1;   //正常
    public int NOPAPER = 2;     //没纸
    public int TRAPPEDPAPER = 3;  //卡纸
    public int UNKNOW = 4;  //未知
    public int SOCKETTIMEOUT = 5; //打印机离线超时
    public int DATAREADTTIMEOUT = 6; //读取打印机返回字节超时
    public int INIT = 7; //打印机结果刚new出来的初始状态

    public boolean isSuccess();

    public int getResultCode();

    public Printer getPrinter();//返回由哪个打印机打印得该结果

    public JSONArray getPrintContent();
}
