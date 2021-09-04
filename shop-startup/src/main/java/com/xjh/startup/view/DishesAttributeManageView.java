package com.xjh.startup.view;

import static com.xjh.common.utils.TableViewUtils.newCol;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.common.valueobject.DishesAttributeValueVO;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.MediumForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

public class DishesAttributeManageView extends MediumForm {
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);

    TableView<TableItemBO> attrPane = new TableView<>();
    TableView<DishesAttributeValueVO> attrValuePane = new TableView<>();

    public DishesAttributeManageView() {
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        addLine(buildAttrPane());
        addLine(buildAttrValuePane());
        Button add = new Button("增加属性");
        addLine(add);
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
                newCol("创建时间", "createTime", 160)
        );
        attrPane.setItems(loadAll());
        attrPane.refresh();
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

    public ObservableList<TableItemBO> loadAll() {
        List<DishesAttributeVO> allAttrs = dishesAttributeService.selectAll();
        return FXCollections.observableArrayList(allAttrs.stream()
                .map(TableItemBO::new).collect(Collectors.toList()));
    }

    public static class TableItemBO {
        public TableItemBO(DishesAttributeVO vo) {
            this.dishesAttributeId = vo.getDishesAttributeId();
            this.dishesAttributeName = vo.getDishesAttributeName();
            this.dishesAttributeMarkInfo = vo.getDishesAttributeMarkInfo();
            if (vo.getIsValueRadio() != null && vo.getIsValueRadio()) {
                this.isValueRadio = RichText.create("单选");
            } else {
                this.isValueRadio = RichText.create("多选");
            }
            this.createTime = RichText.create(DateBuilder.base(vo.getCreateTime()).timeStr());
            this.attachment = vo;
        }

        Integer dishesAttributeId;
        String dishesAttributeName;
        String dishesAttributeMarkInfo;
        RichText isValueRadio;
        RichText createTime;
        DishesAttributeVO attachment;

        public Integer getDishesAttributeId() {
            return dishesAttributeId;
        }

        public void setDishesAttributeId(Integer dishesAttributeId) {
            this.dishesAttributeId = dishesAttributeId;
        }

        public String getDishesAttributeName() {
            return dishesAttributeName;
        }

        public void setDishesAttributeName(String dishesAttributeName) {
            this.dishesAttributeName = dishesAttributeName;
        }

        public String getDishesAttributeMarkInfo() {
            return dishesAttributeMarkInfo;
        }

        public void setDishesAttributeMarkInfo(String dishesAttributeMarkInfo) {
            this.dishesAttributeMarkInfo = dishesAttributeMarkInfo;
        }

        public RichText getIsValueRadio() {
            return isValueRadio;
        }

        public void setIsValueRadio(RichText isValueRadio) {
            this.isValueRadio = isValueRadio;
        }

        public RichText getCreateTime() {
            return createTime;
        }

        public void setCreateTime(RichText createTime) {
            this.createTime = createTime;
        }

        public void setAttachment(DishesAttributeVO attachment) {
            this.attachment = attachment;
        }

        public DishesAttributeVO getAttachment() {
            return attachment;
        }
    }
}
