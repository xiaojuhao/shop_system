package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.LargeForm;
import com.xjh.startup.view.base.ModelWindow;
import com.xjh.startup.view.model.DishesAttributeBO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DishesAttributeManageView extends LargeForm implements Initializable {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);

    TableView<DishesAttributeBO> attrPane = new TableView<>();
    TableView<DishesAttributeValueVO> attrValuePane = new TableView<>();

    public DishesAttributeManageView() {
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);
        addLine(buildAttrPane());
        addLine(buildAttrValuePane());
        Button add = new Button("增加属性");
        add.setOnAction(e -> {
            showEditView(new DishesAttributeVO(), this.getScene().getWindow());
        });
        VBox.setMargin(add, new Insets(0,0,0,200));
        addLine(add);
    }

    public void initialize() {
        loadData();
    }

    private void loadData() {
        attrPane.setItems(loadAll(this.getScene().getWindow()));
        attrPane.refresh();
    }

    private ScrollPane buildAttrPane() {
        ScrollPane pane = new ScrollPane();
        pane.setPrefHeight(250);
        pane.setContent(attrPane);
        pane.setFitToWidth(true);
        attrPane.setPadding(new Insets(5, 0, 0, 5));
        attrPane.getColumns().addAll(
                newCol("ID", "dishesAttributeId", 100),
                newCol("属性名称", "dishesAttributeName", 100),
                newCol("备注", "dishesAttributeMarkInfo", 300),
                newCol("类型", "isValueRadio", 160),
                newCol("创建时间", "createTime", 160),
                newCol("操作", "operations", 0)
        );
        attrPane.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
            if (c != null) {
                if (c.getAttachment() != null && c.getAttachment().getAllAttributeValues() != null) {
                    attrValuePane.setItems(
                            FXCollections.observableArrayList(c.getAttachment().getAllAttributeValues()));
                    attrValuePane.refresh();
                }
            }
        });
        return pane;
    }

    private ScrollPane buildAttrValuePane() {
        ScrollPane pane = new ScrollPane();
        pane.setPrefHeight(200);
        pane.setFitToWidth(true);
        attrValuePane.getColumns().addAll(
                newCol("属性值名称", "attributeValue", 100)
        );
        pane.setContent(attrValuePane);
        return pane;
    }

    public ObservableList<DishesAttributeBO> loadAll(Window window) {
        List<DishesAttributeVO> allAttrs = dishesAttributeService.selectAll();
        return FXCollections.observableArrayList(allAttrs.stream()
                .map(it -> toBO(it, window))
                .collect(Collectors.toList()));
    }

    private DishesAttributeBO toBO(DishesAttributeVO vo, Window window) {
        DishesAttributeBO bo = new DishesAttributeBO();
        bo.setDishesAttributeId(vo.getDishesAttributeId());
        bo.setDishesAttributeName(vo.getDishesAttributeName());
        bo.setDishesAttributeMarkInfo(vo.getDishesAttributeMarkInfo());
        if (vo.getIsValueRadio() != null && vo.getIsValueRadio()) {
            bo.setIsValueRadio(RichText.create("单选"));
        } else {
            bo.setIsValueRadio(RichText.create("多选"));
        }
        bo.setCreateTime(RichText.create(DateBuilder.base(vo.getCreateTime()).timeStr()));
        bo.setAttachment(vo);
        OperationButton edit = new OperationButton();
        edit.setTitle("编辑");
        edit.setAction(() -> this.showEditView(vo, window));
        bo.getOperations().add(edit);

        OperationButton del = new OperationButton();
        del.setTitle("删除");
        del.setAction(() -> this.delDishesAttr(vo, window));
        bo.getOperations().add(del);
        return bo;
    }

    public void showEditView(DishesAttributeVO attr, Window window) {
        Stage stg = new ModelWindow(window, "属性信息");
        stg.setWidth(460);
        stg.setHeight(600);
        stg.setScene(new Scene(new DishesAttributeEditView(attr, this::saveDishesAttr)));
        stg.showAndWait();
        loadData();
    }

    public void delDishesAttr(DishesAttributeVO attr, Window window) {
        List<Dishes> dishesList = dishesService.getAllDishes();
        for (Dishes d : dishesList) {
            if (CommonUtils.isBlank(d.getDishesPublicAttribute())) {
                continue;
            }
            Set<String> usedIds = CommonUtils.splitAsSet(d.getDishesPublicAttribute(), ",");
            if (usedIds.contains(attr.getDishesAttributeId() + "")) {
                AlertBuilder.ERROR("您想删除的菜品属性" + attr.getDishesAttributeName() + "已被一些菜品调用，不可删除!");
                return;
            }
        }
        dishesAttributeService.deleteById(attr);
        loadData();
    }

    private void saveDishesAttr(DishesAttributeVO attr) {
        Logger.info("保存菜品属性:" + CommonUtils.reflectString(attr));
        if (attr.getDishesAttributeId() != null) {
            dishesAttributeService.updateById(attr);
        } else {
            dishesAttributeService.addNew(attr);
        }
    }

}
