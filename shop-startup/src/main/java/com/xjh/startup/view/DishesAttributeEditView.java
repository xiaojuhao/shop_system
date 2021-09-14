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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DishesAttributeEditView extends SmallForm {
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);

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
        collectData.add(()->data.setDishesAttributeName(nameText.getText()));

        ObservableList<String> options = FXCollections.observableArrayList("单选", "复选");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        addPairLine(new Label("属性类型:"), titleWidth, modelSelect, contentWidth);
        collectData.add(() -> data.setIsValueRadio("单选".equals(modelSelect.getSelectionModel().getSelectedItem())));

        TextField markInput = new TextField();
        markInput.setText(attr.getDishesAttributeMarkInfo());
        addPairLine(new Label("属性备注:"), titleWidth, markInput, contentWidth);
        collectData.add(() -> data.setDishesAttributeMarkInfo(markInput.getText()));

        TableView<AttributeValueBO> attrValueTV = new TableView<>();
        attrValueTV.getColumns().addAll(
                newCol("属性值", "attributeValue", 200),
                newCol("操作", "action", 100)
        );
        attrValueTV.setMaxHeight(200);
        collectData.add(() -> {
            List<DishesAttributeValueVO> vals = new ArrayList<>();
            CommonUtils.forEach(attrValueTV.getItems(), it -> {
                DishesAttributeValueVO v = new DishesAttributeValueVO();
                v.setAttributeValue(it.getAttributeValue());
                vals.add(v);
            });
            data.setAllAttributeValues(vals);
        });
        List<AttributeValueBO> valueBOList = new ArrayList<>();
        CommonUtils.forEach(attr.getAllAttributeValues(), v -> {
            AttributeValueBO bo = new AttributeValueBO();
            bo.setAttributeValue(v.getAttributeValue());
            OperationButton op = new OperationButton();
            op.setTitle("删除");
            op.setAction(() -> {
                Predicate<AttributeValueBO> keeped = it-> !it.getAttributeValue().equals(v.getAttributeValue());
                attrValueTV.setItems(FXCollections.observableArrayList(CommonUtils.filter(attrValueTV.getItems(),keeped)));
                attrValueTV.refresh();
                CommonUtils.safeRun(collectData);
                this.updateAttr(data);
            });
            bo.setAction(op);
            valueBOList.add(bo);
        });

        ObservableList<AttributeValueBO> attrValues = FXCollections.observableArrayList(valueBOList);
        attrValueTV.setItems(attrValues);
        addLine(attrValueTV);

        Button update = new Button("保存属性");
        update.setOnAction(e -> {
            CommonUtils.safeRun(collectData);
            this.updateAttr(data);
        });
        Button addAttr = new Button("增加属性");

        addLine(newLine(addAttr, update));
    }

    private void updateAttr(DishesAttributeVO attr){
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
