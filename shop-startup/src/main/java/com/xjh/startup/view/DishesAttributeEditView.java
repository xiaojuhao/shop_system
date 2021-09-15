package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
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
            List<DishesAttributeValueVO> vals = new ArrayList<>();
            CommonUtils.forEach(tv.getItems(), it -> {
                DishesAttributeValueVO v = new DishesAttributeValueVO();
                v.setAttributeValue(it.getAttributeValue());
                vals.add(v);
            });
            data.setAllAttributeValues(vals);
        });
        CommonUtils.forEach(attr.getAllAttributeValues(), v -> {
            AttributeValueBO bo = new AttributeValueBO();
            bo.setAttributeValue(v.getAttributeValue());
            OperationButton op = new OperationButton();
            op.setTitle("删除");
            op.setAction(() -> {
                removeItem(attrList, v.getAttributeValue());
                tv.refresh();
            });
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
            Stage stg = new Stage();
            double width = this.getScene().getWindow().getWidth();
            double height = 200;
            stg.initOwner(this.getScene().getWindow());
            stg.initModality(Modality.WINDOW_MODAL);
            stg.initStyle(StageStyle.DECORATED);
            stg.centerOnScreen();
            stg.setWidth(width);
            stg.setHeight(height);
            stg.setTitle("增 加");
            SmallForm node = new SmallForm();
            TextField textField = new TextField();
            textField.setPrefWidth(width - 100);
            node.addLine(node.newLine(new Label("名称"), textField));
            Button confirm = new Button("确定");
            node.addLine(node.newLine(confirm));
            confirm.setOnAction(e -> {
                String v = textField.getText();
                AttributeValueBO t = new AttributeValueBO();
                t.setAttributeValue(v);
                OperationButton op = new OperationButton();
                op.setTitle("删除");
                op.setAction(() -> {
                    removeItem(attrList, v);
                    tv.refresh();
                });
                t.setAction(op);
                attrList.add(t);
                tv.refresh();
            });
            stg.setScene(new Scene(node));
            stg.showAndWait();
        });
        addLine(newLine(addAttr, update));
    }

    private void removeItem(ObservableList<AttributeValueBO> list, String item){
        list.stream()
                .filter(it -> it.getAttributeValue().equals(item))
                .findFirst().ifPresent(list::remove);
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
