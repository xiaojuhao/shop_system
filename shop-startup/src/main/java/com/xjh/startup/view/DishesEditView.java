package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xjh.common.model.DishesAttributeBO;
import com.xjh.common.store.OssStore;
import com.xjh.common.utils.*;
import com.xjh.common.utils.cellvalue.*;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.service.domain.ConfigService;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.service.store.ImageHelper;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.base.SimpleComboBox;
import com.xjh.startup.view.base.SimpleGridForm;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import lombok.Data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xjh.common.utils.Const.KEEP_BASE64;
import static com.xjh.service.store.TableViewUtils.newCol;

public class DishesEditView extends SimpleGridForm {

    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    Dishes dishes;
    List<Runnable> collectData = new ArrayList<>();

    RichText IS_MAIN = RichText.create("是").with(Color.RED);
    RichText NOT_MAIN = RichText.create("否").with(Color.BLACK);

    public DishesEditView(Dishes param) {
        dishes = dishesService.getById(param.getDishesId());
        if (dishes == null) {
            dishes = new Dishes();
        }
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
                dishesTypeService.loadAllTypesValid(),
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
        TableView<ImgBO> imgTV = newTableView(600, 200);
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
                v.setIsMain(it.getIsMain().get() == IS_MAIN);
                v.setImageSrc(it.getImg().getImgUrl());
                return v;
            }).collect(Collectors.toList());
            if(KEEP_BASE64){
                dishes.setDishesImgs(Base64.encode(JSON.toJSONString(imgs)));
            }else {
                dishes.setDishesImgs(JSON.toJSONString(imgs));
            }
        });
        resolveDishesImg(imgTV, dishes, imgItems);
        imgTV.refresh();

        Button uploadFile = new Button("上传文件");
        uploadFile.setOnMouseClicked(evt -> openFileSelector(imgTV, imgItems));
        addLine((Node) null, uploadFile);

        /* ************************************************************** *\
         *    菜品公共属性
        \* ************************************************************** */
        addLine(createLabel("菜品属性:", labelWidth), new Label("公共属性(不能修改)"));

        ObservableList<DishesAttributeVO> pubAttrItems = FXCollections.observableArrayList();
        if (CommonUtils.isNotBlank(dishes.getDishesPublicAttribute())) {
            Set<Integer> attrIds = CommonUtils.splitAsSet(dishes.getDishesPublicAttribute(), ",")
                    .stream().map(Integer::parseInt).collect(Collectors.toSet());
            pubAttrItems.addAll(dishesAttributeService.getByAttrIds(attrIds));
        }
        ObservableList<DishesAttributeValueVO> pubAttrValueItems = FXCollections.observableArrayList();
        ObjectProperty<DishesAttributeVO> selectedPubAttr = new SimpleObjectProperty<>();
        VBox pubAttrOperations = new VBox();
        pubAttrOperations.setSpacing(10);
        Button addPubAttrBtn = new Button("添加公共属性");
        addPubAttrBtn.setOnAction(evt -> openAddPubAttrView(pubAttrItems));
        Button removePubAttrBtn = new Button("移除公共属性");
        pubAttrOperations.getChildren().addAll(addPubAttrBtn, removePubAttrBtn);
        HBox pubAttrInput = new HBox();
        pubAttrInput.setSpacing(10);
        TableView<DishesAttributeVO> pubAttrTV = newTableView(600, 150);
        pubAttrTV.getColumns().addAll(
                newCol("序号", "dishesAttributeId", 100),
                newCol("名称", "dishesAttributeName", 200)
        );
        pubAttrTV.setItems(pubAttrItems);
        pubAttrTV.getSelectionModel().selectedItemProperty().addListener((x, o, n) -> {
            selectedPubAttr.set(n);
        });
        // 公共属性值
        TableView<DishesAttributeValueVO> pubAttrValTV = newTableView(200, 150);
        pubAttrValTV.setItems(pubAttrValueItems);
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
            List<String> pubAttrIds = CommonUtils.collect(pubAttrTV.getItems(), it -> it.getDishesAttributeId()+"");
            dishes.setDishesPublicAttribute(String.join(",", pubAttrIds));
        });

        /* ************************************************************** *\
         *    菜品私有属性
        \* ************************************************************** */
        addLine("", new Label("私有属性(可修改)"));

        ObservableList<DishesAttributeVO> priAttrItems = FXCollections.observableArrayList();
        if (CommonUtils.isNotBlank(dishes.getDishesPrivateAttribute())) {
            String s = dishes.getDishesPrivateAttribute();
            if (!s.contains("{") && !s.contains("[")) {
                s = Base64.decodeStr(s);
            }
            priAttrItems.addAll(JSON.parseArray(s, DishesAttributeVO.class));
        }
        ObjectProperty<DishesAttributeVO> selectedPriAttr = new SimpleObjectProperty<>();
        VBox priAttrOperations = new VBox();
        priAttrOperations.setSpacing(10);
        Button addPriAttrBtn = new Button("添加私有属性");
        addPriAttrBtn.setOnAction(evt -> openAddPriAttrView(priAttrItems));
        Button removePriAttrBtn = new Button("移除私有属性");
        priAttrOperations.getChildren().addAll(addPriAttrBtn, removePriAttrBtn);
        HBox priAttrInput = new HBox();
        priAttrInput.setSpacing(10);
        TableView<DishesAttributeVO> priAttrTV = newTableView(600, 150);
        priAttrTV.setItems(priAttrItems);
        priAttrTV.getColumns().addAll(
                newCol("ID", "dishesAttributeId", 60),
                newCol("名称", "dishesAttributeName", 150),
                newCol("备注", "dishesAttributeMarkInfo", 150),
                newCol("操作", "operations", 200)
        );
        priAttrTV.getSelectionModel().selectedItemProperty().addListener((x, o, n) -> {
            selectedPriAttr.set(n);
        });
        // 私有属性值
        TableView<DishesAttributeValueVO> priAttrValTV = newTableView(200, 150);
        priAttrValTV.getColumns().addAll(
                newCol("属性值名称", "attributeValue", 200)
        );
        selectedPriAttr.addListener((obs, old, _new) -> {
            priAttrValTV.getItems().clear();
            if (_new != null && _new.getAllAttributeValues() != null) {
                priAttrValTV.getItems().addAll(
                        FXCollections.observableArrayList(_new.getAllAttributeValues()));
            }
            priAttrValTV.refresh();
        });
        priAttrInput.getChildren().addAll(priAttrTV, priAttrValTV);
        addLine(priAttrOperations, priAttrInput);
        collectData.add(() -> {
            AtomicInteger id = new AtomicInteger(100);
            CommonUtils.forEach(priAttrItems, it -> {
                it.setDishesAttributeId(id.incrementAndGet());
                it.setIsSync(CommonUtils.orElse(it.getIsSync(), false));
                it.setIsValueRadio(CommonUtils.orElse(it.getIsValueRadio(), false));
                it.setCreateTime(DateBuilder.now().mills());
            });
            if(KEEP_BASE64){
                dishes.setDishesPrivateAttribute(Base64.encode(JSON.toJSONString(priAttrItems)));
            }else {
                dishes.setDishesPrivateAttribute(JSON.toJSONString(priAttrItems));
            }
        });

        /* ************************************************************** *\
         *    保存数据
        \* ************************************************************** */
        Button save = new Button("保 存");
        save.setOnAction(evt -> {
            CommonUtils.safeRun(collectData);
            System.out.println(JSON.toJSONString(dishes, true));
            Result<Integer> rs = dishesService.save(dishes);
            if (rs.isSuccess()) {
                AlertBuilder.INFO("保存成功");
            } else {
                AlertBuilder.ERROR("保存失败," + rs.getMsg());
            }
        });
        addLine((Node) null, save);
    }

    private void openAddPriAttrView(ObservableList<DishesAttributeVO> priAttrList) {
        Window w = this.getScene().getWindow();
        ModelWindow modelDialog = new ModelWindow(w);
        modelDialog.setWidth(600);
        modelDialog.setWidth(400);
        DishesAttributeEditView view = new DishesAttributeEditView(new DishesAttributeVO(), it -> {
            priAttrList.add(it);
            modelDialog.close();
        });
        modelDialog.setScene(new Scene(view));
        modelDialog.showAndWait();
    }

    private void openAddPubAttrView(ObservableList<DishesAttributeVO> attrItems) {
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
            if (!attrItems.stream()
                    .map(DishesAttributeVO::getDishesAttributeId).collect(Collectors.toList())
                    .contains(it.getDishesAttributeId())) {
                bo.getOperations().add(new OperationButton("添加", () -> {
                    attrItems.add(it);
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
    }

    private void openFileSelector(TableView<ImgBO> imgTV, ObservableList<ImgBO> imgItems) {
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
                bo.getIsMain().set(NOT_MAIN);
                bo.getOperations().add(new OperationButton("删除", cv -> {
                    imgItems.remove(bo);
                    imgTV.refresh();
                }));
                bo.getOperations().add(new OperationButton("设为主图", () -> {
                    for (ImgBO b : imgItems) {
                        b.getIsMain().set(b == bo ? IS_MAIN : NOT_MAIN);
                    }
                }));
                imgItems.add(bo);
                imgTV.refresh();

                if(CommonUtils.isNotBlank(ConfigService.getOssAccessKeyId())){
                    new Thread(() -> {
                        OssStore ossStore = new OssStore(ConfigService.getOssEndpoint(), ConfigService.getOssAccessKeyId(), ConfigService.getOssAccessKeySecret());
                        ossStore.upload(toFile, "images/" + toUrl);
                        Logger.info("上传OSS文件成功: " + "images/" + toUrl);
                    }).start();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                AlertBuilder.ERROR("上传文件失败:" + ex.getMessage());
            }
        }
    }

    private void resolveDishesImg(TableView<ImgBO> imgTV, Dishes dishes,
                                  ObservableList<ImgBO> imgItems) {
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
                    b.getIsMain().set(b == bo ? IS_MAIN : NOT_MAIN);
                }
            }));
            if (it.getIsMain() != null && it.getIsMain()) {
                bo.getIsMain().set(IS_MAIN);
            } else {
                bo.getIsMain().set(NOT_MAIN);
            }
            imgItems.add(bo);
        });
    }

    private <T> TableView<T> newTableView(double width, double height) {
        TableView<T> tv = new TableView<>();
        tv.setPrefWidth(width);
        tv.setPrefHeight(height);
        return tv;
    }

    @Data
    public static class ImgBO {
        int sno;
        ImageSrc img;
        ObjectProperty<RichText> isMain = new SimpleObjectProperty<>();
        Operations operations = new Operations();
    }
}
