package com.xjh.startup.view.base;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ModelWindow extends Stage {
    public ModelWindow(Window window){
        this(window, null);
    }

    public ModelWindow(Window window, String title){
        this.initOwner(window);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.DECORATED);
        this.centerOnScreen();
        this.setWidth(window.getWidth() * 0.9);
        this.setHeight(window.getHeight() * 0.9);
        if(title != null && !title.trim().isEmpty()){
            this.setTitle(title);
        }
    }
}
