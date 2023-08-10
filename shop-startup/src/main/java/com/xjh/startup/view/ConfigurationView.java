package com.xjh.startup.view;

import com.xjh.common.model.ConfigurationBO;
import com.xjh.startup.view.base.MediumForm;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;

public class ConfigurationView extends MediumForm {

    public ConfigurationView() {
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();
        TextArea ta = new TextArea();
        ta.setPrefWidth(width * 0.9);
        ta.setPrefHeight(height * 0.8);
        ta.setText(new ConfigurationBO().toProp());
        this.getChildren().add(ta);

        Button button = new Button("保 存");
        this.getChildren().add(button);
    }
}
