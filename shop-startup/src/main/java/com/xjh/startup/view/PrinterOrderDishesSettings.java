package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PrinterOrderDishesSettings extends SimpleForm implements Initializable {

    @Override
    public void initialize() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));
        // 打印张数
        TextField printNumInput = new TextField();
        addLine(newLine(new Label("打印张数:"), printNumInput));
        // 打印机策略
        addLine(new Label("打印机策略:"));
        TableView<?> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("序号", "sno", 100),
                newCol("餐桌类型", "type", 150),
                newCol("指定打印机", "printer", 200)
        );
        addLine(tv);
    }
}
