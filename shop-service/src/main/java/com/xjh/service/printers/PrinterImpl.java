/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.service.printers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumAlign;
import com.xjh.common.enumeration.EnumComType;
import com.xjh.common.enumeration.EnumPrinterType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.OrElse;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.service.printers.models.TableCellModel;
import com.xjh.service.printers.models.TableRowModel;
import com.xjh.service.printers.models.TextModel;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.*;


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
    PrinterDO printerDO;
    private EnumPrinterType printerType = EnumPrinterType.T58;
    // 默认58毫米打印32个字符
    private int timeout = 3 * 1000; //输入流读取超时时间
    private int connectTimeout = 3 * 1000; //socket连接超时时间
    ExecutorService executorService = new ThreadPoolExecutor(
            1, 1,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public PrinterImpl(PrinterDO dd) {
        this.printerDO = dd;
        printerType = EnumPrinterType.of(dd.getPrinterType());
    }

    public int checkPrinter() {
        int printerStatus;
        SocketAddress socketAddress = new InetSocketAddress(printerDO.getPrinterIp(), printerDO.getPrinterPort());
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
            printerStatus = PrinterStatus.SOCKET_TIMEOUT.status;
        }
        return printerStatus;
    }

    public Future<PrintResult> submitTask(List<Object> contentItems, boolean isVoicce) {
        return executorService.submit(() -> {
            try {
                return print(contentItems, isVoicce);
            } catch (Exception ex) {
                ex.printStackTrace();
                PrintResult fail = new PrintResult(this, contentItems);
                fail.toFailure(PrinterStatus.UNKNOWN.status);
                return fail;
            }
        });
    }

    public synchronized PrintResult print(List<Object> contentItems, boolean isVoicce) throws Exception {
        System.out.println("调用打印机: " + printerDO.getPrinterIp()+":"+printerDO.getPrinterPort());
        PrintResult printResult = new PrintResult(this, contentItems);
        SocketAddress socketAddress = new InetSocketAddress(printerDO.getPrinterIp(), printerDO.getPrinterPort());
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
                for (int i = 0; i < contentItems.size(); i++) {
                    Object contentItem = contentItems.get(i);
                    EnumComType comType = null;
                    if (contentItem instanceof JSONObject) {
                        JSONObject jsonItem = (JSONObject) contentItem;
                        comType = EnumComType.of(jsonItem.getInteger("ComType"));
                        if (comType == EnumComType.TEXT) {
                            printText(outputStream, jsonItem);
                        } else if (comType == EnumComType.TABLE) {
                            printTable(outputStream, jsonItem);
                        } else if (comType == EnumComType.QRCODE) {
                            printQRCode(outputStream, jsonItem);
                        } else if (comType == EnumComType.QRCODE2) {
                            printQRCode2(outputStream, jsonItem);
                        } else if (comType == EnumComType.LINE) {
                            printDotLine(outputStream, jsonItem);
                        } else {
                            Logger.info("不支持的打印类型。。。。。。");
                            String detailedInfo = "json第 " + i + " 个元素的type属性错误,错误类型为：type = " + comType;
                            throw new Exception(detailedInfo + ", " + comType);
                        }
                    } else if (contentItem instanceof TextModel) {
                        printText(outputStream, (TextModel) contentItem);
                    } else {
                        Logger.info("不支持的打印类型。。。。。。");
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
                        printResult.toFailure(PrinterStatus.DATA_READ_TIMEOUT.status);
                        break;
                    }
                    dataRead = PrinterCmdUtil.byteMerger(dataRead, inputData);

                    if (StatusUtil.checkStatus(inputData)) {
                        if (StatusUtil.checkDetailedStatus(inputData) == PrinterStatus.PRINTING.status) {
                            printingFlag = true;
                        }
                        if (StatusUtil.checkDetailedStatus(inputData) == PrinterStatus.NORMAL.status && printingFlag) {
                            successFlag = true;
                            printResult.toSuccess(PrinterStatus.NORMAL.status);
                        }
                    } else {
                        int resultCode = StatusUtil.checkDetailedStatus(inputData);
                        printResult.toFailure(resultCode);
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
            Logger.info("打印出错了 .......");
            e.printStackTrace();
            printResult.setSuccess(false);
            if (printResult.getResultCode() == PrinterStatus.INIT.status) {
                printResult.setResultCode(PrinterStatus.SOCKET_TIMEOUT.status);
            }
            return printResult;
        } catch (Exception e) {
            e.printStackTrace();
            printResult.toFailure(PrinterStatus.UNKNOWN.status);
        }
        ////////////////////事件处理////////////////////
        return printResult;
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

    private void printText(OutputStream outputStream, TextModel model) throws IOException {
        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(model.getFrontEnterNum()),
                PrinterCmdUtil.printSpace(model.getFrontLen()),
                PrinterCmdUtil.fontSizeSetBig(model.getSize()), // 设置字体大小
                PrinterCmdUtil.printText(model.getSampleContent()), // 打印内容
                PrinterCmdUtil.fontSizeSetBig(1), // 恢复字体大小
                PrinterCmdUtil.printSpace(model.getBehindLen()), PrinterCmdUtil.nextLine(model.getBehindEnterNum())};
        outputStream.write(PrinterCmdUtil.byteMerger(byteList));
        outputStream.flush();
    }

    private void printText(OutputStream outputStream, JSONObject jsonObject) throws IOException {
        String sampleContent = jsonObject.getString("SampleContent");
        Integer size = jsonObject.getInteger("Size");
        Integer frontEnterNum = OrElse.orGet(jsonObject.getInteger("FrontEnterNum"), 0);
        Integer behindEnterNum = OrElse.orGet(jsonObject.getInteger("BehindEnterNum"), 0);
        Integer frontLen = OrElse.orGet(jsonObject.getInteger("FrontLen"), 0);
        Integer behindLen = OrElse.orGet(jsonObject.getInteger("BehindLen"), 0);

//        byte[][] byteList = new byte[][]{
//                PrinterCmdUtil.nextLine(frontEnterNum),
//                PrinterCmdUtil.printSpace(frontLen),
//                PrinterCmdUtil.fontSizeSetBig(size), // 设置字体大小
//                PrinterCmdUtil.printText(sampleContent), // 打印内容
//                PrinterCmdUtil.fontSizeSetBig(1), // 恢复字体大小
//                PrinterCmdUtil.printSpace(behindLen),
//                PrinterCmdUtil.nextLine(behindEnterNum)
//        };
//        outputStream.write(PrinterCmdUtil.byteMerger(byteList));
//        outputStream.flush();
        printText(outputStream,
                TextModel.builder().sampleContent(sampleContent).size(size).frontEnterNum(frontEnterNum)
                        .behindEnterNum(behindEnterNum).frontLen(frontLen).behindLen(behindLen).build());
    }

    private void printQRCode(OutputStream outputStream, JSONObject jsonObject) throws Exception {
        int maxSize = (printerDO.getPrinterType() == 1 ? 48 : 32);
        String content = jsonObject.getString("Content");
        double width = jsonObject.getDouble("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");
        int size = Math.min((int) (printerType.numOfChars * width / 100), maxSize);

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

    private void printQRCode2(OutputStream outputStream, JSONObject jsonObject) throws Exception {
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
        int realCount = printerType.numOfChars / size;
        byte[] hyphenLine = PrinterCmdUtil.printText(CommonUtils.repeatStr("- ", realCount / 2));
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);

        byte[][] byteList = new byte[][]{
                PrinterCmdUtil.nextLine(frontEnterNum),
                byteSize,
                hyphenLine,
                byteSizeDefault,
                PrinterCmdUtil.nextLine(behindEnterNum)
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }


    private void printTable(OutputStream outputStream, JSONObject jsonObject) throws Exception {
        int size = jsonObject.getInteger("Size");
        int frontEnterNum = jsonObject.getInteger("FrontEnterNum");
        int behindEnterNum = jsonObject.getInteger("BehindEnterNum");

        JSONArray columnNames = jsonObject.getJSONArray("columnNames");
        JSONArray columnWidths = jsonObject.getJSONArray("columnWidths");
        JSONArray columnAligns = jsonObject.getJSONArray("columnAligns");
        JSONArray rows = jsonObject.getJSONArray("rows");

        // 保证第一行不会接在上次打印结果的后面
        outputStream.write(PrinterCmdUtil.nextLine(frontEnterNum));
        // 设置字符大小
        outputStream.write(PrinterCmdUtil.fontSizeSetBig(size));
        // 设置行间距永远是字体大小的1.5倍
        outputStream.write(PrinterCmdUtil.lineDistance(4 * size + 2));
        printTableRow(outputStream, columnNames, columnWidths, columnAligns, size);
        for (int i = 0; i < rows.size(); i++) {
            // printTableRow(outputStream, rows.getJSONArray(i), columnWidths, columnAligns, size);
            TableRowModel row = new TableRowModel();
            for (int j = 0; j < columnWidths.size(); j++) {
                row.addCell(rows.getJSONArray(i).getString(j), columnWidths.getInteger(j), columnAligns.getInteger(j));
            }
            for (TableRowModel subRow : row.split(printerType)) {
                Logger.info(subRow.formatStr());
                printTableRow(outputStream, subRow, size);
                outputStream.write(PrinterCmdUtil.nextLine(1));
            }
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
            int colWidth = columnWidths.getInteger(i); // 宽度百分比
            int colAlign = columnAligns.getInteger(i); // 对齐方式
            int paddingLeft = (int) (widthPlus * printerType.numOfChars / 100 / size);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("paddingLeft", paddingLeft);
            jsonObject.put("align", colAlign);
            // 打印的字符数（按打印纸的行总字符数，按百分比折算）
            int widthChar = (printerType.numOfChars * colWidth / 100 / size);
            if (i == columnWidths.size() - 1) {
                widthChar = (printerType.numOfChars - (int) (widthPlus * printerType.numOfChars / 100)) / size;
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
                JSONObject cellMeta = jsonCell.getJSONObject(0);
                List<String> group = (List<String>) jsonCell.get(1);

                if (group.size() > i) {
                    // int paddingLeft = cellMeta.getInteger("paddingLeft");
                    int widthChar = cellMeta.getInteger("widthChar");   //12223333
                    EnumAlign align = EnumAlign.of(cellMeta.getInteger("align"));

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
        }
    }

    private void printTableRow(
            OutputStream outputStream,
            TableRowModel row,
            int size) throws Exception {
        // 整个数据都是空，不打印
        if (row == null || row.isEmpty()) {
            return;
        }

        for (TableCellModel cell : row.getCells()) {
            int widthChar = cell.getCharWidth();   //12223333
            EnumAlign align = EnumAlign.of(cell.getAlign());

            byte[] byteContent = PrinterCmdUtil.printText(StringUtil.alignString(cell.getText(), widthChar, align));
            byte[][] byteList = new byte[][]{
                    byteContent,
            };
            byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
            outputStream.write(byteMerger);
        }
    }
}
