/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.printers;

import static com.xjh.startup.foundation.printers.PrinterCmdUtil.PRINTER_STATUS_NORMAL;
import static com.xjh.startup.foundation.printers.PrinterCmdUtil.PRINTER_STATUS_PRINTING;

import java.util.Arrays;

/**
 * @author liangh
 * 这个类用于检测打印的状态，根据状态来判断哪里出现了问题，方便其他程序精细化控制
 */
@SuppressWarnings("unused")
public class StatusUtil {

    //打印机会返回四个字节，这些字节中可能出错的位
    private static final byte b03 = 8;
    private static final byte b05 = 32;
    private static final byte b06 = 64;

    private static final byte b10 = 1;
    private static final byte b13 = 8;  // 切刀错误，塞纸了
    private static final byte b15 = 32;
    private static final byte b16 = 64;

    private static final byte b201 = 3;
    private static final byte b223 = 12;    // 缺纸
    private static final byte b25 = 32;

    private static final byte b36 = 64;
    //打印机会返回四个字节，这些字节中可能出错的位


    /**
     * 判断打印机返回的状态是否正常
     *
     * @param data 打印机返回的的4个字节数据
     * @return true表示打印机正常，false表示出现了某种错误
     */
    public static boolean checkStatus(byte[] data) {
        return Arrays.equals(PRINTER_STATUS_NORMAL, data) || Arrays.equals(PRINTER_STATUS_PRINTING, data);
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
        if (Arrays.equals(PRINTER_STATUS_NORMAL, data)) {
            return PrinterStatus.NORMAL.status;
        } else if (Arrays.equals(PRINTER_STATUS_PRINTING, data)) {
            return PrinterStatus.PRINTING.status;
        } else if (byteAndEqual(d2, b223)) {
            return PrinterStatus.NO_PAPER.status;
        } else if (byteAndEqual(d1, b15)) {
            return PrinterStatus.TRAPPED_PAPER.status;
        } else {
            return PrinterStatus.UNKNOWN.status;
        }
    }

    /**
     * 判断a&b之后，是否还与b相等
     *
     * @param a a
     * @param b b
     * @return rs
     */
    public static boolean byteAndEqual(byte a, byte b) {
        byte result = (byte) (a & b);
        return result == b;
    }

}
