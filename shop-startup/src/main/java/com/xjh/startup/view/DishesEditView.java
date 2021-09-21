package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.startup.view.base.SimpleGridForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DishesEditView extends SimpleGridForm {
    public DishesEditView(Dishes dishes) {
        double labelWidth = 200;
        Label nameLabel = createLabel("名称:", labelWidth);
        TextField nameInput = createTextField("名称", 300);
        nameInput.setText(dishes.getDishesName());
        addLine(nameLabel, hbox(nameInput));

        Label priceLabel = createLabel("价格:", labelWidth);
        TextField priceInput = createTextField("价格");
        priceInput.setText(new Money(dishes.getDishesPrice()).toString());
        Label priceTail = new Label("元");
        priceTail.setAlignment(Pos.CENTER_LEFT);
        addLine(priceLabel, hbox(priceInput, priceTail));

        Label stockLabel = createLabel("库存:", labelWidth);
        TextField stockInput = createTextField("库存");
        stockInput.setText(CommonUtils.orElse(dishes.getDishesStock(), 0).toString());
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
        descInput.setText(dishes.getDishesDescription());
        descInput.setMinHeight(100);
        addLine(descLabel, descInput);

        ObservableList<ImgBO> imgItems = FXCollections.observableArrayList();
        Label imgLabel = createLabel("菜品图片:", labelWidth);
        TableView<ImgBO> imgTV = new TableView<>();
        imgTV.setPrefWidth(500);
        imgTV.getColumns().addAll(
                newCol("序号", "sno", 60),
                newCol("图片", "img", 150),
                newCol("主图", "isMain", 60),
                newCol("操作", "operations", 0)
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
            bo.getOperations().add(new OperationButton("删除", () -> {

            }));
            bo.getOperations().add(new OperationButton("设为主图", () -> {

            }));
            if (it.getMain() != null && it.getMain()) {
                bo.setIsMain("是");
            } else {
                bo.setIsMain("否");
            }
            imgItems.add(bo);
        });
        imgTV.refresh();

        Button uploadFile = new Button("上传文件");
        uploadFile.setOnMouseClicked(evt -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("选择图片");
            chooser.getExtensionFilters().addAll(
                    new ExtensionFilter("所有文件", "*.*"),
                    new ExtensionFilter("JPG", "*.jpg"),
                    new ExtensionFilter("PNG", "*.png")
            );
            File file = chooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                String toUrl = "db/img/" + CommonUtils.randomStr(8) + ".jpg";
                File toFile = new File(ImageHelper.getImageDir() + toUrl);
                try {
                    if (!toFile.getParentFile().exists()) {
                        toFile.getParentFile().mkdirs();
                    }
                    System.out.println("拷贝:" + file.toPath() + " >> " + toFile.toPath());
                    Files.copy(file.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    ImgBO bo = new ImgBO();
                    bo.setSno(imgItems.size() + 1);
                    ImageSrc img = new ImageSrc(toUrl);
                    img.setWidth(100);
                    img.setHeight(60);
                    bo.setImg(img);
                    bo.getOperations().add(new OperationButton("删除", () -> {

                    }));
                    imgItems.add(bo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlertBuilder.ERROR("上传文件失败:" + ex.getMessage());
                }
            }
        });
        addLine((Node) null, uploadFile);

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
        String isMain;
        Operations operations = new Operations();

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

        public Operations getOperations() {
            return operations;
        }

        public void setOperations(Operations operations) {
            this.operations = operations;
        }

        public String getIsMain() {
            return isMain;
        }

        public void setIsMain(String isMain) {
            this.isMain = isMain;
        }
    }
}
