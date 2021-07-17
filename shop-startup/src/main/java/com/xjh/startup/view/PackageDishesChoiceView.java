package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.alibaba.fastjson.JSON;
import com.xjh.common.enumeration.EnumChoseType;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.JSONBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DishesChoiceItemBO;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class PackageDishesChoiceView extends Group {
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesPackageTypeDAO typeDAO = GuiceContainer.getInstance(DishesPackageTypeDAO.class);
    DishesPackageDishesDAO packageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    GridPane grid = new GridPane();

    public PackageDishesChoiceView(DishesChoiceItemBO bo) {
        grid.setVgap(10);
        grid.setHgap(10);
        this.getChildren().add(grid);
        List<Supplier<DishesPackageDishes>> collectDishesId = new ArrayList<>();
        Supplier<Integer> addNumSp;
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
                    collectDishesId.add(() -> cb.isSelected() ? pd : null);
                }
                grid.add(choices, 1, row);
            }
        }
        //
        {
            row++;
            Label label = new Label("数量:");
            TextField numInput = new TextField();
            numInput.setMaxWidth(100);
            numInput.textProperty().addListener((x, ov, nv) -> {
                numInput.setText(CommonUtils.parseInt(nv, 0).toString());
            });
            addNumSp = () -> CommonUtils.parseInt(numInput.getText(), 1);
            grid.add(label, 0, row);
            grid.add(numInput, 1, row);
        }
        //
        {
            row++;
            Button saveBtn = new Button("加入购物车");
            grid.add(saveBtn, 0, row, 2, 1);
            saveBtn.setOnMouseClicked(evt -> {
                List<DishesPackageDishes> selectedDishes = CommonUtils.map(collectDishesId, Supplier::get);
                CartItemVO cartItem = new CartItemVO();
                if (bo.getIfPackage() == 1) {
                    cartItem.setDishesId(bo.getDishesPackageId());
                } else {
                    cartItem.setDishesId(bo.getDishesId());
                }
                cartItem.setIfDishesPackage(2);
                cartItem.setDishesPriceId(0);
                cartItem.setNums(addNumSp.get());
                cartItem.setPackagedishes(CommonUtils.map(selectedDishes, JSONBuilder::toJSON));
                try {
                    Result<CartVO> addCartRs = cartService.addItem(bo.getDeskId(), cartItem);
                    LogUtils.info("购物车信息:" + JSON.toJSONString(addCartRs));
                    if (addCartRs.isSuccess()) {
                        AlertBuilder.INFO("通知消息", "添加购物车成功");
                    } else {
                        AlertBuilder.ERROR(addCartRs.getMsg());
                    }
                } catch (Exception ex) {
                    AlertBuilder.ERROR("报错消息", "添加购物车异常," + ex.getMessage());
                }

            });
        }
    }
}
