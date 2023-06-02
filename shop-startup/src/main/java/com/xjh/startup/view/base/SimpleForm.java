package com.xjh.startup.view.base;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Const;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SimpleForm extends VBox {
    public void addLine(Node node) {
        this.getChildren().add(node);
    }

    public void addPairLine(Region title, Region content) {
        if (title instanceof Label) {
            ((Label) title).setAlignment(Pos.CENTER_RIGHT);
        }
        addLine(newCenterLine(title, content));
    }

    public HBox newLine(Node... nodes) {
        HBox line = new HBox();
        line.setSpacing(Const.REGULAR_SPACING);
        line.setPadding(new Insets(0, 0, 0, 10));
        for (Node n : nodes) {
            line.getChildren().add(n);
        }
        return line;
    }

    public HBox newCenterLine(Node... nodes) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER);
        line.setSpacing(Const.REGULAR_SPACING);
        for (Node n : nodes) {
            line.getChildren().add(n);
        }
        return line;
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
        if(CommonUtils.isNotBlank(promptText)) {
            textField.setPromptText(promptText);
        }
        if(width > 0) {
            textField.setPrefWidth(width);
        }
        return textField;
    }

    public Label createLabel(String text) {
        return createLabel(text, 0);
    }

    public Label createLabel(String text, double width) {
        Label label = new Label(text);
        if(width > 0){
            label.setPrefWidth(width);
        }
        return label;
    }
}
