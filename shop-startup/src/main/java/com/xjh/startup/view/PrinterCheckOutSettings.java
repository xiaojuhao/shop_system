package com.xjh.startup.view;

import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;

import javafx.scene.control.Label;

public class PrinterCheckOutSettings extends SimpleForm implements Initializable {

    @Override
    public void initialize() {
        addLine(newLine(new Label("结账打印设置")));
    }
}
