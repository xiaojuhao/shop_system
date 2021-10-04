/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;


/**
 * 先默认免丢单和自动返回功能是开启的
 * 如果输入流读阻塞，可能是免丢单和自动返回功能关闭了，所以输入流阻塞会开启免丢单。
 * 打印机不能打印过快，不然会打印一半，不执行，应该有足够的时间供打印机处理。
 * 58打印机默认情况下一行16个汉字（32字母）；80打印机一行24个汉字（48个字母）
 *
 * @author liangh
 */
public class PrinterImpl implements Printer {
    public static int BENPAO_ALIGN_CENTER = 0;
    public static int BENPAO_ALIGN_LEFT = 2;
    public static int BENPAO_ALIGN_RIGHT = 4;

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
     * @param id
     * @param name
     * @param ip
     * @param port
     * @param infoMark
     * @param printerType,打印机类型，58mm为0,80mm为1
     * @param addTime                         ,添加打印机的时间
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
        int printStatus = Printer.PRINTSTATUS_NORMAL;
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try (Socket s = new Socket();) {
            s.connect(socketAddress, connectTimeout);
            s.setSoTimeout(timeout);
            try (OutputStream outputStream = s.getOutputStream(); InputStream inputStream = s.getInputStream();) {
                openAutoReturn(outputStream);
                byte[] inputData = new byte[4];
                inputStream.read(inputData);
                printStatus = StatusUtil.checkDetailedStatus(inputData);
            }
        } catch (IOException ex) {
            printStatus = Printer.PRINTSTATUS_SOCKETTIMEOUT;
        }
        return printStatus;
    }

    @Override
    public PrintResult print(JSONArray jSONArray, boolean isVoicce) throws Exception {
        PrintResultImpl printResultImpl = new PrintResultImpl(this, jSONArray);
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
                boolean isText = true; //如果本模块是文本，则置为true，供下一个模块判断要不要加一个前置换行。如果true，要加，否则后一个模块会接在文本后面；如果不是，不加，否则会多一个空行。
                for (int i = 0; i < jSONArray.size(); i++) {
                    JSONObject jsonObject = jSONArray.getJSONObject(i);
                    int comType = jsonObject.getInteger("ComType");
                    if (comType == TicketCom.TYPE_TEXT) {
                        printText(outputStream, jsonObject);
                        isText = true;
                    } else if (comType == TicketCom.TYPE_TABLE) {
                        printTable(outputStream, jsonObject, isText);
                        isText = false;
                    } else if (comType == TicketCom.TYPE_QRCODE) {
                        printQRCode(outputStream, jsonObject, isText);
                        isText = false;
                    } else if (comType == TicketCom.TYPE_QRCODE2) {
                        printQRCode2(outputStream, jsonObject, isText);
                        isText = false;
                    } else if (comType == TicketCom.TYPE_LINE) {
                        printDotLine(outputStream, jsonObject, isText);
                        isText = false;
                    } else {
                        String detailedInfo = "json第 " + i + " 个元素的type属性错误,错误类型为：type = " + TicketCom.getComType(comType);
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
                        printResultImpl.setSuccess(false);
                        printResultImpl.setResultCode(PrintResult.DATAREADTTIMEOUT);
                        break;
                    }
                    dataRead = PrinterCmdUtil.byteMerger(dataRead, inputData);
                    //                    System.out.println("api.print.PrinterImpl.print()" + Arrays.toString(inputData));

                    if (StatusUtil.checkStatus(inputData)) {
                        if (StatusUtil.checkDetailedStatus(inputData) == StatusUtil.PRINTING) {
                            printingFlag = true;
                        }

                        if (StatusUtil.checkDetailedStatus(inputData) == StatusUtil.NORMAL && printingFlag) {
                            successFlag = true;
                            printResultImpl.setSuccess(true);
                            printResultImpl.setResultCode(PrintResult.NORMAL);
                        }

                    } else {
                        int resultCode = StatusUtil.checkDetailedStatus(inputData);
                        printResultImpl.setSuccess(false);
                        printResultImpl.setResultCode(resultCode);
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
            if (printResultImpl.getResultCode() == PrintResult.INIT) {
                printResultImpl.setResultCode(PrintResult.SOCKETTIMEOUT);
            }
            return printResultImpl;
        } catch (Exception e) {
        }
        ////////////////////事件处理////////////////////
        return printResultImpl;
    }

    private void openAutoReturn(OutputStream outputStream) throws IOException {
        byte[] asb = new byte[]{0x1D, 0x61, 0x0F};//激活自动返回功能，应立即收到一条返回
        outputStream.write(asb);
        outputStream.flush();
    }


    /**
     * 激活免丢单功能和自动返回功能
     */
    private void openPreventLost(OutputStream outputStream) throws IOException, InterruptedException {
        byte[] activeLost = new byte[]{0x1B, 0x73, 0x42, 0x45, -110, -102, 0x01, 0x00, 0x5F, 0x0A};// 激活免丢单功能

        outputStream.write(activeLost);
        outputStream.flush();
        //打印机会有一段反应时间
        Thread.sleep(1000);
    }

    private void closePreventLost() throws IOException {
        try (Socket socket = new Socket(ip, port); OutputStream outputStream = socket.getOutputStream();) {
            byte[] closeActiveLost = new byte[]
                    {
                            0x1B, 0x73, 0x42, 0x45, -110, -102, 0x00, 0x00, 0x5F, 0x0A
                    };// 关闭免丢单功能
            outputStream.write(closeActiveLost);
            outputStream.flush();
        }
    }

    private void feedPaperCut(OutputStream outputStream) throws IOException {
        byte[] nextLine = PrinterCmdUtil.nextLine();
        //切纸行首有效，所以前面添加一个换行符
        byte[] feedPaperCut = PrinterCmdUtil.feedPaperCut();
        byte[][] byteList = new byte[][]{
                nextLine,
                feedPaperCut
        };
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

        byte[] byteFrontWrap = PrinterCmdUtil.nextLine(frontEnterNum);
        byte[] byteFrontSpace = PrinterCmdUtil.printSpace(frontLen);
        byte[] byteSize = PrinterCmdUtil.fontSizeSetBig(size);
        byte[] byteContent = PrinterCmdUtil.printText(sampleContent);
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);
        byte[] byteBackSpace = PrinterCmdUtil.printSpace(behindLen);
        byte[] byteBackWrap = PrinterCmdUtil.nextLine(behindEnterNum);
        byte[][] byteList = new byte[][]{
                byteFrontWrap,
                byteFrontSpace,
                byteSize,
                byteContent,
                byteSizeDefault,
                byteBackSpace,
                byteBackWrap
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }

    private void printQRCode(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws IOException, WriterException {
        int maxSize = 32;
        if (printerType == 1) {
            maxSize = 48;
        }

        String content = jsonObject.getString("Content");
        double width = jsonObject.getDouble("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");

        int size = (int) (charCount * width / 100);
        if (size > maxSize) {
            size = maxSize;
        }

        byte[] nextLine = PrinterCmdUtil.nextLine();
        if (isText) {
            outputStream.write(nextLine);
        }
        byte[] byteFrontWrap = PrinterCmdUtil.nextLine(frontEnterNum);
        byte[] alignCenter = PrinterCmdUtil.alignCenter();
        byte[] byteQRCode = PrinterCmdUtil.printQRCode(content, size * 12);
        byte[] alignLeft = PrinterCmdUtil.alignLeft();
        byte[] byteBackWrap = PrinterCmdUtil.nextLine(behindEnterNum);
        byte[][] byteList = new byte[][]{
                byteFrontWrap,
                alignCenter,
                byteQRCode,
                byteBackWrap,
                alignLeft
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
        print1_5Distance(outputStream);
    }

    private void printQRCode2(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws IOException, WriterException {
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");
        int width = jsonObject.getInteger("Width");
        int heigth = jsonObject.getInteger("Height");
        int qrWidth = jsonObject.getInteger("QrWidth");
        int leftPadding1 = jsonObject.getInteger("LeftPadding1");
        int leftPadding2 = jsonObject.getInteger("LeftPadding2");
        String text1 = jsonObject.getString("Text1");
        String text2 = jsonObject.getString("Text2");

        byte[] nextLine = PrinterCmdUtil.nextLine();
        if (isText) {
            outputStream.write(nextLine);
        }
        byte[] byteFrontWrap = PrinterCmdUtil.nextLine(frontEnterNum);
        byte[] byteQRCode = PrinterCmdUtil.printQRCode2(width, heigth, qrWidth, leftPadding1, leftPadding2, text1, text2);
        byte[] byteBackWrap = PrinterCmdUtil.nextLine(behindEnterNum);
        byte[][] byteList = new byte[][]{
                byteFrontWrap,
                byteQRCode,
                byteBackWrap
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
        print1_5Distance(outputStream);
    }

    private void printRowDistance(OutputStream outputStream, JSONObject jsonObject) throws IOException {
        int rowDistance = jsonObject.getInteger("rowDistance");
        byte[] byteRowDistance = PrinterCmdUtil.lineDistance(rowDistance);
        outputStream.write(byteRowDistance);
        outputStream.flush();

    }

    private void print1_5Distance(OutputStream outputStream) throws IOException {
        byte[] byteRowDistance = PrinterCmdUtil.lineDistance(6);
        outputStream.write(byteRowDistance);
        outputStream.flush();
    }


    private void printDotLine(OutputStream outputStream, JSONObject jsonObject, boolean isText) throws IOException {
        int size = jsonObject.getInteger("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");

        byte[] byteSize = PrinterCmdUtil.fontSizeSetBig(size);
        int realCount = charCount / size;
        String oneDotLine = "- ";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < realCount / 2; i++) {
            stringBuffer.append(oneDotLine);
        }
        byte[] dotLine = PrinterCmdUtil.printText(stringBuffer.toString());
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);
        byte[] byteNextLine = PrinterCmdUtil.nextLine();
        if (isText) {
            outputStream.write(byteNextLine);
        }

        byte[] byteFrontEnterNum = PrinterCmdUtil.nextLine(frontEnterNum);
        byte[] byteBehindEnterNum = PrinterCmdUtil.nextLine(behindEnterNum);

        byte[][] byteList = new byte[][]{
                byteFrontEnterNum,
                byteSize,
                dotLine,
                byteSizeDefault,
                byteNextLine,
                byteBehindEnterNum
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

        byte[] nextLine = PrinterCmdUtil.nextLine();
        if (isText) {
            outputStream.write(nextLine);
        }
        byte[] byteFrontWrap = PrinterCmdUtil.nextLine(frontEnterNum);//保证第一行不会接在上次打印结果的后面
        outputStream.write(byteFrontWrap);
        byte[] byteSize = PrinterCmdUtil.fontSizeSetBig(size);
        outputStream.write(byteSize);
        //设置行间距永远是字体大小的1.5倍
        byte[] byteRowDistance = PrinterCmdUtil.lineDistance(4 * size + 2);
        outputStream.write(byteRowDistance);
        printTableRow(outputStream, columnNames, columnWidths, columnAligns, size);
        for (int i = 0; i < rows.size(); i++) {
            printTableRow(outputStream, rows.getJSONArray(i), columnWidths, columnAligns, size);
        }
        print1_5Distance(outputStream);
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);
        outputStream.write(byteSizeDefault);
        byte[] byteNextLines = PrinterCmdUtil.nextLine(behindEnterNum);
        outputStream.write(byteNextLines);
        outputStream.flush();

    }


    private void printTableRow(OutputStream outputStream, JSONArray oneRow, JSONArray columnWidths, JSONArray columnAligns, int size) throws Exception {
        boolean printFlag = false;
        for (int i = 0; i < oneRow.size(); i++) {
            if (!"".equals(oneRow.getString(i))) {
                printFlag = true;
                break;
            }
        }

        if (!printFlag) {
            return;
        }

        int maxRow = 1;
        double widthPlus = 0;
        JSONArray jsonCells = new JSONArray();
        for (int i = 0; i < columnWidths.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            int paddingLeft = (int) (widthPlus * charCount / 100 / size);
            jsonObject.put("paddingLeft", paddingLeft);
            jsonObject.put("align", columnAligns.getInteger(i));
            int widthChar = (int) (charCount * columnWidths.getInteger(i) / 100 / size);
            if (i == columnWidths.size() - 1) {
                widthChar = (int) (charCount - (int) (widthPlus * charCount / 100)) / size;
            }
            widthPlus += columnWidths.getInteger(i);
            jsonObject.put("widthChar", widthChar);

            List<String> group = StringUtil.getGroup(oneRow.getString(i), widthChar, i + 1);
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
                    int align = jsonObject.getInteger("align");

                    String stringAlign = "left";
                    if (align == BENPAO_ALIGN_CENTER) {
                        stringAlign = "center";
                    } else if (align == BENPAO_ALIGN_RIGHT) {
                        stringAlign = "right";
                    }


                    //                    byte[] bytePaddingLeft = PrinterCmdUtil.hTPositionMove(paddingLeft);
                    byte[] byteContent = PrinterCmdUtil.printText(StringUtil.alignString(group.get(i), widthChar, stringAlign));
                    byte[][] byteList = new byte[][]{
                            //                            bytePaddingLeft,    //填充会随着字号的倍增而倍增，所以左填充放在字体放大之前
                            byteContent,
                    };
                    byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
                    outputStream.write(byteMerger);

                }


            }
            byte[] nextLine = PrinterCmdUtil.nextLine();
            outputStream.write(nextLine);
        }

    }

    private void printTableRowOld(OutputStream outputStream, JSONArray oneRow, JSONArray columnWidths, JSONArray columnAligns, int size) throws UnsupportedEncodingException, Exception {
        boolean printFlag = false;
        for (int i = 0; i < oneRow.size(); i++) {
            if (!"".equals(oneRow.getString(i))) {
                printFlag = true;
                break;
            }
        }

        if (!printFlag) {
            return;
        }

        int maxRow = 1;
        double widthPlus = 0;
        JSONArray jsonCells = new JSONArray();
        for (int i = 0; i < columnWidths.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            int paddingLeft = (int) (widthPlus * charCount / 100 / size);
            jsonObject.put("paddingLeft", paddingLeft);
            jsonObject.put("align", columnAligns.getInteger(i));
            int widthChar = (int) (charCount * columnWidths.getInteger(i) / 100 / size);
            if (i == columnWidths.size() - 1) {
                widthChar = (int) (charCount - (int) (widthPlus * charCount / 100)) / size;
            }
            widthPlus += columnWidths.getInteger(i);
            jsonObject.put("widthChar", widthChar);

            List<String> group = StringUtil.getGroup(oneRow.getString(i), widthChar, i + 1);
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
                    int align = jsonObject.getInteger("align");

                    String stringAlign = "left";
                    if (align == BENPAO_ALIGN_CENTER) {
                        stringAlign = "center";
                    } else if (align == BENPAO_ALIGN_RIGHT) {
                        stringAlign = "right";
                    }


                    byte[] bytePaddingLeft = PrinterCmdUtil.hTPositionMove(paddingLeft);
                    byte[] byteContent = PrinterCmdUtil.printText(StringUtil.alignString(group.get(i), widthChar, stringAlign));
                    byte[][] byteList = new byte[][]{
                            bytePaddingLeft,    //填充会随着字号的倍增而倍增，所以左填充放在字体放大之前
                            byteContent,
                    };
                    byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
                    outputStream.write(byteMerger);

                }


            }
            byte[] nextLine = PrinterCmdUtil.nextLine();
            outputStream.write(nextLine);
        }

    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getInfoMark() {
        return infoMark;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public long getAddTime() {
        return addTime;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public int getPrinterType() {
        return printerType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInfoMark(String infoMark) {
        this.infoMark = infoMark;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPrinterType(int printerType) {
        this.printerType = printerType;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (id != ((PrinterImpl) obj).getId()) {
            return false;
        }
        return true;
    }

}
