package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.startup.view.base.SmallForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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

        TableView<DishesAttributeValueVO> attrValueTV = new TableView<>();
        attrValueTV.getColumns().add(
                newCol("属性值", "attributeValue", 100)
        );
        attrValueTV.setMaxHeight(200);
        ObservableList<DishesAttributeValueVO> attrValues =
                FXCollections.observableArrayList(attr.getAllAttributeValues());
        attrValueTV.setItems(attrValues);
        addLine(attrValueTV);

        Button update = new Button("保存属性");
        Button addAttr = new Button("增加属性");

        addLine(newLine(addAttr, update));
    }
}
