package com.xjh.startup.view;

import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.startup.view.base.SmallForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DishesAttributeEditView extends SmallForm {

    public DishesAttributeEditView(DishesAttributeVO attr) {
        super();
        Label nameLabel = new Label("属性名:");
        nameLabel.setPrefWidth(100);
        TextField nameText = new TextField();
        nameText.setText(attr.getDishesAttributeName());
        nameText.setPrefWidth(200);
        addLine(newLine(nameLabel, nameText));

        Label modelLabel = new Label("属性类型:");
        modelLabel.setPrefWidth(100);
        ObservableList<String> options = FXCollections.observableArrayList("单选", "复选");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.setPrefWidth(200);
        addLine(newLine(modelLabel, modelSelect));

        Label markLabel = new Label("属性备注");
        markLabel.setPrefWidth(100);
        TextField markInput = new TextField();
        markInput.setText(attr.getDishesAttributeMarkInfo());
        markInput.setPrefWidth(200);
        addLine(newLine(markLabel, markInput));


    }
}
