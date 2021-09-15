package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SmallForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class DishesAttributeEditView extends SmallForm {
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);


    ObservableList<AttributeValueBO> attrList = FXCollections.observableArrayList();
    DishesAttributeVO data;
    List<Runnable> collectData = new ArrayList<>();

    public DishesAttributeEditView(DishesAttributeVO attr) {
        super();

        data = CommonUtils.deepClone(attr, DishesAttributeVO.class);
        double titleWidth = 100;
        double contentWidth = 200;

        TextField nameText = new TextField();
        nameText.setText(attr.getDishesAttributeName());
        addPairLine(new Label("属性名:"), titleWidth, nameText, contentWidth);
        collectData.add(() -> data.setDishesAttributeName(nameText.getText()));

        ObservableList<String> options = FXCollections.observableArrayList("单选", "复选");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.getSelectionModel().select((attr.getIsValueRadio() != null && attr.getIsValueRadio()) ? "单选" : "复选");
        addPairLine(new Label("属性类型:"), titleWidth, modelSelect, contentWidth);
        collectData.add(() -> data.setIsValueRadio("单选".equals(modelSelect.getSelectionModel().getSelectedItem())));

        TextField markInput = new TextField();
        markInput.setText(attr.getDishesAttributeMarkInfo());
        addPairLine(new Label("属性备注:"), titleWidth, markInput, contentWidth);
        collectData.add(() -> data.setDishesAttributeMarkInfo(markInput.getText()));
        TableView<AttributeValueBO> tv = new TableView<>();
        tv.getColumns().addAll(
                newCol("属性值", "attributeValue", 200),
                newCol("操作", "action", 100)
        );
        tv.setMaxHeight(200);
        tv.setItems(attrList);
        collectData.add(() -> {
            List<DishesAttributeValueVO> vals = CommonUtils.collect(attrList, it -> {
                DishesAttributeValueVO v = new DishesAttributeValueVO();
                v.setAttributeValue(it.getAttributeValue());
                return v;
            });
            data.setAllAttributeValues(vals);
        });
        CommonUtils.forEach(attr.getAllAttributeValues(), v -> {
            AttributeValueBO bo = new AttributeValueBO();
            bo.setAttributeValue(v.getAttributeValue());
            OperationButton op = new OperationButton();
            op.setTitle("删除");
            op.setAction(() -> removeItem(tv, v.getAttributeValue()));
            bo.setAction(op);
            attrList.add(bo);
        });
        addLine(tv);
        tv.refresh();
        Button update = new Button("保 存");
        update.setOnAction(e -> {
            CommonUtils.safeRun(collectData);
            this.updateAttr(data);
        });
        Button addAttr = new Button("增 加");
        addAttr.setOnAction(evt -> {
            ModelWindow stg = new ModelWindow(this.getScene().getWindow(), "增加属性");
            stg.setHeight(200);
            SmallForm sform = new SmallForm();
            TextField textField = createTextField("属性值", stg.getWidth() - 100);
            sform.addLine(sform.newLine(createLabel("名称"), textField));
            Button confirm = new Button("确 定");
            sform.addLine(sform.newLine(confirm));
            confirm.setOnAction(e -> {
                addItem(tv, textField.getText());
                stg.hide();
            });
            stg.setScene(new Scene(sform));
            stg.showAndWait();
        });
        addLine(newLine(addAttr, update));
    }

    private void removeItem(TableView<AttributeValueBO> tv, String item) {
        tv.getItems().stream()
                .filter(it -> it.getAttributeValue().equals(item))
                .findFirst().ifPresent(tv.getItems()::remove);
        tv.refresh();
    }

    private void addItem(TableView<AttributeValueBO> tv, String item) {
        AttributeValueBO t = new AttributeValueBO();
        t.setAttributeValue(item);
        OperationButton op = new OperationButton();
        op.setTitle("删除");
        op.setAction(() -> removeItem(tv, item));
        t.setAction(op);
        attrList.add(t);
        tv.refresh();
    }

    private void updateAttr(DishesAttributeVO attr) {
        Logger.info("保存菜品属性:" + CommonUtils.reflectString(attr));
        dishesAttributeService.updateById(attr);
    }


    public static class AttributeValueBO {
        String attributeValue;
        OperationButton action;

        public String getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public OperationButton getAction() {
            return action;
        }

        public void setAction(OperationButton action) {
            this.action = action;
        }
    }
}
