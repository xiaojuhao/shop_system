package com.xjh.startup.view;

import com.xjh.common.enumeration.EnumChoseType;
import com.xjh.common.model.DishesChoiceItemBO;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.JSONBuilder;
import com.xjh.common.valueobject.CartItemVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackageDishes;
import com.xjh.dao.dataobject.DishesPackageType;
import com.xjh.dao.mapper.DishesPackageDishesDAO;
import com.xjh.dao.mapper.DishesPackageTypeDAO;
import com.xjh.service.domain.DishesService;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PackageDishesChoiceView extends Group {
    DishesPackageTypeDAO typeDAO = GuiceContainer.getInstance(DishesPackageTypeDAO.class);
    DishesPackageDishesDAO packageDishesDAO = GuiceContainer.getInstance(DishesPackageDishesDAO.class);
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    GridPane grid = new GridPane();

    public PackageDishesChoiceView(DishesChoiceItemBO bo, Consumer<CartItemVO> addCartItemCb, AtomicInteger lines) {
        Integer packageId = bo.getDishesPackageId();

        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 0, 0, 10));
        this.getChildren().add(grid);
        List<Supplier<List<DishesPackageDishes>>> dishesSources = new ArrayList<>();
        List<Supplier<String>> dishesCheckers = new ArrayList<>();
        Supplier<Integer> addNumSp;
        int row = -1;
        //
        {
            row++;
            Label label = new Label("套餐名:");
            label.setMinWidth(80);
            Label name = new Label(bo.getDishesName());
            grid.add(label, 0, row);
            grid.add(name, 1, row);

            lines.incrementAndGet();
        }

        {
            // 查询套餐包含的菜品类型
            List<DishesPackageType> types = typeDAO.getByDishesPackageId(packageId);
            for (DishesPackageType type : types) {
                row++;
                List<DishesPackageDishes> packageDishes = packageDishesDAO.getByDishesPackageTypeId(
                        packageId, type.getDishesPackageTypeId());
                // 菜品类型包含的菜品个数
                int dishesSize = packageDishes.size();
                // 选择个数
                Integer typeChooseNums = type.getChooseNums();

                EnumChoseType chose = EnumChoseType.of(type.getIfRequired());
                StringBuilder name = new StringBuilder(type.getDishesPackageTypeName());
                if (chose == EnumChoseType.ALL) {
                    name.append("(必选):");
                } else if (chose == EnumChoseType.MAX) {
                    name.append("(最多选").append(typeChooseNums).append("个):");
                } else {
                    name.append("(").append(dishesSize).append("选").append(typeChooseNums).append("):");
                }
                grid.add(new Label(name.toString()), 0, row);
                FlowPane choices = new FlowPane();
                choices.setVgap(5);
                choices.setHgap(5);
                List<Supplier<DishesPackageDishes>> typeSelected = new ArrayList<>();
                int dishesIndex = 0;
                for (DishesPackageDishes pd : packageDishes) {
                    dishesIndex ++;
                    if(dishesIndex % 3 == 2){
                        lines.incrementAndGet();
                    }
                    Dishes dishes = dishesService.getById(pd.getDishesId());
                    if (dishes == null) {
                        continue;
                    }
                    CheckBox cb = new CheckBox(dishes.getDishesName());
                    if (chose == EnumChoseType.ALL) {
                        cb.setSelected(true);
                        // 必选时，不可取消
                        cb.selectedProperty().addListener((x, y, z) -> cb.setSelected(true));
                    }
                    choices.getChildren().add(cb);
                    typeSelected.add(() -> cb.isSelected() ? pd : null);


                }
                dishesSources.add(() -> CommonUtils.collect(typeSelected, Supplier::get));
                if (chose == EnumChoseType.ALL) {
                    dishesCheckers.add(() -> {
                        if (CommonUtils.collect(typeSelected, Supplier::get).size() != packageDishes.size()) {
                            return type.getDishesPackageTypeName() + " 未选中";
                        }
                        return null;
                    });
                } else if (chose == EnumChoseType.MAX) {
                    dishesCheckers.add(() -> {
                        int selectedSize = CommonUtils.collect(typeSelected, Supplier::get).size();
                        if (selectedSize > typeChooseNums) {
                            return type.getDishesPackageTypeName() + " 最多可选" + typeChooseNums + "个,已选择" + selectedSize + "个";
                        }
                        return null;
                    });
                } else {
                    dishesCheckers.add(() -> {
                        int selectedSize = CommonUtils.collect(typeSelected, Supplier::get).size();
                        if (selectedSize != typeChooseNums) {
                            return type.getDishesPackageTypeName() + " 应选" + typeChooseNums + "个,已选择" + selectedSize + "个";
                        }
                        return null;
                    });
                }
                grid.add(choices, 1, row);

                lines.incrementAndGet();
            }
        }
        //
        {
            row++;
            Label label = new Label("数量:");
            TextField numInput = new TextField();
            numInput.setText("1");
            numInput.setMaxWidth(100);
            numInput.textProperty().addListener((x, ov, nv) -> {
                numInput.setText(CommonUtils.parseInt(nv, 0).toString());
            });
            addNumSp = () -> CommonUtils.parseInt(numInput.getText(), 1);
            grid.add(label, 0, row);
            grid.add(numInput, 1, row);

            lines.incrementAndGet();
        }
        //
        {
            row++;
            Button saveBtn = new Button("加入购物车");
            VBox box = new VBox();
            box.getChildren().add(saveBtn);
            box.setAlignment(Pos.CENTER);
            grid.add(box, 0, row, 2, 1);

            lines.incrementAndGet();

            saveBtn.setOnMouseClicked(evt -> {
                for (Supplier<String> checker : dishesCheckers) {
                    String errmsg = checker.get();
                    if (errmsg != null) {
                        AlertBuilder.ERROR(errmsg);
                        return;
                    }
                }
                if (addNumSp.get() <= 0) {
                    AlertBuilder.ERROR("请输入添加数量");
                    return;
                }
                List<DishesPackageDishes> selectedDishes = new ArrayList<>();
                CommonUtils.forEach(dishesSources, it -> CommonUtils.addList(selectedDishes, it.get()));
                CartItemVO cartItem = new CartItemVO();
                if (bo.getIfPackage() == 1) {
                    cartItem.setDishesId(packageId);
                } else {
                    cartItem.setDishesId(bo.getDishesId());
                }
                cartItem.setIfDishesPackage(2);
                cartItem.setDishesPriceId(0);
                cartItem.setNums(addNumSp.get());
                cartItem.setPackagedishes(CommonUtils.collect(selectedDishes, JSONBuilder::toJSON));

                addCartItemCb.accept(cartItem);

                this.getScene().getWindow().hide();

            });
        }
    }
}
