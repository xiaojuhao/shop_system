package com.xjh.startup.view;

import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.utils.TableViewUtils;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import com.xjh.service.domain.DishesAttributeService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleForm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;

public class DishesAttributeManageView extends SimpleForm {
    DishesAttributeService dishesAttributeService = GuiceContainer.getInstance(DishesAttributeService.class);

    TableView<TableItemBO> tableView = new TableView<>();

    public DishesAttributeManageView() {
        this.setSpacing(15);

        addLine(tableView);
        tableView.setPadding(new Insets(5, 0, 0, 5));
        tableView.getColumns().addAll(
                TableViewUtils.newCol("ID", "dishesAttributeId", 100),
                TableViewUtils.newCol("属性名称", "dishesAttributeName", 100),
                TableViewUtils.newCol("备注", "dishesAttributeMarkInfo", 300),
                TableViewUtils.newCol("类型", "isValueRadio", 160)
        );
        tableView.setItems(loadAll());
        tableView.refresh();
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

        }

        Integer dishesAttributeId;
        String dishesAttributeName;
        String dishesAttributeMarkInfo;
        RichText isValueRadio;
        RichText createTime;

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
    }
}
