package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleComboBox;
import com.xjh.startup.view.base.SimpleGridForm;
import com.xjh.startup.view.model.DishesAttributeBO;

import cn.hutool.core.codec.Base64;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import lombok.Data;

public class DishesEditView extends SimpleGridForm {

    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    List<Runnable> collectData = new ArrayList<>();

    RichText isMain = RichText.create("是").with(Color.RED);
    RichText notMain = RichText.create("否").with(Color.BLACK);

    public DishesEditView(Dishes dishes) {
        double labelWidth = 120;
        Label nameLabel = createLabel("名称:", labelWidth);
        TextField nameInput = createTextField("名称", 300);
        nameInput.setText(dishes.getDishesName());
        addLine(nameLabel, hbox(nameInput));
        collectData.add(() -> dishes.setDishesName(nameInput.getText()));

        Label priceLabel = createLabel("价格:", labelWidth);
        TextField priceInput = createTextField("价格");
        priceInput.setText(new Money(dishes.getDishesPrice()).toString());
        Label priceTail = new Label("元");
        priceTail.setAlignment(Pos.CENTER_LEFT);
        addLine(priceLabel, hbox(priceInput, priceTail));
        collectData.add(() -> dishes.setDishesPrice(CommonUtils.parseDouble(priceInput.getText(), 0D)));

        Label stockLabel = createLabel("库存:", labelWidth);
        TextField stockInput = createTextField("库存");
        stockInput.setText(CommonUtils.orElse(dishes.getDishesStock(), 0).toString());
        ComboBox<String> stockTypeSelector = new SimpleComboBox<>(
                Lists.newArrayList("有限库存", "无限库存"),
                Function.identity(),
                selected -> stockInput.setDisable(CommonUtils.eq(selected, "无限库存"))
        );
        stockTypeSelector.getSelectionModel().select("无限库存");
        addLine(stockLabel, hbox(stockTypeSelector, stockInput));
        collectData.add(() -> {
            if (CommonUtils.eq(stockTypeSelector.getSelectionModel().getSelectedItem(), "无限库存")) {
                dishes.setDishesStock(-1);
            } else {
                dishes.setDishesStock(CommonUtils.parseInt(stockInput.getText(), 0));
            }
        });

        Label unitLabel = createLabel("库存单位:", labelWidth);
        TextField unitInput = createTextField("库存单位");
        unitInput.setText(dishes.getDishesUnitName());
        addLine(unitLabel, hbox(unitInput));
        collectData.add(() -> dishes.setDishesUnitName(unitInput.getText()));

        Label dishesTypeLabel = createLabel("菜品类型:", labelWidth);
        ComboBox<DishesType> dishesTypeInput = new SimpleComboBox<>(
                dishesTypeService.loadAllTypes(),
                DishesType::getTypeName,
                null
        );
        dishesTypeInput.getSelectionModel().selectFirst();
        Label printSnoLabel = new Label("打印序列:");
        printSnoLabel.setPadding(new Insets(0, 5, 0, 100));
        TextField printSnoInput = new TextField();
        printSnoInput.setText(dishes.getPrintSortby() + "");
        printSnoInput.setPrefWidth(60);
        addLine(dishesTypeLabel, hbox(dishesTypeInput, printSnoLabel, printSnoInput));
        collectData.add(() -> {
            dishes.setDishesTypeId(dishesTypeInput.getSelectionModel().getSelectedItem().getTypeId());
            dishes.setPrintSortby(CommonUtils.parseInt(printSnoInput.getText(), 0));
        });

        Label descLabel = createLabel("菜品描述:", labelWidth);
        TextArea descInput = new TextArea();
        descInput.setText(dishes.getDishesDescription());
        descInput.setPrefHeight(100);
        addLine(descLabel, descInput);
        collectData.add(() -> dishes.setDishesDescription(descInput.getText()));

        ObservableList<ImgBO> imgItems = FXCollections.observableArrayList();
        Label imgLabel = createLabel("菜品图片:", labelWidth);
        TableView<ImgBO> imgTV = new TableView<>();
        imgTV.setPrefWidth(600);
        imgTV.setPrefHeight(200);
        imgTV.getColumns().addAll(
                newCol("序号", "sno", 60),
                newCol("图片", "img", 150),
                newCol("主图", "isMain", 60),
                newCol("操作", "operations", 0)
        );
        imgTV.setItems(imgItems);
        addLine(imgLabel, imgTV);
        collectData.add(() -> {
            List<DishesImgVO> imgs = imgItems.stream().map(it -> {
                DishesImgVO v = new DishesImgVO();
                v.setIsMain("是".equals(it.getIsMain()));
                v.setImageSrc(it.getImg().getImgUrl());
                return v;
            }).collect(Collectors.toList());
            dishes.setDishesImgs(Base64.encode(JSON.toJSONString(imgs)));
        });
        CommonUtils.forEach(ImageHelper.resolveImgs(dishes.getDishesImgs()), it -> {
            ImgBO bo = new ImgBO();
            bo.setSno(imgItems.size() + 1);
            bo.setImg(new ImageSrc(it.getImageSrc(), 100, 60));
            bo.getOperations().add(new OperationButton("删除", cv -> {
                imgItems.remove(bo);
                imgTV.refresh();
            }));
            bo.getOperations().add(new OperationButton("设为主图", () -> {
                for (ImgBO b : imgItems) {
                    b.getIsMain().set(b == bo ? isMain : notMain);
                }
            }));
            if (it.getIsMain() != null && it.getIsMain()) {
                bo.getIsMain().set(isMain);
            } else {
                bo.getIsMain().set(notMain);
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
                    Logger.info("拷贝:" + file.toPath() + " >> " + toFile.toPath());
                    Files.copy(file.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    ImgBO bo = new ImgBO();
                    bo.setSno(imgItems.size() + 1);
                    bo.setImg(new ImageSrc(toUrl, 100, 60));
                    bo.getIsMain().set(notMain);
                    bo.getOperations().add(new OperationButton("删除", cv -> {
                        imgItems.remove(bo);
                        imgTV.refresh();
                    }));
                    bo.getOperations().add(new OperationButton("设为主图", () -> {
                        for (ImgBO b : imgItems) {
                            b.getIsMain().set(b == bo ? isMain : notMain);
                        }
                    }));
                    imgItems.add(bo);
                    imgTV.refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlertBuilder.ERROR("上传文件失败:" + ex.getMessage());
                }
            }
        });
        addLine((Node) null, uploadFile);

        addLine(createLabel("菜品属性:", labelWidth), new Label("公共属性(不能修改)"));

        ObservableList<DishesAttributeVO> selectedPubAttrItems = FXCollections.observableArrayList();
        ObservableList<DishesAttributeValueVO> selectedPubAttrValueItems = FXCollections.observableArrayList();
        ObjectProperty<DishesAttributeVO> selectedPubAttr = new SimpleObjectProperty<>();
        VBox pubAttrOperations = new VBox();
        pubAttrOperations.setSpacing(10);
        Button addPubAttrBtn = new Button("添加公共属性");
        addPubAttrBtn.setOnAction(evt -> {
            // 弹出公共属性选择框
            List<DishesAttributeVO> allAttrList = dishesAttributeService.selectAll();
            ObjectProperty<DishesAttributeBO> selectedAttr = new SimpleObjectProperty<>();
            Window window = this.getScene().getWindow();
            ModelWindow w = new ModelWindow(window);
            HBox box = new HBox();
            box.setSpacing(20);
            box.setPadding(new Insets(10, 10, 10, 10));
            TableView<DishesAttributeBO> attrsTV = new TableView<>();
            attrsTV.setPrefWidth(window.getWidth() * 0.9 * 0.6);
            attrsTV.getColumns().addAll(
                    newCol("属性ID", "dishesAttributeId", 60),
                    newCol("属性名称", "dishesAttributeName", 200),
                    newCol("属性名称", "operations", 0)
            );
            attrsTV.setItems(FXCollections.observableArrayList(allAttrList.stream().map(it -> {
                DishesAttributeBO bo = new DishesAttributeBO();
                bo.setAttachment(it);
                bo.setDishesAttributeId(it.getDishesAttributeId());
                bo.setDishesAttributeName(it.getDishesAttributeName());
                if (!selectedPubAttrItems.stream()
                        .map(DishesAttributeVO::getDishesAttributeId).collect(Collectors.toList())
                        .contains(it.getDishesAttributeId())) {
                    bo.getOperations().add(new OperationButton("添加", () -> {
                        selectedPubAttrItems.add(it);
                        w.hide();
                    }));
                }
                return bo;
            }).collect(Collectors.toList())));
            attrsTV.getSelectionModel().selectedItemProperty().addListener((x, o, n) -> {
                selectedAttr.set(n);
            });
            attrsTV.refresh();
            TableView<DishesAttributeValueVO> attrsValTV = new TableView<>();
            attrsValTV.setPrefWidth(window.getWidth() * 0.9 * 0.4);
            attrsValTV.getColumns().addAll(
                    newCol("属性值", "attributeValue", 200)
            );
            selectedAttr.addListener((obs, ov, nv) -> {
                attrsValTV.getItems().clear();
                if (nv != null && nv.getAttachment() != null && nv.getAttachment().getAllAttributeValues() != null) {
                    attrsValTV.setItems(FXCollections.observableArrayList(nv.getAttachment().getAllAttributeValues()));
                }
                attrsValTV.refresh();
            });
            box.getChildren().addAll(attrsTV, attrsValTV);
            w.setScene(new Scene(box));
            w.showAndWait();
        });
        Button removePubAttrBtn = new Button("移除公共属性");
        pubAttrOperations.getChildren().addAll(addPubAttrBtn, removePubAttrBtn);
        HBox pubAttrInput = new HBox();
        pubAttrInput.setSpacing(10);
        TableView<DishesAttributeVO> pubAttrTV = new TableView<>();
        pubAttrTV.setPrefWidth(600);
        pubAttrTV.setPrefHeight(150);
        pubAttrTV.getColumns().addAll(
                newCol("序号", "dishesAttributeId", 100),
                newCol("名称", "dishesAttributeName", 200)
        );
        pubAttrTV.setItems(selectedPubAttrItems);
        pubAttrTV.getSelectionModel().selectedItemProperty().addListener((x, o, n) -> {
            selectedPubAttr.set(n);
        });

        TableView<DishesAttributeValueVO> pubAttrValTV = new TableView<>();
        pubAttrValTV.setPrefWidth(200);
        pubAttrValTV.setPrefHeight(150);
        pubAttrValTV.setItems(selectedPubAttrValueItems);
        pubAttrValTV.getColumns().addAll(
                newCol("属性值名称", "attributeValue", 200)
        );
        selectedPubAttr.addListener((obs, old, _new) -> {
            pubAttrValTV.getItems().clear();
            if (_new != null && _new.getAllAttributeValues() != null) {
                pubAttrValTV.getItems().addAll(
                        FXCollections.observableArrayList(_new.getAllAttributeValues()));
            }
            pubAttrValTV.refresh();
        });

        pubAttrInput.getChildren().addAll(pubAttrTV, pubAttrValTV);
        addLine(pubAttrOperations, pubAttrInput);
        collectData.add(() -> {
            List<String> pubAttrIds = CommonUtils.collect(pubAttrTV.getItems(), it -> it.getDishesAttributeId().toString());
            dishes.setDishesPublicAttribute(String.join(",", pubAttrIds));
        });

        addLine("", new Label("私有属性(可修改)"));

        ObservableList<DishesAttributeBO> priAttrList = FXCollections.observableArrayList();
        VBox priAttrOperations = new VBox();
        priAttrOperations.setSpacing(10);
        Button addPriAttrBtn = new Button("添加私有属性");
        addPriAttrBtn.setOnAction(evt -> {
            Window w = this.getScene().getWindow();
            ModelWindow modelDialog = new ModelWindow(w);
            modelDialog.setWidth(600);
            modelDialog.setWidth(400);
            DishesAttributeEditView view = new DishesAttributeEditView(new DishesAttributeVO(), it -> {
                DishesAttributeBO bo = new DishesAttributeBO();
                bo.setAttachment(it);
                bo.setDishesAttributeId(1);
                bo.setDishesAttributeName(it.getDishesAttributeName());
                bo.setDishesAttributeMarkInfo(it.getDishesAttributeMarkInfo());
                priAttrList.add(bo);
                modelDialog.close();
            });
            modelDialog.setScene(new Scene(view));
            modelDialog.showAndWait();
        });
        Button removePriAttrBtn = new Button("移除私有属性");
        priAttrOperations.getChildren().addAll(addPriAttrBtn, removePriAttrBtn);
        HBox priAttrInput = new HBox();
        priAttrInput.setSpacing(10);
        TableView<DishesAttributeBO> priAttrTV = new TableView<>();
        priAttrTV.setPrefWidth(600);
        priAttrTV.setPrefHeight(150);
        priAttrTV.setItems(priAttrList);
        priAttrTV.getColumns().addAll(
                newCol("ID", "dishesAttributeId", 60),
                newCol("名称", "dishesAttributeName", 150),
                newCol("备注", "dishesAttributeMarkInfo", 150),
                newCol("操作", "operations", 200)
        );
        priAttrTV.setItems(priAttrList);

        TableView<Dishes> priAttrValTV = new TableView<>();
        priAttrValTV.setPrefWidth(200);
        priAttrValTV.setPrefHeight(150);
        priAttrValTV.getColumns().addAll(
                newCol("属性值名称", "attributeValue", 200)
        );
        priAttrInput.getChildren().addAll(priAttrTV, priAttrValTV);
        addLine(priAttrOperations, priAttrInput);
        collectData.add(() -> {
            dishes.setDishesPrivateAttribute(Base64.encode(JSON.toJSONString(new ArrayList<>())));
        });

        Button save = new Button("保 存");
        save.setOnAction(evt -> {
            CommonUtils.safeRun(collectData);
            System.out.println(JSON.toJSONString(dishes, true));
            Result<Integer> rs = dishesService.save(dishes);
            if(rs.isSuccess()){
                AlertBuilder.INFO("保存成功");
            }else {
                AlertBuilder.ERROR("保存失败," + rs.getMsg());
            }
        });
        addLine((Node) null, save);
    }

    @Data
    public static class ImgBO {
        int sno;
        ImageSrc img;
        ObjectProperty<RichText> isMain = new SimpleObjectProperty<>();
        Operations operations = new Operations();
    }
}
