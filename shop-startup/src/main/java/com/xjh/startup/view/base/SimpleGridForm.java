package com.xjh.startup.view.base;

import com.xjh.common.utils.CommonUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class SimpleGridForm extends GridPane {
    int row = -1;

    public SimpleGridForm() {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(20, 100, 10, 10));
    }

    public void addLine(String title, Node content) {
        addLine(new Label(title), content);
    }

    public void addLine(Node title, Node content) {
        row++;
        if (title instanceof Label) {
            ((Label) title).setAlignment(Pos.CENTER_RIGHT);
        }
        if (title != null) {
            this.add(title, 0, row);
        }
        if (content != null) {
            this.add(content, 1, row);
        }
    }

    public HBox hbox(Node... nodes) {
        HBox box = new HBox();
        box.getChildren().addAll(nodes);
        return box;
    }

    public TextField initTextField(String text) {
        return initTextField(text, 0);
    }

    public TextField initTextField(String text, double width) {
        TextField tf = createTextField(null, width);
        tf.setText(text);
        return tf;
    }

    public TextField createTextField(String promptText) {
        return createTextField(promptText, 0);
    }

    public TextField createTextField(String promptText, double width) {
        TextField textField = new TextField();
        if (CommonUtils.isNotBlank(promptText)) {
            textField.setPromptText(promptText);
        }
        if (width > 0) {
            textField.setPrefWidth(width);
        }
        return textField;
    }

    public Label createLabel(String text) {
        return createLabel(text, 0);
    }

    public Label createLabel(String text, double width) {
        Label label = new Label(text);
        if (width > 0) {
            label.setPrefWidth(width);
        }
        return label;
    }
}
