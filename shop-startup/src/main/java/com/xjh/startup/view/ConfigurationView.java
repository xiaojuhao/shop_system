package com.xjh.startup.view;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.service.domain.ConfigService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.MediumForm;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;

import java.util.List;

import static com.xjh.service.domain.ConfigService.toProp;

public class ConfigurationView extends MediumForm {
    ConfigService configService = GuiceContainer.getInstance(ConfigService.class);

    public ConfigurationView() {
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();
        TextArea ta = new TextArea();
        ta.setPrefWidth(width * 0.9);
        ta.setPrefHeight(height * 0.8);
        ta.setText(toProp(configService.loadSysCfg()));
        this.getChildren().add(ta);

        Button button = new Button("保 存");
        button.setOnAction(evt -> {
            String text = ta.getText();
            List<ConfigService.CO> coList = ConfigService.toCOList(text);
            System.out.println(coList);
            configService.saveCOList(coList);
            AlertBuilder.INFO("保存成功");
        });
        this.getChildren().add(button);
    }
}
