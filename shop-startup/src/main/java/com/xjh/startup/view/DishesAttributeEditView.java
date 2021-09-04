package com.xjh.startup.view;

import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.startup.view.base.SmallForm;

import javafx.scene.control.Label;

public class DishesAttributeEditView extends SmallForm {

    public DishesAttributeEditView(DishesAttributeVO attr) {
        super();
        addLine(newLine(new Label("属性名称:"), new Label(attr.getDishesAttributeName())));
        addLine(newLine(new Label("选项"), new Label(attr.getDishesAttributeMarkInfo())));
    }
}
