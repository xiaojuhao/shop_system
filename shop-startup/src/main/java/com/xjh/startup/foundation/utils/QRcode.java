/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import static com.xjh.startup.foundation.utils.PrinterCmdUtil.draw;

import java.awt.*;
import java.util.HashMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author 36181
 */
public class QRcode {
    public static void drawQRcode(String content, Graphics2D graphics2D, int x, int y, int width) throws WriterException {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, width, hints);
        draw(graphics2D, bitMatrix, x, y);
    }
}
