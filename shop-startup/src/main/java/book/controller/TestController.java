package book.controller;

import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TestController extends Application {
    public void start(Stage primaryStage) throws Exception {
        Stage s1 = new Stage();
        //具有纯白色背景和平台装饰的舞台。
        s1.initStyle(StageStyle.DECORATED);
        s1.setTitle("s1");
        s1.setMaximized(true);
        s1.show();

        //        Stage s2 = new Stage();
        //        //一个纯白色背景且没有装饰的舞台。
        //        s2.initStyle(StageStyle.UNDECORATED);
        //        s2.setTitle("s2");
        //        s2.show();

        //        Stage s3 = new Stage();
        //        //具有透明背景且没有装饰的舞台。
        //        s3.initStyle(StageStyle.TRANSPARENT);
        //        s3.setTitle("s3");
        //        s3.show();

        Stage s4 = new Stage();
        //具有纯白色背景和最少平台装饰的舞台。
        //        s4.initStyle(StageStyle.UTILITY);
        s4.initOwner(s1);
        // 子窗口的模态对话框
        s4.initModality(Modality.WINDOW_MODAL);
        s4.setTitle("s4");
        s4.setWidth(s1.getWidth() / 2);
        s4.setHeight(s1.getHeight() / 2);
        s4.show();
        s4.centerOnScreen(); //设置居中

        Stage s5 = new Stage();
        s5.initOwner(s4);
        s5.initModality(Modality.WINDOW_MODAL);
        s5.setTitle("s4");
        s5.setWidth(s4.getWidth() / 2);
        s5.setHeight(s4.getHeight() / 2);
        s5.show();
        s5.centerOnScreen(); //设置居中
    }

    public static void main(String[] args) {
        launch(args);
    }
}
