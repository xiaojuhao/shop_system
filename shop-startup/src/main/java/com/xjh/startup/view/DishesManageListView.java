package com.xjh.startup.view;


import java.util.List;
import java.util.stream.Collectors;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.TableViewUtils;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.valueobject.PageCond;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class DishesManageListView extends SimpleForm implements Initializable {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);

    ObjectProperty<Condition> cond = new SimpleObjectProperty<>(new Condition());
    ObservableList<BO> items = FXCollections.observableArrayList();
    TableView<BO> tableView = new TableView<>();

    @Override
    public void initialize() {
        buildCond();
        buildContent();
        buildFoot();
        loadData();
    }

    private void loadData() {
        PageCond page = new PageCond();
        page.setPageNo(cond.get().getPageNo());
        page.setPageSize(cond.get().getPageSize());
        List<Dishes> list = dishesService.pageQuery(new Dishes(), page);
        Platform.runLater(() -> {
            items.clear();
            items.addAll(list.stream().map(it -> {
                BO bo = new BO();
                bo.setDishesId(it.getDishesId());
                bo.setDishesName(it.getDishesName());
                bo.setDishesPrice(new Money(it.getDishesPrice()));
                if (it.getDishesStock() != null && it.getDishesStock() >= 0) {
                    bo.setDishesStock(it.getDishesStock().toString());
                } else {
                    bo.setDishesStock("不限");
                }
                if (it.getDishesStatus() != null && it.getDishesStatus() == 1) {
                    bo.setDishesStatus("上架");
                } else {
                    bo.setDishesStatus("下架");
                }
                Operations operations = new Operations();
                OperationButton edit = new OperationButton("编辑", () -> {
                });
                OperationButton onoff = new OperationButton("上下架", () -> {
                });
                OperationButton del = new OperationButton("删除", () -> {
                });
                operations.add(edit);
                operations.add(onoff);
                operations.add(del);
                bo.setOperations(operations);
                return bo;
            }).collect(Collectors.toList()));
            tableView.refresh();
        });
    }

    private void buildCond() {
        // name
        HBox nameLine = new HBox();
        Label nameLabel = new Label("名称:");
        TextField nameInput = new TextField();
        nameLine.getChildren().add(nameLabel);
        nameLine.getChildren().add(nameInput);


        // status
        HBox statusLine = new HBox();
        Label statusLabel = new Label("状态:");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton all = new RadioButton("全部");
        all.setToggleGroup(toggleGroup);
        all.setUserData(-1);

        RadioButton online = new RadioButton("上架");
        online.setToggleGroup(toggleGroup);
        online.setUserData(1);
        online.setSelected(true);

        RadioButton offline = new RadioButton("下架");
        offline.setToggleGroup(toggleGroup);
        offline.setUserData(0);

        statusLine.getChildren().add(statusLabel);
        statusLine.getChildren().add(newLine(online, offline));


        addLine(newLine(nameLine, statusLine));
    }

    private void buildContent() {

        tableView.getColumns().addAll(
                TableViewUtils.newCol("序号", "dishesId", 100),
                TableViewUtils.newCol("名称", "dishesName", 200),
                TableViewUtils.newCol("状态", "dishesStatus", 80),
                TableViewUtils.newCol("价格", "dishesPrice", 100),
                TableViewUtils.newCol("库存", "dishesStock", 100),
                TableViewUtils.newCol("操作", "operations", 200)
        );
        tableView.setItems(items);
        addLine(tableView);
    }

    private void buildFoot() {
        cond.addListener((ob, o, n) -> {
            loadData();
        });
        Button prev = new Button("上一页");
        prev.setOnMouseClicked(e -> {
            Condition c = CommonUtils.deepClone(cond.get(), Condition.class);
            int pageNo = c.getPageNo();
            if (pageNo <= 1) {
                c.setPageNo(1);
            } else {
                c.setPageNo(pageNo - 1);
            }
            cond.set(c);
        });
        Button next = new Button("下一页");
        next.setOnMouseClicked(e -> {
            Condition c = CommonUtils.deepClone(cond.get(), Condition.class);
            c.setPageNo(c.getPageNo() + 1);
            cond.set(c);
        });
        addLine(newLine(prev, next));
    }

    public static class Condition {
        int pageNo = 1;
        int pageSize = 20;
        String name;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class BO {
        Integer dishesId;
        Integer dishesTypeId;
        String dishesName;
        Money dishesPrice;
        String dishesStock;
        String dishesDescription;
        String dishesImgs;
        String dishesUnitName;
        String dishesStatus;
        Long creatTime;
        Integer ifNeedMergePrint;
        Integer ifNeedPrint;
        String validTime;
        Operations operations;

        public Money getDishesPrice() {
            return dishesPrice;
        }

        public void setDishesPrice(Money dishesPrice) {
            this.dishesPrice = dishesPrice;
        }

        public Integer getDishesId() {
            return dishesId;
        }

        public void setDishesId(Integer dishesId) {
            this.dishesId = dishesId;
        }

        public Integer getDishesTypeId() {
            return dishesTypeId;
        }

        public void setDishesTypeId(Integer dishesTypeId) {
            this.dishesTypeId = dishesTypeId;
        }

        public String getDishesName() {
            return dishesName;
        }

        public void setDishesName(String dishesName) {
            this.dishesName = dishesName;
        }

        public String getDishesStock() {
            return dishesStock;
        }

        public void setDishesStock(String dishesStock) {
            this.dishesStock = dishesStock;
        }

        public String getDishesDescription() {
            return dishesDescription;
        }

        public void setDishesDescription(String dishesDescription) {
            this.dishesDescription = dishesDescription;
        }

        public String getDishesImgs() {
            return dishesImgs;
        }

        public void setDishesImgs(String dishesImgs) {
            this.dishesImgs = dishesImgs;
        }

        public String getDishesUnitName() {
            return dishesUnitName;
        }

        public void setDishesUnitName(String dishesUnitName) {
            this.dishesUnitName = dishesUnitName;
        }

        public String getDishesStatus() {
            return dishesStatus;
        }

        public void setDishesStatus(String dishesStatus) {
            this.dishesStatus = dishesStatus;
        }

        public Long getCreatTime() {
            return creatTime;
        }

        public void setCreatTime(Long creatTime) {
            this.creatTime = creatTime;
        }

        public Integer getIfNeedMergePrint() {
            return ifNeedMergePrint;
        }

        public void setIfNeedMergePrint(Integer ifNeedMergePrint) {
            this.ifNeedMergePrint = ifNeedMergePrint;
        }

        public Integer getIfNeedPrint() {
            return ifNeedPrint;
        }

        public void setIfNeedPrint(Integer ifNeedPrint) {
            this.ifNeedPrint = ifNeedPrint;
        }

        public String getValidTime() {
            return validTime;
        }

        public void setValidTime(String validTime) {
            this.validTime = validTime;
        }

        public Operations getOperations() {
            return operations;
        }

        public void setOperations(Operations operations) {
            this.operations = operations;
        }
    }
}
