package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.xjh.common.utils.*;
import com.xjh.common.utils.cellvalue.*;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.service.domain.DishesPackageService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleComboBox;
import com.xjh.startup.view.base.SimpleGridForm;
import com.xjh.common.model.IntStringPair;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.Data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class PackageDishesEditView extends SimpleGridForm {
    DishesPackageService dishesPackageService = GuiceContainer.getInstance(DishesPackageService.class);
    DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    DishesPackage dishesPackage;
    List<Runnable> collectData = new ArrayList<>();

    RichText IS_MAIN = RichText.create("是").with(Color.RED);
    RichText NOT_MAIN = RichText.create("否").with(Color.BLACK);

    public PackageDishesEditView(DishesPackage param) {
        dishesPackage = dishesPackageService.getByDishesPackageId(param.getDishesPackageId());
        if (dishesPackage == null) {
            dishesPackage = new DishesPackage();
        }
        double labelWidth = 120;
        Label nameLabel = createLabel("套餐名称:", labelWidth);
        TextField nameInput = createTextField("套餐名称", 300);
        nameInput.setText(dishesPackage.getDishesPackageName());
        addLine(nameLabel, hbox(nameInput));
        collectData.add(() -> dishesPackage.setDishesPackageName(nameInput.getText()));

        Label priceLabel = createLabel("套餐价格:", labelWidth);
        TextField priceInput = createTextField("套餐价格");
        priceInput.setText(new Money(dishesPackage.getDishesPackagePrice()).toString());
        Label priceTail = new Label("元");
        priceTail.setAlignment(Pos.CENTER_LEFT);
        addLine(priceLabel, hbox(priceInput, priceTail));
        collectData.add(() -> dishesPackage.setDishesPackagePrice(CommonUtils.parseDouble(priceInput.getText(), 0D)));

        Label dishesTypeLabel = createLabel("报餐类型:", labelWidth);
        SimpleComboBox<DishesType> dishesTypeInput = new SimpleComboBox<>(
                dishesTypeService.loadAllTypesValid(),
                DishesType::getTypeName,
                null
        );
        dishesTypeInput.select(it -> CommonUtils.eq(it.getTypeId(), dishesPackage.getDishesPackageType()));
        addLine(dishesTypeLabel, hbox(dishesTypeInput));
        collectData.add(() -> {
            dishesPackage.setDishesPackageType(dishesTypeInput.getSelectionModel().getSelectedItem().getTypeId());
        });

        Label printSnoLabel = new Label("排序:");
        printSnoLabel.setPadding(new Insets(0, 5, 0, 100));
        TextField sortByInput = new TextField();
        sortByInput.setText(dishesPackage.getSortby() + "");
        sortByInput.setPrefWidth(60);
        addLine(printSnoLabel, hbox(sortByInput));
        collectData.add(() -> {
            dishesPackage.setSortby(CommonUtils.parseInt(sortByInput.getText(), 0));
        });

        Label statusLabel = new Label("状态");
        statusLabel.setPadding(new Insets(0, 5, 0, 100));
        ObservableList<IntStringPair> statusOptions = FXCollections.observableArrayList(
                new IntStringPair(1, "已启用"),
                new IntStringPair(0, "已停用")
        );
        ComboBox<IntStringPair> statusInput = new ComboBox<>(statusOptions);
        IntStringPair.select(statusInput, dishesPackage.getDishesPackageStatus(), 1);
        addLine(statusLabel, hbox(statusInput));
        collectData.add(() -> {
            IntStringPair s = statusInput.getSelectionModel().getSelectedItem();
            if (s != null) {
                dishesPackage.setDishesPackageStatus(s.getKey());
            }
        });

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
            dishesPackage.setDishesPackageImg(Base64.encode(JSON.toJSONString(imgs)));
        });
        resolveDishesImg(imgTV, dishesPackage, imgItems);
        imgTV.refresh();

        Button uploadFile = new Button("上传文件");
        uploadFile.setOnMouseClicked(evt -> openFileSelector(imgTV, imgItems));
        addLine((Node) null, uploadFile);

        /* ************************************************************** *\
         *    保存数据
        \* ************************************************************** */
        Button save = new Button("保 存");
        save.setOnAction(evt -> {
            CommonUtils.safeRun(collectData);
            System.out.println(JSON.toJSONString(dishesPackage, true));
            Result<Integer> rs = dishesPackageService.save(dishesPackage);
            if (rs.isSuccess()) {
                AlertBuilder.INFO("保存成功");
                this.getScene().getWindow().hide();
            } else {
                AlertBuilder.ERROR("保存失败," + rs.getMsg());
            }
        });
        addLine((Node) null, save);
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
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertBuilder.ERROR("上传文件失败:" + ex.getMessage());
            }
        }
    }

    private void resolveDishesImg(TableView<ImgBO> imgTV, DishesPackage dishesPackage,
            ObservableList<ImgBO> imgItems) {
        CommonUtils.forEach(ImageHelper.resolveImgs(dishesPackage.getDishesPackageImg()), it -> {
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
