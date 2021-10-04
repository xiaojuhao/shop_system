/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.util.Arrays;

/**
 * @author liangh
 * 这个类用于检测打印的状态，根据状态来判断哪里出现了问题，方便其他程序精细化控制
 */
public class StatusUtil {

    private static final byte[] BYTENORMAL = new byte[]{20, 0, 0, 15}; //打印状态正常
    private static final byte[] BYTEPRINTING = new byte[]{20, 0, 64, 15}; //正在打印中

    //打印机会返回四个字节，这些字节中可能出错的位
    private static final byte b03 = 8;
    private static final byte b05 = 32;
    private static final byte b06 = 64;

    private static final byte b10 = 1;
    private static final byte b13 = 8;  //切刀错误，塞纸了
    private static final byte b15 = 32;
    private static final byte b16 = 64;

    private static final byte b201 = 3;
    private static final byte b223 = 12;    //缺纸
    private static final byte b25 = 32;

    private static final byte b36 = 64;
    //打印机会返回四个字节，这些字节中可能出错的位

    public static final int PRINTING = 0;
    public static final int NORMAL = 1;
    public static final int NOPAPER = 2;
    public static final int TRAPPEDPAPER = 3;
    public static final int UNKNOW = 4;//正常，打印中，缺纸，卡纸，未知

    /**
     * 判断打印机返回的状态是否正常
     *
     * @param data 打印机返回的的4个字节数据
     * @return true表示打印机正常，false表示出现了某种错误
     */
    public static boolean checkStatus(byte[] data) {
        return Arrays.equals(BYTENORMAL, data) || Arrays.equals(BYTEPRINTING, data);
    }

    /**
     * 判断打印机的详细状态
     *
     * @param data data
     */
    public static int checkDetailedStatus(byte[] data) {
        byte d0 = data[0];
        byte d1 = data[1];
        byte d2 = data[2];
        byte d3 = data[3];
        if (Arrays.equals(BYTENORMAL, data)) {
            return NORMAL;
        } else if (Arrays.equals(BYTEPRINTING, data)) {
            return PRINTING;
        } else if (byteAndEqual(d2, b223)) {
            return NOPAPER;
        } else if (byteAndEqual(d1, b15)) {
            return TRAPPEDPAPER;
        } else {
            return UNKNOW;
        }
    }

    /**
     * 判断a&b之后，是否还与b相等
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean byteAndEqual(byte a, byte b) {
        byte result = (byte) (a & b);
        if (result == b) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 激活免丢单功能
     *
     * @return
     */
    private static byte[] openPreventLost() {
        byte[] activeLost = new byte[]{0x1B, 0x73, 0x42, 0x45, -110, -102, 0x01, 0x00, 0x5F, 0x0A};// 激活免丢单功能
        return activeLost;
    }

    /**
     * 关闭免丢单功能
     *
     * @return
     */
    private static byte[] closePreventLost() {
        byte[] closeActiveLost = new byte[]{0x1B, 0x73, 0x42, 0x45, -110, -102, 0x00, 0x00, 0x5F, 0x0A};// 关闭免丢单功能
        return closeActiveLost;
    }


    /**
     * 激活自动返回功能（当免丢单功能激活的时候，自动返回功能才有效）
     *
     * @return
     */
    private static byte[] openAutoReturn() {
        byte[] asb = new byte[]{0x1D, 0x61, 0x0F};//激活自动返回功能，应立即收到一条返回,
        return asb;
    }

}
