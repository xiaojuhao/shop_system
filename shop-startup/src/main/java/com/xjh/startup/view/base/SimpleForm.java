package com.xjh.startup.view.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SimpleForm extends VBox {
    public void addLine(Node node) {
        this.getChildren().add(node);
    }

    public void addPairLine(Region title, double titleWidth, Region content, double contentWidth){
        title.setPrefWidth(titleWidth);
        content.setPrefWidth(contentWidth);
        addLine(newLine(title, content));
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

    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    public Label createLabel(String text) {
        return new Label(text);
    }
}
