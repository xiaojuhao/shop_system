package com.xjh.service.printers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.xjh.common.utils.CommonUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

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

    public static final byte[] PRINTER_STATUS_NORMAL = new byte[]{20, 0, 0, 15}; //打印状态正常
    public static final byte[] PRINTER_STATUS_PRINTING = new byte[]{20, 0, 64, 15}; //正在打印中

    //------------------------打印机初始化-----------------------------

    /**
     * 打印机初始化,打印模式被设置为上电时的默认模式
     *
     * @return rs
     */
    public static byte[] initPrinter() {
        return new byte[]{ESC, 64};
    }

    //------------------------换1行-----------------------------

    /**
     * 换行
     *
     * @return rs
     */
    public static byte[] nextLine() {
        return nextLine(1);
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
        return new byte[]{ESC, 100, (byte) n};
    }

    //------------------------下划线-----------------------------

    /**
     * 绘制下划线（1点宽）
     *
     * @return rs
     */
    public static byte[] underlineWithOneDotWidthOn() {
        return new byte[]{ESC, 45, 1};
    }

    /**
     * 绘制下划线（2点宽）
     *
     * @return rs
     */
    public static byte[] underlineWithTwoDotWidthOn() {
        return new byte[]{ESC, 45, 2};
    }


    /**
     * 取消绘制下划线
     *
     * @return rs
     */
    public static byte[] underlineOff() {
        return new byte[]{ESC, 45, 0};
    }

    //------------------------加粗-----------------------------

    /**
     * 选择加粗模式
     *
     * @return rs
     */
    public static byte[] boldOn() {
        return new byte[]{ESC, 69, 0xF};
    }


    /**
     * 取消加粗模式
     *
     * @return rs
     */
    public static byte[] boldOff() {
        return new byte[]{ESC, 69, 0};
    }

    //------------------------对齐-----------------------------

    /**
     * 左对齐,
     *
     * @return rs
     */
    public static byte[] alignLeft() {
        return new byte[]{ESC, 97, 0};
    }


    /**
     * 居中对齐,从当前行开始居中
     *
     * @return rs
     */
    public static byte[] alignCenter() {
        return new byte[]{ESC, 97, 1};
    }


    /**
     * 右对齐
     *
     * @return rs
     */
    public static byte[] alignRight() {
        return new byte[]{ESC, 97, 2};
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
     * @param n n的取值范围是0~20,n=1时，单位是1/4字符高，当行高小于实际字符高度时以字符高度为准
     * @return rs
     */
    public static byte[] lineDistance(int n) {
        return new byte[]{ESC, 51, (byte) (12 * n)};
    }

    //激活自动返回功能，应立即收到一条返回
    public static byte[] openAutoReturn() {
        return new byte[]{GS, 0x61, 0x0F};
    }

    //------------------------字体变大-----------------------------


    /**
     * 字体变大为标准的n倍
     *
     * @param sizeLevel sizeLevel
     * @return rs
     */
    public static byte[] fontSizeSetBig(Integer sizeLevel) {
        if (sizeLevel == null) {
            sizeLevel = 1;
        }
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
        if (CommonUtils.isBlank(text)) {
            text = " ";
        }
        return text.getBytes("gb18030");
    }

    /**
     * @param n 打印n个空格
     * @return rs
     */
    public static byte[] printSpace(int n) throws UnsupportedEncodingException {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < n; i++) {
            text.append(" ");
        }
        return text.toString().getBytes("gb18030");
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
        byte[] result = new byte[]{ESC, 0x33, 0x00}; // ESC 3 设置行间距为最小间距
        // ESC * m nL nH 点阵图
        byte[] escBmp = new byte[]{ESC, 0x2A, 0x21, 0x00, 0x00};
        //nL, nH
        escBmp[3] = (byte) (bmp.getWidth() % 256);
        escBmp[4] = (byte) (bmp.getWidth() / 256);

        int printHeight;
        if (bmp.getHeight() % 24 == 0) {
            printHeight = bmp.getHeight() / 24;
        } else {
            printHeight = bmp.getHeight() / 24 + 1;
        }
        // 每行进行打印
        int pixelColor;
        for (int i = 0; i < printHeight; i++) {
            result = byteMerger(result, escBmp);
            for (int j = 0; j < bmp.getWidth(); j++) {
                byte[] data = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
                for (int k = 0; k < 24; k++) {
                    if (((i * 24) + k) < bmp.getHeight()) {
                        pixelColor = bmp.getRGB(j, (i * 24) + k);
                        if (pixelColor != -1 && pixelColor != 0) {
                            data[k / 8] += (byte) (128 >> (k % 8));
                        }
                    }
                }
                result = byteMerger(result, data);
            }
            //换行
            result = byteMerger(result, new byte[]{CR, LF});
        }
        // 恢复默认的行距
        byte[] defaultLineSpace = new byte[]{ESC, 50};
        return byteMerger(result, defaultLineSpace);
    }

    public static void drawQRcode(
            String content,
            Graphics2D graphics2D,
            int xStart,
            int yStart,
            int width,
            int height) throws Exception {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, width, height, hints);
        draw(graphics2D, bitMatrix, xStart, yStart);
    }

    /**
     * 打印二维码,google-QRCode生成的颜色只有纯黑白两种
     */
    public static byte[] printQRCode(
            String content,
            int width,
            int height) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        draw(graphics2D, bitMatrix, 0, 0);

        return printImage(bufferedImage);
    }

    /**
     * 打印二维码（一行2个）
     */
    public static byte[] printQRCode2(
            int width, int height,
            int qrWidth, int qrHeight,
            int leftPadding1, int leftPadding2,
            String text1, String text2) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级L,M,Q,H
        hints.put(EncodeHintType.MARGIN, 0); //边距
        BitMatrix bitMatrix1 = new MultiFormatWriter().encode(text1, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
        BitMatrix bitMatrix2 = new MultiFormatWriter().encode(text2, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
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
        return new byte[]{GS, 86, 66, 0};
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
        return new byte[]{ESC, 0x73, 0x42, 0x45, -110, -102, 0x01, 0x00, 0x5F, 0x0A};// 激活免丢单功能
    }

    /**
     * 关闭免丢单功能
     */
    public static byte[] closeActiveLost() {
        return new byte[]{ESC, 0x73, 0x42, 0x45, -110, -102, 0x00, 0x00, 0x5F, 0x0A};
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
     * @param byteList byteList
     * @return rs
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

    // 将 matrix 打印到 graphics2D 上
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
