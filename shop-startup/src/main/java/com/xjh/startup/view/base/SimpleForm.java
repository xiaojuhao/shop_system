package com.xjh.startup.view.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SimpleForm extends VBox {
    public void addLine(Node node) {
        this.getChildren().add(node);
    }

    public HBox newLine(Node... nodes) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.setSpacing(10);
        for (Node n : nodes) {
            line.getChildren().add(n);
        }
        return line;
    }
}
