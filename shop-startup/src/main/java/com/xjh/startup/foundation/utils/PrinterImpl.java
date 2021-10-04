/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.xjh.common.utils.CommonUtils;
import com.xjh.startup.foundation.constants.EnumAlign;
import com.xjh.startup.foundation.constants.EnumComType;

import lombok.Data;


/**
 * 先默认免丢单和自动返回功能是开启的
 * 如果输入流读阻塞，可能是免丢单和自动返回功能关闭了，所以输入流阻塞会开启免丢单。
 * 打印机不能打印过快，不然会打印一半，不执行，应该有足够的时间供打印机处理。
 * 58打印机默认情况下一行16个汉字（32字母）；80打印机一行24个汉字（48个字母）
 *
 * @author liangh
 */
@Data
@SuppressWarnings("unused")
public class PrinterImpl implements Printer {
    private int id;
    private String name;
    private String ip;
    private int port;
    private String infoMark;
    private int printerType;
    private int status;
    private long addTime;
    private int charCount = 32;
    private int timeout = 30 * 1000; //输入流读取超时时间
    private int connectTimeout = 5 * 1000; //socket连接超时时间


    /**
     * @param id          id
     * @param name        name
     * @param ip          ip
     * @param port        port
     * @param infoMark    mark
     * @param printerType 打印机类型，58mm为0,80mm为1
     * @param addTime     ,添加打印机的时间
     */
    public PrinterImpl(int id, String name, String ip, int port, String infoMark, int printerType, int status, long addTime) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.infoMark = infoMark;
        this.printerType = printerType;
        this.status = status;
        this.addTime = addTime;

        if (printerType == 1) {
            charCount = 48;
        }
    }


    public PrinterImpl(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("printerId");
        this.name = resultSet.getString("printerName");
        this.ip = resultSet.getString("printerIp");
        this.port = resultSet.getInt("printerPort");
        this.infoMark = resultSet.getString("printerInfo");
        this.printerType = resultSet.getInt("printerType");
        this.status = resultSet.getInt("printerStatus");
        this.addTime = resultSet.getLong("addTime");

        if (printerType == 1) {
            charCount = 48;
        }
    }

    @Override
    public int checkPrinter() {
        int printerStatus;
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try (Socket s = new Socket()) {
            s.connect(socketAddress, connectTimeout);
            s.setSoTimeout(timeout);
            try (OutputStream outputStream = s.getOutputStream();
                 InputStream inputStream = s.getInputStream()) {
                openAutoReturn(outputStream);
                byte[] inputData = new byte[4];
                int readSize = inputStream.read(inputData);
                printerStatus = StatusUtil.checkDetailedStatus(inputData);
            }
        } catch (IOException ex) {
            printerStatus = Printer.PRINTSTATUS_SOCKETTIMEOUT;
        }
        return printerStatus;
    }

    @Override
    public PrintResult print(JSONArray contentItems, boolean isVoicce) throws Exception {
        PrintResultImpl printResultImpl = new PrintResultImpl(this, contentItems);
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        byte[] dataRead = new byte[0];
        try (Socket s = new Socket()) {
            s.connect(socketAddress, connectTimeout);
            s.setSoTimeout(timeout);
            try (OutputStream outputStream = s.getOutputStream();
                 InputStream inputStream = s.getInputStream()) {
                initPrinter(outputStream);
                openAutoReturn(outputStream);
                print1_5Distance(outputStream);

                // 如果本模块是文本，则置为true，供下一个模块判断要不要加一个前置换行。
                // 如果true，要加，否则后一个模块会接在文本后面；
                // 如果不是，不加，否则会多一个空行。
                boolean isText = true;
                for (int i = 0; i < contentItems.size(); i++) {
                    JSONObject contentItem = contentItems.getJSONObject(i);
                    EnumComType comType = EnumComType.of(contentItem.getInteger("ComType"));
                    if (comType == EnumComType.TEXT) {
                        printText(outputStream, contentItem);
                        isText = true;
                    } else if (comType == EnumComType.TABLE) {
                        printTable(outputStream, contentItem, isText);
                        isText = false;
                    } else if (comType == EnumComType.QRCODE) {
                        printQRCode(outputStream, contentItem, isText);
                        isText = false;
                    } else if (comType == EnumComType.QRCODE2) {
                        printQRCode2(outputStream, contentItem, isText);
                        isText = false;
                    } else if (comType == EnumComType.LINE) {
                        printDotLine(outputStream, contentItem);
                        isText = false;
                    } else {
                        String detailedInfo = "json第 " + i + " 个元素的type属性错误,错误类型为：type = " + comType;
                        throw new Exception(detailedInfo + ", " + comType);
                    }
                }
                feedPaperCut(outputStream);
                if (isVoicce) {
                    voice(outputStream);
                }

                byte[] inputData = new byte[4];
                boolean successFlag = false;
                boolean printingFlag = false;
                while (!successFlag) {
                    int len;
                    try {
                        len = inputStream.read(inputData);
                    } catch (java.net.SocketTimeoutException e) {
                        openPreventLost(outputStream);
                        printResultImpl.toFailure(StatusUtil.DATA_READ_TIMEOUT);
                        break;
                    }
                    dataRead = PrinterCmdUtil.byteMerger(dataRead, inputData);

                    if (StatusUtil.checkStatus(inputData)) {
                        if (StatusUtil.checkDetailedStatus(inputData) == StatusUtil.PRINTING) {
                            printingFlag = true;
                        }
                        if (StatusUtil.checkDetailedStatus(inputData) == StatusUtil.NORMAL && printingFlag) {
                            successFlag = true;
                            printResultImpl.toSuccess(StatusUtil.NORMAL);
                        }
                    } else {
                        int resultCode = StatusUtil.checkDetailedStatus(inputData);
                        printResultImpl.toFailure(resultCode);
                        status = resultCode;
                        //查看输入流是否还有字节，有的话，一起全部都读出来
                        while (len != -1) {
                            len = inputStream.read(inputData);
                            if (len != -1) {
                                dataRead = PrinterCmdUtil.byteMerger(dataRead, inputData);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            printResultImpl.setSuccess(false);
            if (printResultImpl.getResultCode() == StatusUtil.INIT) {
                printResultImpl.setResultCode(StatusUtil.SOCKET_TIMEOUT);
            }
            return printResultImpl;
        } catch (Exception e) {
            e.printStackTrace();
            printResultImpl.toFailure(StatusUtil.UNKNOWN);
        }
        ////////////////////事件处理////////////////////
        return printResultImpl;
    }

    /**
     * 激活自动返回功能，应立即收到一条返回
     */
    private void openAutoReturn(OutputStream outputStream) throws IOException {
        outputStream.write(PrinterCmdUtil.openAutoReturn());
        outputStream.flush();
    }


    /**
     * 激活免丢单功能和自动返回功能
     */
    private void openPreventLost(OutputStream outputStream) throws IOException, InterruptedException {
        outputStream.write(PrinterCmdUtil.openPreventLost());
        outputStream.flush();
        //打印机会有一段反应时间
        Thread.sleep(1000);
    }

    private void closePreventLost(OutputStream outputStream) throws IOException {
        // 关闭免丢单功能
        outputStream.write(PrinterCmdUtil.closeActiveLost());
        outputStream.flush();
    }

    private void feedPaperCut(OutputStream outputStream) throws IOException {
        // 切纸行首有效，所以前面添加一个换行符
        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(),
                PrinterCmdUtil.feedPaperCut()};
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }

    private void voice(OutputStream outputStream) throws IOException {
        byte[] voice = PrinterCmdUtil.voice();
        outputStream.write(voice);
        outputStream.flush();
    }

    private void initPrinter(OutputStream outputStream) throws IOException {
        byte[] initPrinter = PrinterCmdUtil.initPrinter();
        outputStream.write(initPrinter);
        outputStream.flush();
    }


    private void printText(OutputStream outputStream, JSONObject jsonObject) throws IOException {
        String sampleContent = jsonObject.getString("SampleContent");
        int size = jsonObject.getInteger("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");
        int frontLen = jsonObject.getInteger("FrontLen");
        int behindLen = jsonObject.getInteger("BehindLen");

        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(frontEnterNum),
                PrinterCmdUtil.printSpace(frontLen),
                PrinterCmdUtil.fontSizeSetBig(size), // 设置字体大小
                PrinterCmdUtil.printText(sampleContent), // 打印内容
                PrinterCmdUtil.fontSizeSetBig(1), // 恢复字体大小
                PrinterCmdUtil.printSpace(behindLen),
                PrinterCmdUtil.nextLine(behindEnterNum)
        };
        outputStream.write(PrinterCmdUtil.byteMerger(byteList));
        outputStream.flush();
    }

    private void printQRCode(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws IOException, WriterException {
        int maxSize = (printerType == 1 ? 48 : 32);
        String content = jsonObject.getString("Content");
        double width = jsonObject.getDouble("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");
        int size = Math.min((int) (charCount * width / 100), maxSize);

        if (isText) {
            outputStream.write(PrinterCmdUtil.nextLine());
        }
        // 二维码内容
        byte[] byteQRCode = PrinterCmdUtil.printQRCode(content, size * 12, size * 12);
        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(frontEnterNum),
                PrinterCmdUtil.alignCenter(), // 设置居中对齐
                byteQRCode,
                PrinterCmdUtil.nextLine(behindEnterNum),
                PrinterCmdUtil.alignLeft() // 恢复左对齐
        };
        outputStream.write(PrinterCmdUtil.byteMerger(byteList));
        outputStream.flush();
        print1_5Distance(outputStream);
    }

    private void printQRCode2(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws IOException, WriterException {
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");
        int width = jsonObject.getInteger("Width");
        int height = jsonObject.getInteger("Height");
        int qrWidth = jsonObject.getInteger("QrWidth");
        int qrHeight = jsonObject.getInteger("QrWidth");
        int leftPadding1 = jsonObject.getInteger("LeftPadding1");
        int leftPadding2 = jsonObject.getInteger("LeftPadding2");
        String text1 = jsonObject.getString("Text1");
        String text2 = jsonObject.getString("Text2");

        if (isText) {
            outputStream.write(PrinterCmdUtil.nextLine());
        }

        byte[] byteQRCode = PrinterCmdUtil.printQRCode2(
                width, height,
                qrWidth, qrHeight,
                leftPadding1, leftPadding2,
                text1, text2);

        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(frontEnterNum),
                byteQRCode,
                PrinterCmdUtil.nextLine(behindEnterNum)
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
        print1_5Distance(outputStream);
    }

    private void printRowDistance(OutputStream outputStream, JSONObject jsonObject) throws IOException {
        int rowDistance = jsonObject.getInteger("rowDistance");
        outputStream.write(PrinterCmdUtil.lineDistance(rowDistance));
        outputStream.flush();
    }

    private void print1_5Distance(OutputStream outputStream) throws IOException {
        byte[] byteRowDistance = PrinterCmdUtil.lineDistance(6);
        outputStream.write(byteRowDistance);
        outputStream.flush();
    }

    private void printDotLine(OutputStream outputStream, JSONObject jsonObject) throws IOException {
        int size = jsonObject.getInteger("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");

        byte[] byteSize = PrinterCmdUtil.fontSizeSetBig(size);
        int realCount = charCount / size;
        byte[] hyphenLine = PrinterCmdUtil.printText(CommonUtils.repeatStr("- ", realCount / 2));
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);

        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(),
                PrinterCmdUtil.nextLine(frontEnterNum),
                byteSize,
                hyphenLine,
                byteSizeDefault,
                PrinterCmdUtil.nextLine(),
                PrinterCmdUtil.nextLine(behindEnterNum)
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }


    private void printTable(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws Exception {
        int size = jsonObject.getInteger("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");

        JSONArray columnNames = jsonObject.getJSONArray("columnNames");
        JSONArray columnWidths = jsonObject.getJSONArray("columnWidths");
        JSONArray columnAligns = jsonObject.getJSONArray("columnAligns");
        JSONArray rows = jsonObject.getJSONArray("rows");

        if (isText) {
            outputStream.write(PrinterCmdUtil.nextLine());
        }
        // 保证第一行不会接在上次打印结果的后面
        outputStream.write(PrinterCmdUtil.nextLine(frontEnterNum));
        // 设置字符大小
        outputStream.write(PrinterCmdUtil.fontSizeSetBig(size));
        // 设置行间距永远是字体大小的1.5倍
        outputStream.write(PrinterCmdUtil.lineDistance(4 * size + 2));
        printTableRow(outputStream, columnNames, columnWidths, columnAligns, size);
        for (int i = 0; i < rows.size(); i++) {
            printTableRow(outputStream, rows.getJSONArray(i), columnWidths, columnAligns, size);
        }
        print1_5Distance(outputStream);
        // 重置字符大小
        outputStream.write(PrinterCmdUtil.fontSizeSetBig(1));
        // 最后的回车
        outputStream.write(PrinterCmdUtil.nextLine(behindEnterNum));
        outputStream.flush();
    }


    private void printTableRow(
            OutputStream outputStream,
            JSONArray rowData,
            JSONArray columnWidths,
            JSONArray columnAligns,
            int size) throws Exception {
        // 整个数据都是空，不打印
        if (CommonUtils.collValueIsEmpty(rowData)) {
            return;
        }
        int maxRow = 1;
        double widthPlus = 0;
        JSONArray jsonCells = new JSONArray();
        for (int i = 0; i < columnWidths.size(); i++) {
            int colWidth = columnWidths.getInteger(i);
            int colAlign = columnAligns.getInteger(i);
            int paddingLeft = (int) (widthPlus * charCount / 100 / size);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("paddingLeft", paddingLeft);
            jsonObject.put("align", colAlign);
            int widthChar = (charCount * colWidth / 100 / size);
            if (i == columnWidths.size() - 1) {
                widthChar = (charCount - (int) (widthPlus * charCount / 100)) / size;
            }
            widthPlus += colWidth;
            jsonObject.put("widthChar", widthChar);

            List<String> group = StringUtil.getGroup(rowData.getString(i), widthChar, i + 1);
            if (group.size() > maxRow) {
                maxRow = group.size();
            }
            JSONArray jsonCell = new JSONArray();
            jsonCell.add(jsonObject);
            jsonCell.add(group);
            jsonCells.add(jsonCell);
        }

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < jsonCells.size(); j++) {
                JSONArray jsonCell = jsonCells.getJSONArray(j);
                JSONObject jsonObject = jsonCell.getJSONObject(0);
                List<String> group = (List<String>) jsonCell.get(1);

                if (group.size() > i) {
                    int paddingLeft = jsonObject.getInteger("paddingLeft");
                    int widthChar = jsonObject.getInteger("widthChar");   //12223333
                    EnumAlign align = EnumAlign.of(jsonObject.getInteger("align"));

                    //byte[] bytePaddingLeft = PrinterCmdUtil.hTPositionMove(paddingLeft);
                    byte[] byteContent = PrinterCmdUtil.printText(StringUtil.alignString(group.get(i), widthChar, align));
                    byte[][] byteList = new byte[][]{
                            //bytePaddingLeft,    //填充会随着字号的倍增而倍增，所以左填充放在字体放大之前
                            byteContent,
                    };
                    byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
                    outputStream.write(byteMerger);
                }
            }
            // 换行
            outputStream.write(PrinterCmdUtil.nextLine());
        }
    }
}
