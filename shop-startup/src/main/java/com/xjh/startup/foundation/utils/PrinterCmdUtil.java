package com.xjh.startup.foundation.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@SuppressWarnings("unused")
public class PrinterCmdUtil {

    private static final byte ESC = 27;
    private static final byte FS = 28;
    private static final byte GS = 29;
    private static final byte DLE = 16;
    private static final byte EOT = 4;
    private static final byte ENQ = 5;
    private static final byte SP = 32;
    private static final byte HT = 9;
    private static final byte LF = 10;
    private static final byte CR = 13;
    private static final byte FF = 12;
    private static final byte CAN = 24;


    //------------------------打印机初始化-----------------------------


    /**
     * 打印机初始化,打印模式被设置为上电时的默认模式
     *
     * @return rs
     */
    public static byte[] initPrinter() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 64;
        return result;
    }

    //------------------------换1行-----------------------------


    /**
     * 换行
     *
     * @return rs
     */
    public static byte[] nextLine() {
        byte[] result = new byte[1];
        result[0] = LF;
        return result;
    }

    /**
     * 换行
     *
     * @param lineNum 要换几行
     * @return content
     */
    public static byte[] nextLine(int lineNum) {
        byte[] result = new byte[lineNum];
        for (int i = 0; i < lineNum; i++) {
            result[i] = LF;
        }

        return result;
    }


    /**
     * 打印并向前走纸n行
     *
     * @param n，n的取值范围是0~255
     * @return rs
     */
    public static byte[] printAndNextLines(int n) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 100;
        result[2] = (byte) n;
        return result;
    }

    //------------------------下划线-----------------------------


    /**
     * 绘制下划线（1点宽）
     *
     * @return rs
     */
    public static byte[] underlineWithOneDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        return result;
    }


    /**
     * 绘制下划线（2点宽）
     *
     * @return rs
     */
    public static byte[] underlineWithTwoDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        return result;
    }


    /**
     * 取消绘制下划线
     *
     * @return rs
     */
    public static byte[] underlineOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 0;
        return result;
    }


    //------------------------加粗-----------------------------


    /**
     * 选择加粗模式
     *
     * @return rs
     */
    public static byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }


    /**
     * 取消加粗模式
     *
     * @return rs
     */
    public static byte[] boldOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

    //------------------------得到指定的对齐-----------------------------


    /**
     * 得到指定的对齐
     *
     * @param align 指定的对齐方式
     * @return rs
     */
    public static byte[] getAlign(String align) {
        if ("center".equals(align)) {
            return alignCenter();
        } else if ("right".equals(align)) {
            return alignRight();
        } else {
            return alignLeft();
        }
    }


    //------------------------对齐-----------------------------


    /**
     * 左对齐,
     *
     * @return rs
     */
    public static byte[] alignLeft() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }


    /**
     * 居中对齐,从当前行开始居中
     *
     * @return rs
     */
    public static byte[] alignCenter() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }


    /**
     * 右对齐
     *
     * @return rs
     */
    public static byte[] alignRight() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }


    /**
     * 水平定位点向水平方向移动，距离行首移动n列，超过一行的总列数时，换行至下一行行首,不在移动
     *
     * @param n， n的取值范围是0~255
     * @return rs
     */
    public static byte[] hTPositionMove(int n) {
        byte[] result = new byte[5];
        result[0] = ESC;
        result[1] = 68;
        result[2] = (byte) n;
        result[3] = 0;
        result[4] = HT;
        return result;
    }

    /**
     * 设置默认的行间距
     *
     * @return rs
     */
    public static byte[] defaultLineDistance() {
        return new byte[]{ESC, 50};
    }

    /**
     * 设置行间距,先将移动单位设置成默认移动单位，设置行高
     *
     * @param n， n的取值范围是0~20,n=1时，单位是1/4字符高，当行高小于实际字符高度时以字符高度为准
     * @return rs
     */
    public static byte[] lineDistance(int n) {
        int realLineSpace = 12 * n;
        return new byte[]{ESC, 51, (byte) realLineSpace};
    }

    /**
     * 这种58，80对待移动单位不一样
     */
    //    public static byte[] lineDistance( int n )
    //    {
    //        int realLineSpace = 6 * n;
    //        byte[] result = new byte[7];
    //        result[0] = 29;
    //        result[1] = 80;
    //        result[2] = 0;
    //        result[3] = 0;
    //        result[4] = ESC;
    //        result[5] = 51;
    //        result[6] = (byte) realLineSpace;
    //        return result;
    //    }


    //------------------------字体变大-----------------------------


    /**
     * 字体变大为标准的n倍
     *
     * @param sizeLevel sizeLevel
     * @return rs
     */
    public static byte[] fontSizeSetBig(int sizeLevel) {
        byte realSize = 0;
        switch (sizeLevel) {
            case 1:
                realSize = 0;
                break;
            case 2:
                realSize = 17;
                break;  //17
            case 3:
                realSize = 34;
                break;
            case 4:
                realSize = 51;
                break;
            case 5:
                realSize = 68;
                break;
            case 6:
                realSize = 85;
                break;
            case 7:
                realSize = 102;
                break;
            case 8:
                realSize = 119;
                break;
        }
        return new byte[]{GS, 33, realSize};
    }

    /**
     * 打印文本
     *
     * @param text 打印的文字内容
     * @return rs
     * @throws UnsupportedEncodingException ex
     */
    public static byte[] printText(String text) throws UnsupportedEncodingException {
        byte[] byteText = null;
        byteText = text.getBytes("gb18030");
        return byteText;
    }

    /**
     * @param n 打印n个空格
     * @return rs
     */
    public static byte[] printSpace(int n) throws UnsupportedEncodingException {
        byte[] byteText = null;
        StringBuffer text = new StringBuffer();
        for (int i = 0; i < n; i++) {
            text.append(" ");
        }
        byteText = text.toString().getBytes("gb18030");
        return byteText;
    }

    /**
     * 打印位图
     *
     * @param path 位图的路径
     * @return rs
     */
    public static byte[] printImageWithPath(String path) throws IOException {
        BufferedImage bmp = ImageIO.read(new File(path));
        return printImage(bmp);

    }


    /**
     * 打印位图
     *
     * @param bmp img
     * @return rs
     */
    public static byte[] printImage(BufferedImage bmp) {

        byte[] result = new byte[]
                {
                        0x1B, 0x33, 0x00
                }; //ESC 3	设置行间距为最小间距

        byte[] data = new byte[3];
        data[0] = (byte) 0x00;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;    //重置参数

        int pixelColor;

        // ESC * m nL nH 点阵图
        byte[] escBmp = new byte[]
                {
                        0x1B, 0x2A, 0x00, 0x00, 0x00
                };

        escBmp[2] = (byte) 0x21;

        //nL, nH
        escBmp[3] = (byte) (bmp.getWidth() % 256);
        escBmp[4] = (byte) (bmp.getWidth() / 256);

        //        byte[] nextLine = nextLine();
        //        result = byteMerger(result,nextLine);
        //        byte[] hTPositionMove = hTPositionMove(leftPadding);

        int printHight;
        if (bmp.getHeight() % 24 == 0) {
            printHight = bmp.getHeight() / 24;
        } else {
            printHight = bmp.getHeight() / 24 + 1;
        }
        // 每行进行打印
        for (int i = 0; i < printHight; i++) {
            //            result = byteMerger(result,hTPositionMove);
            result = byteMerger(result, escBmp);

            for (int j = 0; j < bmp.getWidth(); j++) {
                for (int k = 0; k < 24; k++) {
                    if (((i * 24) + k) < bmp.getHeight()) {
                        pixelColor = bmp.getRGB(j, (i * 24) + k);
                        if (pixelColor != -1 && pixelColor != 0) {
                            data[k / 8] += (byte) (128 >> (k % 8));
                        }
                    }
                }

                result = byteMerger(result, data);
                // 重置参数
                data[0] = (byte) 0x00;
                data[1] = (byte) 0x00;
                data[2] = (byte) 0x00;
            }
            //换行
            byte[] byte_send1 = new byte[2];
            byte_send1[0] = 0x0d;
            byte_send1[1] = 0x0a;
            result = byteMerger(result, byte_send1);
        }
        //恢复默认的行距
        byte[] defaultLineSpace = new byte[]{27, 50};
        result = byteMerger(result, defaultLineSpace);
        return result;
    }

    /**
     * 打印二维码,goole-QRCode生成的颜色只有纯黑白两种
     *
     * @param content 二维码得扫描后内容
     * @param width   二维码图片的宽度
     * @return rs
     */
    public static byte[] printQRCode(String content, int width) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距
        BitMatrix bitMatrix = null;

        bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, width, hints);

        BufferedImage bufferedImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        draw(graphics2D, bitMatrix, 0, 0);

        return printImage(bufferedImage);
    }

    public static byte[] printQRCode2(int width, int height, int qrWidth, int leftPadding1, int leftPadding2, String text1, String text2) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距
        BitMatrix bitMatrix1 = new MultiFormatWriter().encode(text1, BarcodeFormat.QR_CODE, qrWidth, qrWidth, hints);
        BitMatrix bitMatrix2 = new MultiFormatWriter().encode(text2, BarcodeFormat.QR_CODE, qrWidth, qrWidth, hints);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        draw(graphics2D, bitMatrix1, leftPadding1, 5);
        draw(graphics2D, bitMatrix2, leftPadding1 + qrWidth + leftPadding2, 5);

        return printImage(bufferedImage);
    }


    //------------------------切纸-----------------------------


    /**
     * 进纸并切割，这条命令只有在行首有效
     *
     * @return x
     */

    public static byte[] feedPaperCut() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 66;
        result[3] = 0;
        return result;
    }

    public static byte[] voice() {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 66;
        result[2] = 2; // 鸣叫次数
        result[3] = 8; // 持续时间(ms)
        return result;
    }

    /**
     * 激活免丢单功能和自动返回功能
     */
    public static byte[] openPreventLost() {
        return new byte[]{0x1B, 0x73, 0x42, 0x45, -110, -102, 0x01, 0x00, 0x5F, 0x0A};// 激活免丢单功能
    }

    /**
     * 合并两个字节数组
     *
     * @param byte_1 x
     * @param byte_2 x
     * @return x
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 将一个二维字节数组整合成一个一维字节数组，一组字节数组包含二维字节数组中的所有字节
     *
     * @param byteList
     * @return
     */
    public static byte[] byteMerger(byte[][] byteList) {

        int length = 0;
        for (byte[] bytes : byteList) {
            length += bytes.length;
        }
        byte[] result = new byte[length];

        int index = 0;
        for (byte[] nowByte : byteList) {
            for (byte b : nowByte) {
                result[index] = b;
                index++;
            }
        }
        return result;
    }

    private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();

    public static void draw(Graphics2D graphics2D, BitMatrix matrix, int xStart, int yStart) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int onColor = DEFAULT_CONFIG.getPixelOnColor();
        int offColor = DEFAULT_CONFIG.getPixelOffColor();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(matrix.get(x, y) ? onColor : offColor);
                graphics2D.setColor(color);
                graphics2D.fillRect(xStart + x, yStart + y, 1, 1);
            }
        }

    }
}
