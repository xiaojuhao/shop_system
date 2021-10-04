package com.xjh.startup.another;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.FileUtils;
import com.xjh.startup.foundation.utils.PrintResult;
import com.xjh.startup.foundation.utils.PrinterImpl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PrinterTest extends Application {
    String ip = "192.168.1.4";
    int port = 9100;

    @Override
    public void start(Stage primaryStage) throws Exception {
        PrinterImpl printer = new PrinterImpl(1, "打印机", ip, port,
                "mark", 1, 1,
                System.currentTimeMillis());
        HBox box = new HBox();
        Button btn = new Button("打印");
        btn.setOnAction(evt -> {
            try {
                PrintResult ps = printer.print(loadJson("/data/ticket.json"), true);
                System.out.println("打印结果:" + JSON.toJSONString(ps));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        box.getChildren().add(btn);
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
        launch(args);
    }
}
