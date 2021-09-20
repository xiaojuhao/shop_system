package com.xjh.startup.view;

import com.xjh.dao.dataobject.Dishes;
import com.xjh.startup.view.base.SimpleForm;

import javafx.scene.control.Button;

public class DishesEditView extends SimpleForm {
    public DishesEditView(Dishes dishes) {
        addLine(new Button("编辑--" + dishes.getDishesName()));
    }
}
