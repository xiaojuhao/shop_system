package com.xjh.startup.view;

import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;

import javafx.scene.control.Label;

public class PrinterKitchenSettings extends SimpleForm implements Initializable {

    @Override
    public void initialize() {
        addLine(newLine(new Label("后厨打印设置")));
    }
}
