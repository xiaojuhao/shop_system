package com.xjh.startup.view;

import com.xjh.common.model.DishesAttributeValueBO;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SmallForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class DishesAttributeEditView extends SmallForm {
    ObservableList<DishesAttributeValueBO> attrList = FXCollections.observableArrayList();
    DishesAttributeVO data;
    List<Runnable> collectData = new ArrayList<>();

    public DishesAttributeEditView(DishesAttributeVO attr, Consumer<DishesAttributeVO> onSave) {
        super();

        data = CopyUtils.deepClone(attr);
        double titleWidth = 80;
        double contentWidth = 250;

        Label nameLabel = createLabel("属性名:", titleWidth);
        TextField nameText = initTextField(attr.getDishesAttributeName(), contentWidth);
        addPairLine(nameLabel, nameText);
        collectData.add(() -> data.setDishesAttributeName(nameText.getText()));

        Label modelLabel = createLabel("属性类型:", titleWidth);
        ObservableList<String> options = FXCollections.observableArrayList("单选", "复选");
        ComboBox<String> modelSelect = new ComboBox<>(options);
        modelSelect.setPrefWidth(contentWidth);
        modelSelect.getSelectionModel().select(attr.getIsValueRadio() ? "单选" : "复选");
        addPairLine(modelLabel, modelSelect);
        collectData.add(() -> data.setIsValueRadio("单选".equals(modelSelect.getSelectionModel().getSelectedItem())));

        Label markLabel = createLabel("属性备注:", titleWidth);
        TextField markInput = initTextField(attr.getDishesAttributeMarkInfo(), contentWidth);
        addPairLine(markLabel, markInput);
        collectData.add(() -> data.setDishesAttributeMarkInfo(markInput.getText()));

        TableView<DishesAttributeValueBO> tv = new TableView<>();
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
            DishesAttributeValueBO bo = new DishesAttributeValueBO();
            bo.setAttributeValue(v.getAttributeValue());
            OperationButton op = new OperationButton();
            op.setTitle(RichText.create("删除"));
            op.setAction(() -> removeItem(tv, v.getAttributeValue()));
            bo.setAction(op);
            attrList.add(bo);
        });
        addLine(tv);
        tv.refresh();
        Button update = new Button("保 存");
        update.setOnAction(e -> {
            CommonUtils.safeRun(collectData);
            onSave.accept(data);
        });
        Button addAttr = new Button("增加属性值");
        addAttr.setOnAction(evt -> {
            ModelWindow stg = new ModelWindow(this.getScene().getWindow(), "增加属性值");
            stg.setHeight(200);
            SmallForm sform = new SmallForm();
            TextField textField = createTextField("属性值", stg.getWidth() - 100);
            sform.addLine(newCenterLine(createLabel("名称"), textField));
            Button confirm = new Button("确 定");
            sform.addLine(newCenterLine(confirm));
            confirm.setOnAction(e -> {
                addItem(tv, textField.getText());
                stg.hide();
            });
            stg.setScene(new Scene(sform));
            stg.showAndWait();
        });
        addPairLine(addAttr, update);
    }

    private void removeItem(TableView<DishesAttributeValueBO> tv, String item) {
        tv.getItems().stream()
                .filter(it -> it.getAttributeValue().equals(item))
                .findFirst().ifPresent(tv.getItems()::remove);
        tv.refresh();
    }

    private void addItem(TableView<DishesAttributeValueBO> tv, String item) {
        DishesAttributeValueBO t = new DishesAttributeValueBO();
        t.setAttributeValue(item);
        OperationButton op = new OperationButton();
        op.setTitle(RichText.create("删除"));
        op.setAction(() -> removeItem(tv, item));
        t.setAction(op);
        attrList.add(t);
        tv.refresh();
    }


}
