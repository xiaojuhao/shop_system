package com.xjh.startup.another;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GroupTest extends Application {
    public static List<Circle> circles = new ArrayList<Circle>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Label first = new Label("第一个");
        first.setLayoutY(0);
        Label snd = new Label("第二个");
        snd.setLayoutY(100);
        Button third = new Button("第三个");
        third.setLayoutY(200);
        root.getChildren().addAll(first, snd, third);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


}
