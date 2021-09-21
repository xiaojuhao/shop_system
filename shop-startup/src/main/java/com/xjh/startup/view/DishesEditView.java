package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.startup.view.base.SimpleGridForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DishesEditView extends SimpleGridForm {
    public DishesEditView(Dishes dishes) {
        double labelWidth = 200;
        Label nameLabel = createLabel("名称:", labelWidth);
        TextField nameInput = createTextField("名称", 300);
        addLine(nameLabel, hbox(nameInput));

        Label priceLabel = createLabel("价格:", labelWidth);
        TextField priceInput = createTextField("价格");
        Label priceTail = new Label("元");
        priceTail.setAlignment(Pos.CENTER_LEFT);
        addLine(priceLabel, hbox(priceInput, priceTail));

        Label stockLabel = createLabel("库存:", labelWidth);
        TextField stockInput = createTextField("库存");
        ComboBox<String> stockTypeSelector = new ComboBox<>();
        stockTypeSelector.setItems(FXCollections.observableArrayList("有限库存", "无限库存"));
        stockTypeSelector.valueProperty().addListener((_obs, _old, _new) -> {
            stockInput.setDisable(CommonUtils.eq(_new, "无限库存"));
        });
        stockTypeSelector.getSelectionModel().select("无限库存");

        addLine(stockLabel, hbox(stockTypeSelector, stockInput));

        Label unitLabel = createLabel("库存单位:", labelWidth);
        TextField unitInput = createTextField("库存单位");
        unitInput.setText(dishes.getDishesUnitName());
        addLine(unitLabel, hbox(unitInput));

        Label descLabel = createLabel("菜品描述:", labelWidth);
        TextArea descInput = new TextArea();
        descInput.setMinHeight(100);
        addLine(descLabel, descInput);

        ObservableList<ImgBO> imgItems = FXCollections.observableArrayList();
        Label imgLabel = createLabel("菜品图片:", labelWidth);
        TableView<ImgBO> imgTV = new TableView<>();
        imgTV.setPrefWidth(500);
        imgTV.getColumns().addAll(
                newCol("序号", "sno", 60),
                newCol("图片", "img", 150),
                newCol("操作", "operation", 0)
        );
        imgTV.setItems(imgItems);
        addLine(imgLabel, imgTV);
        CommonUtils.forEach(ImageHelper.resolveImgs(dishes.getDishesImgs()), it -> {
            ImgBO bo = new ImgBO();
            bo.setSno(imgItems.size() + 1);
            ImageSrc img = new ImageSrc(it.getImageSrc());
            img.setWidth(100);
            img.setHeight(60);
            bo.setImg(img);
            bo.setOperation(new OperationButton("删除", () -> {

            }));
            imgItems.add(bo);
        });
        imgTV.refresh();

        Label pubAttrLabel = createLabel("公共属性:", labelWidth);
        TextField pubAttrInput = createTextField("公共属性");
        addLine(pubAttrLabel, pubAttrInput);

        Label priAttrLabel = createLabel("私有属性:", labelWidth);
        TextField priAttrInput = createTextField("私有属性");
        addLine(priAttrLabel, priAttrInput);
    }

    public static class ImgBO {
        int sno;
        ImageSrc img;
        OperationButton operation;

        public int getSno() {
            return sno;
        }

        public void setSno(int sno) {
            this.sno = sno;
        }

        public ImageSrc getImg() {
            return img;
        }

        public void setImg(ImageSrc img) {
            this.img = img;
        }

        public OperationButton getOperation() {
            return operation;
        }

        public void setOperation(OperationButton operation) {
            this.operation = operation;
        }
    }
}
