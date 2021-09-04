package com.xjh.startup.view.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SmallForm extends SimpleForm {

    public SmallForm() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
    }

    public void addLine(Node node) {
        this.getChildren().add(node);
    }

    public HBox newDefaultLine(Node... nodes) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.setSpacing(10);
        for (Node n : nodes) {
            line.getChildren().add(n);
        }
        return line;
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
