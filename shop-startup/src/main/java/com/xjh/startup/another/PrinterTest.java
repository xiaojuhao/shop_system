package com.xjh.startup.another;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.FileUtils;
import com.xjh.startup.foundation.utils.PrinterCmdUtil;
import com.xjh.startup.foundation.utils.PrinterImpl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PrinterTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PrinterImpl printer = new PrinterImpl(1, "打印机", ip, port,
                "mark", 1, 1,
                System.currentTimeMillis());
        HBox box = new HBox();
        Button btn = new Button("打印");
        btn.setOnAction(evt -> {
            try {
                printer.print(loadJson(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        box.getChildren().add(btn);
        primaryStage.setScene(new Scene(box, 600, 600));
        primaryStage.show();
    }

    String ip = "192.168.1.4";
    int port = 9100;
    int connectTimeout = 5000;
    int timeout = 5000;

    private void print() throws Exception {
        SocketAddress socketAddress = new InetSocketAddress(this.ip, this.port);

        Socket s = new Socket();
        s.connect(socketAddress, this.connectTimeout);
        s.setSoTimeout(this.timeout);

        OutputStream outputStream = s.getOutputStream();
        InputStream inputStream = s.getInputStream();
        initPrinter(outputStream);
        openAutoReturn(outputStream);
        print1_5Distance(outputStream);

        printText(outputStream, null);

        feedPaperCut(outputStream);
        voice(outputStream);
        outputStream.close();
    }

    private void initPrinter(OutputStream outputStream) throws IOException {
        byte[] initPrinter = PrinterCmdUtil.initPrinter();
        outputStream.write(initPrinter);
        outputStream.flush();
    }

    private void openAutoReturn(OutputStream outputStream) throws IOException {
        byte[] asb = new byte[]{29, 97, 15};
        outputStream.write(asb);
        outputStream.flush();
    }

    private void print1_5Distance(OutputStream outputStream) throws IOException {
        byte[] byteRowDistance = PrinterCmdUtil.lineDistance(6);
        outputStream.write(byteRowDistance);
        outputStream.flush();
    }

    private void feedPaperCut(OutputStream outputStream) throws IOException {
        byte[] nextLine = PrinterCmdUtil.nextLine();
        byte[] feedPaperCut = PrinterCmdUtil.feedPaperCut();
        byte[][] byteList = new byte[][]{nextLine, feedPaperCut};
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }

    private void voice(OutputStream outputStream) throws IOException {
        byte[] voice = PrinterCmdUtil.voice();
        outputStream.write(voice);
        outputStream.flush();
    }

    private void printText(OutputStream outputStream, JSONObject jsonObject) throws Exception {
        String sampleContent = "测试00001";//jsonObject.getString("SampleContent");
        int size = 2;// jsonObject.getInt("Size");
        int frontEnterNum = 1;//jsonObject.getInt("FrontEnterNum");
        int behindEnterNum = 1;//jsonObject.getInt("BehindEnterNum");
        int frontLen = 3;//jsonObject.getInt("FrontLen");
        int behindLen = 3;//jsonObject.getInt("BehindLen");
        // 换行：frontEnterNum 行数
        byte[] byteFrontWrap = PrinterCmdUtil.nextLine(frontEnterNum);
        // 打印字符
        byte[] byteContent0 = PrinterCmdUtil.printText(sampleContent);
        // 打印空格
        byte[] byteFrontSpace = PrinterCmdUtil.printSpace(frontLen);
        // 设置字符大小
        byte[] byteSize = PrinterCmdUtil.fontSizeSetBig(size);
        // 打印字符
        byte[] byteContent = PrinterCmdUtil.printText(sampleContent);
        // 重置字符大小
        byte[] byteSizeDefault = PrinterCmdUtil.fontSizeSetBig(1);
        // 打印空格
        byte[] byteBackSpace = PrinterCmdUtil.printSpace(behindLen);
        // 换行：behindEnterNum个数
        byte[] byteBackWrap = PrinterCmdUtil.nextLine(behindEnterNum);

        // byte[] qrcode = PrinterCmdUtil.printQRCode("http://www.baidu.com", 300);


        byte[][] byteList = new byte[][]{byteFrontWrap,
                byteContent0,
                byteFrontSpace, byteSize, byteContent, byteSizeDefault, byteBackSpace, byteBackWrap,
                // PrinterCmdUtil.nextLine(2),
                // qrcode
        };
        byte[] byteMerger = PrinterCmdUtil.byteMerger(byteList);
        outputStream.write(byteMerger);
        outputStream.flush();
    }

    static JSONArray loadJson() throws Exception {
        String url = PrinterTest.class.getResource("/data/ticket.json")
                .toURI().toURL().getPath();
        File file = new File(url);
        String content = FileUtils.readFile(file);
        return JSONArray.parseArray(content);
    }

    public static void main(String[] args) throws Exception {
        // loadJson();
        launch(args);
    }
}
