package com.xjh.startup.view;

import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

public class PrinterTaskManageView extends SimpleForm implements Initializable {
    @Override
    public void initialize() {
        PrinterOrderDishesSettings printerOrderDishesSettings = new PrinterOrderDishesSettings();
        PrinterKitchenSettings printerKitchenSettings = new PrinterKitchenSettings();
        PrinterCheckOutSettings printerCheckOutSettings = new PrinterCheckOutSettings();

        Window window = this.getScene().getWindow();
        Tab tab1 = new Tab("点餐打印");
        tab1.setClosable(false);
        tab1.setContent(printerOrderDishesSettings);
        tab1.setOnSelectionChanged(evt -> printerOrderDishesSettings.initialize());

        Tab tab2 = new Tab("后厨打印");
        tab2.setClosable(false);
        tab2.setContent(printerKitchenSettings);
        tab2.setOnSelectionChanged(evt -> printerKitchenSettings.initialize());

        Tab tab3 = new Tab("结账打印");
        tab3.setClosable(false);
        tab3.setContent(printerCheckOutSettings);
        tab3.setOnSelectionChanged(evt -> printerCheckOutSettings.initialize());

        TabPane pane = new TabPane(tab1, tab2, tab3);
        HBox line = newCenterLine(pane);
        line.setPrefWidth(window.getWidth() * 0.8);
        addLine(line);
    }
}
