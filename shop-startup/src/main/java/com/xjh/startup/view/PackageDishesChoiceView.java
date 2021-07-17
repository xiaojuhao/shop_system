package com.xjh.startup.view;

import java.util.List;

import com.xjh.common.enumeration.EnumChoseType;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DishesChoiceItemBO;

import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class PackageDishesChoiceView extends Group {
    DishesPackageTypeDAO typeDAO = GuiceContainer.getInstance(DishesPackageTypeDAO.class);
    DishesPackageDishesDAO packageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    GridPane grid = new GridPane();

    public PackageDishesChoiceView(DishesChoiceItemBO bo) {
        grid.setVgap(10);
        grid.setHgap(10);
        this.getChildren().add(grid);
        int row = -1;
        //
        {
            row++;
            Label label = new Label("套餐名:");
            Label name = new Label(bo.getDishesName());
            grid.add(label, 0, row);
            grid.add(name, 1, row);
        }
        {
            List<DishesPackageType> types = typeDAO.getByDishesPackageId(bo.getDishesPackageId());
            for (DishesPackageType type : types) {
                row++;
                List<DishesPackageDishes> packageDishes = packageDishesDAO.getByDishesPackageTypeId(type.getDishesPackageTypeId());
                EnumChoseType chose = EnumChoseType.of(type.getIfRequired());
                StringBuilder name = new StringBuilder(type.getDishesPackageTypeName());
                if (chose == EnumChoseType.ALL) {
                    name.append("(必选):");
                } else if (chose == EnumChoseType.MAX) {
                    name.append("(最多选").append(type.getChooseNums()).append("个):");
                } else {
                    name.append("(").append(packageDishes.size()).append("选").append(type.getChooseNums()).append("):");
                }
                grid.add(new Label(name.toString()), 0, row);
                FlowPane choices = new FlowPane();
                choices.setVgap(5);
                choices.setHgap(5);
                for (DishesPackageDishes pd : packageDishes) {
                    Dishes dishes = dishesService.getById(pd.getDishesId());
                    if (dishes == null) {
                        continue;
                    }
                    CheckBox cb = new CheckBox(dishes.getDishesName());
                    if (chose == EnumChoseType.ALL) {
                        cb.setSelected(true);
                        cb.selectedProperty().addListener((x, y, z) -> {
                            cb.setSelected(true);
                        });
                    }
                    choices.getChildren().add(cb);
                }
                grid.add(choices, 1, row);
            }
        }
        //
        {
            row++;
            Label label = new Label("数量:");
            TextField numInput = new TextField();
            grid.add(label, 0, row);
            grid.add(numInput, 1, row);
        }
    }
}
