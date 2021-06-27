package com.xjh.startup.view;

import com.xjh.startup.view.model.DeskOrderParam;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class CartView extends VBox {
    private DeskOrderParam param;

    public CartView(DeskOrderParam param) {
        this.param = param;
        this.getChildren().add(tableList());
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
    }

    private TableView tableList() {
        return new TableView();
    }
}
