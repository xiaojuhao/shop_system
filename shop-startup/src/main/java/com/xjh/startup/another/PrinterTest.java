package com.xjh.startup.another;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.enumeration.EnumPrinterType;
import com.xjh.common.utils.FileUtils;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.service.printers.PrintResult;
import com.xjh.service.printers.PrinterImpl;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PrinterTest {

    static void directPrint() throws Exception {
        PrinterDO dd = new PrinterDO();
        dd.setPrinterId(1);
        dd.setPrinterName("打印机");
        dd.setPrinterIp("192.168.1.7");
        dd.setPrinterPort(9100);
        dd.setPrinterType(EnumPrinterType.T80.code);
        dd.setPrinterStatus(1);

        PrinterImpl printer = new PrinterImpl(dd);
        Future<PrintResult> ps = printer.submitTask(loadJson("/data/ticket2.json"), true);
        System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
        System.exit(0);
    }

    public void start(Stage primaryStage) throws Exception {
        PrinterDO dd = new PrinterDO();
        dd.setPrinterId(1);
        dd.setPrinterName("打印机");
        dd.setPrinterIp("192.168.1.9");
        dd.setPrinterPort(9100);
        dd.setPrinterType(1);
        dd.setPrinterStatus(1);

        PrinterImpl printer = new PrinterImpl(dd);
        HBox box = new HBox();
        Button btn = new Button("打印");
        btn.setOnAction(evt -> {
            try {
                Future<PrintResult> ps = printer.submitTask(loadJson("/data/ticket.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button btn2 = new Button("结账80毫米");
        btn2.setOnAction(evt -> {
            try {
                Future<PrintResult> ps = printer.submitTask(loadJson("/data/ticket2.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button btn3 = new Button("后厨80毫米");
        btn3.setOnAction(evt -> {
            try {
                Future<PrintResult> ps = printer.submitTask(loadJson("/data/ticket3.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button btn4 = new Button("FONT");
        btn4.setOnAction(evt -> {
            try {
                Future<PrintResult> ps = printer.submitTask(loadJson("/data/font.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button btn5 = new Button("数字");
        btn5.setOnAction(evt -> {
            try {
                Future<PrintResult> ps = printer.submitTask(loadJson("/data/text.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps.get(3, TimeUnit.SECONDS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        box.getChildren().addAll(btn, btn2, btn3, btn4, btn5);
        primaryStage.setScene(new Scene(box, 600, 600));
        primaryStage.show();
    }

    static JSONArray loadJson(String resource) throws Exception {
        String url = PrinterTest.class.getResource(resource)
                .toURI().toURL().getPath();
        File file = new File(url);
        String content = FileUtils.readFile(file);
        return JSONArray.parseArray(content);
    }

    public static void main(String[] args) throws Exception {
        // loadJson();
        // launch(args);
        directPrint();
    }
}
