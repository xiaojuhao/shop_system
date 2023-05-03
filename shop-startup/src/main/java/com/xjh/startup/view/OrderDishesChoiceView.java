package com.xjh.startup.view;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xjh.common.enumeration.EnumChoiceAction;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.*;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.mapper.DishesPriceDAO;
import com.xjh.dao.query.DishesPackageQuery;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.domain.model.SendOrderRequest;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.SimpleForm;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DishesChoiceItemBO;
import com.xjh.startup.view.model.DishesQueryCond;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xjh.common.utils.CommonUtils.collect;
import static com.xjh.common.utils.ImageHelper.buildImageView;


public class OrderDishesChoiceView extends VBox {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesPackageDAO dishesPackageDAO = GuiceContainer.getInstance(DishesPackageDAO.class);
    CartService cartService = GuiceContainer.getInstance(CartService.class);
    DishesPriceDAO dishesPriceDAO = GuiceContainer.getInstance(DishesPriceDAO.class);

    private final DeskOrderParam param;
    private final SimpleIntegerProperty cartSize = new SimpleIntegerProperty(0);
    private final ObjectProperty<DishesQueryCond> qryDishesCond = new SimpleObjectProperty<>();

    public OrderDishesChoiceView(DeskOrderParam param, double prefWidth) {
        this.param = param;
        this.getChildren().add(topMenus());
        this.getChildren().add(new Separator(Orientation.HORIZONTAL));
        this.getChildren().add(initDishesView(prefWidth));

        refreshCartSize();
    }

    private HBox topMenus() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10, 0, 0, 0));
        // 套餐、普通菜品选择
        hbox.getChildren().add(new Label("类型:"));
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton commonType = new RadioButton("普通菜品");
        commonType.setToggleGroup(toggleGroup);
        commonType.setUserData(0);
        commonType.setSelected(true);
        hbox.getChildren().add(commonType);
        // 送菜不展示套餐
        if (param.getChoiceAction() != EnumChoiceAction.SEND) {
            RadioButton packageType = new RadioButton("套餐");
            packageType.setToggleGroup(toggleGroup);
            packageType.setUserData(1);
            hbox.getChildren().add(packageType);
        }
        toggleGroup.selectedToggleProperty().addListener((x, ov, nv) -> {
            int selectedType = (int) toggleGroup.getSelectedToggle().getUserData();
            DishesQueryCond newCond = CopyUtils.cloneObj(qryDishesCond.get());
            newCond.setIfPackage(selectedType);
            qryDishesCond.set(newCond);
        });
        // 分割线
        hbox.getChildren().add(new Separator(Orientation.VERTICAL));

        // 按名称搜索
        hbox.getChildren().add(new Label("菜品名称:"));
        TextField dishesNameField = new TextField();
        dishesNameField.focusedProperty().addListener((_obs, _old, _new) ->
                qryDishesCond.get().setDishesName(dishesNameField.getText()));
        hbox.getChildren().add(dishesNameField);

        // 菜品类型
        hbox.getChildren().add(dishesTypeIdSelector());

        // 查询按钮
        Button queryBtn = new Button();
        queryBtn.setText("查 询");
        queryBtn.setOnAction(evt -> {
            DishesQueryCond newCond = CopyUtils.cloneObj(qryDishesCond.get()).newVersion();
            newCond.setPageNo(1);
            qryDishesCond.setValue(newCond);
        });
        hbox.getChildren().add(queryBtn);

        // 分割线
        hbox.getChildren().add(new Separator(Orientation.VERTICAL));

        // 购物车按钮
        Button cartBtn = new Button();
        cartBtn.setText("查看购物车(" + cartSize.get() + ")");
        cartSize.addListener((_this, _old, _new) -> cartBtn.setText("查看购物车(" + _new + ")"));
        cartBtn.setOnMouseClicked(evt -> {
            Stage cartStage = new Stage();
            cartStage.initOwner(this.getScene().getWindow());
            cartStage.initModality(Modality.WINDOW_MODAL);
            cartStage.initStyle(StageStyle.DECORATED);
            cartStage.centerOnScreen();
            cartStage.setWidth(this.getScene().getWindow().getWidth() - 10);
            cartStage.setHeight(this.getScene().getWindow().getHeight() - 100);
            cartStage.setTitle("购物车[桌号:" + param.getDeskName() + "]");
            cartStage.setScene(new Scene(new CartView(param, () -> this.getScene().getWindow().hide())));
            cartStage.showAndWait();
            refreshCartSize();
        });
        hbox.getChildren().add(cartBtn);

        // 下单按钮(赠送时不展示)
        if (param.getChoiceAction() != EnumChoiceAction.SEND) {
            Button placeOrder = new Button("直接下单");
            placeOrder.setOnMouseClicked(evt -> createOrderFromCart());
            hbox.getChildren().add(placeOrder);
        }
        return hbox;
    }

    private VBox initDishesView(double prefWidth) {
        VBox box = new VBox();
        // 菜品列表
        ScrollPane sp = new ScrollPane();
        sp.setMinWidth(prefWidth * 3 + 20);
        FlowPane pane = new FlowPane();
        sp.setContent(pane);
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(prefWidth);
        qryDishesCond.addListener((_this, _old, _new) -> {
            List<DishesChoiceItemBO> bolist = new ArrayList<>();
            if (_new.getIfPackage() == null || _new.getIfPackage() == 0) {
                List<Dishes> dishesList = queryList(_new);
                bolist.addAll(collect(dishesList, this::buildBO));
            } else if (_new.getIfPackage() == 1) {
                List<DishesPackage> packageList = queryPackageList(_new);
                bolist.addAll(collect(packageList, this::buildBO));
            }
            List<VBox> list = collect(bolist, it -> this.buildDishesView(it, prefWidth / 6 - 10));
            Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.getChildren().addAll(list);
            });
        });
        qryDishesCond.set(new DishesQueryCond());
        box.getChildren().add(sp);
        // 分页
        Button nextPage = new Button();
        nextPage.setText("下一页");
        nextPage.setOnMouseClicked(evt -> {
            DishesQueryCond _old = qryDishesCond.get();
            DishesQueryCond _new = CopyUtils.cloneObj(_old);
            _new.setPageNo(_old.getPageNo() + 1);
            qryDishesCond.set(_new);
        });

        Button prevPage = new Button();
        prevPage.setText("上一页");
        prevPage.setOnMouseClicked(evt -> {
            DishesQueryCond _old = qryDishesCond.get();
            DishesQueryCond _new = CopyUtils.cloneObj(_old);
            _new.setPageNo(Math.max(1, _old.getPageNo() - 1));
            qryDishesCond.set(_new);
        });
        HBox page = new HBox();
        page.setSpacing(20);
        page.getChildren().add(prevPage);
        page.getChildren().add(nextPage);
        box.getChildren().add(page);
        return box;
    }

    private List<Dishes> queryList(DishesQueryCond queryCond) {
        PageCond page = new PageCond();
        page.setPageNo(queryCond.getPageNo());
        page.setPageSize(queryCond.getPageSize());
        Dishes cond = new Dishes();
        cond.setDishesTypeId(queryCond.getDishesTypeId());
        cond.setDishesName(queryCond.getDishesName());
        cond.setDishesStatus(1);
        cond.setIfdelete(0);
        return dishesService.pageQuery(cond, page);
    }

    private List<DishesPackage> queryPackageList(DishesQueryCond queryCond) {
        DishesPackageQuery cond = new DishesPackageQuery();
        cond.setName(queryCond.getDishesName());
        cond.setPageNo(1);
        cond.setPageSize(100);
        return dishesPackageDAO.pageQuery(cond);
    }

    private DishesChoiceItemBO buildBO(Dishes dishes) {
        DishesChoiceItemBO bo = new DishesChoiceItemBO();
        bo.setOrderId(param.getOrderId());
        bo.setDeskId(param.getDeskId());
        bo.setDeskName(param.getDeskName());

        bo.setImg(dishesService.getDishesImageUrl(dishes));
        bo.setDishesId(dishes.getDishesId());
        bo.setDishesName(dishes.getDishesName());
        bo.setDishesPrice(dishes.getDishesPrice());
        bo.setIfPackage(0);
        return bo;
    }

    private DishesChoiceItemBO buildBO(DishesPackage dishes) {
        DishesChoiceItemBO bo = new DishesChoiceItemBO();
        List<DishesImgVO> imgs = ImageHelper.resolveImgs(dishes.getDishesPackageImg());
        String img = null;
        if (CommonUtils.isNotEmpty(imgs)) {
            img = imgs.get(0).getImageSrc();
        }
        bo.setOrderId(param.getOrderId());
        bo.setDeskId(param.getDeskId());
        bo.setDeskName(param.getDeskName());
        bo.setImg(img);
        bo.setDishesPackageId(dishes.getDishesPackageId());
        bo.setDishesName(dishes.getDishesPackageName());
        bo.setDishesPrice(dishes.getDishesPackagePrice());
        bo.setIfPackage(1);
        return bo;
    }

    private VBox buildDishesView(DishesChoiceItemBO bo, double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);

        ImageView iv = getImageView(bo.getImg(), width);
        box.setOnMouseClicked(evt -> onDishesClicked(bo)); // 双击菜品
        // 菜品图片
        box.getChildren().add(iv);
        // 菜品名称
        box.getChildren().add(new Label(bo.getDishesName()));
        // 菜品价格
        String price = CommonUtils.formatMoney(bo.getDishesPrice());
        box.getChildren().add(new Label("单价:" + price + "元"));

        return box;
    }

    private void onDishesClicked(DishesChoiceItemBO bo) {
        if (ClickHelper.isDblClick()) {
            Dishes dishes = dishesService.getById(bo.getDishesId());
            if (!DishesService.isInValidTime(dishes)) {
                AlertBuilder.ERROR("当前时间不可售");
                return;
            }
            // 赠送
            if (param.getChoiceAction() == EnumChoiceAction.SEND) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("送菜");
                alert.setHeaderText("确定给用户送菜吗?");
                alert.setContentText(bo.getDishesName());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }
                Result<String> sendRs = sendDishes(bo);
                if (!sendRs.isSuccess()) {
                    AlertBuilder.ERROR(sendRs.getMsg());
                }
                this.getScene().getWindow().hide();
            }
            // 打开套餐
            else if (bo.getIfPackage() == 1) {
                openPackageAddDialog(bo);
            }
            // 加入到购物车
            else {
                openAddDishesDialog(bo);
            }
        }
    }

    private Result<String> sendDishes(DishesChoiceItemBO bo) {
        SendOrderRequest req = new SendOrderRequest();
        CopyUtils.copy(bo, req);
        cartService.createSendOrder(req);
        return Result.success("赠送成功");
    }

    private void openPackageAddDialog(DishesChoiceItemBO bo) {
        Stage stage = new Stage();
        Window owner = this.getScene().getWindow();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(owner.getWidth() / 5 * 3);
        stage.setHeight(owner.getHeight() / 3 * 2);
        stage.setTitle("点菜[桌号:" + param.getDeskName() + "]");
        stage.setScene(new Scene(new PackageDishesChoiceView(bo, this::addCartItem)));
        stage.showAndWait();
    }

    private void openAddDishesDialog(DishesChoiceItemBO bo) {
        Stage stage = new Stage();
        Window owner = this.getScene().getWindow();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(owner.getWidth() / 2);
        stage.setHeight(owner.getHeight() / 2);
        stage.setTitle("点菜[桌号:" + param.getDeskName() + "]");
        List<DishesAttributeVO> dishesAttrs = dishesService.getDishesAttribute(bo.getDishesId());
        SimpleForm form = new SimpleForm();
        form.setSpacing(10);
        form.setPadding(new Insets(10, 0, 0, 0));
        List<Runnable> collectActions = new ArrayList<>();
        for (DishesAttributeVO attr : dishesAttrs) {
            Label name = new Label(attr.getDishesAttributeName() + ":");
            name.setPrefWidth(100);
            HBox optionBox = new HBox();
            optionBox.setSpacing(10);
            // 单选框
            if (attr.getIsValueRadio()) {
                ToggleGroup group = new ToggleGroup();
                List<RadioButton> radios = new ArrayList<>();
                attr.getAllAttributeValues().forEach(v -> {
                    RadioButton radio = new RadioButton(v.getAttributeValue());
                    radio.setToggleGroup(group);
                    radio.setUserData(v.getAttributeValue());
                    radios.add(radio);
                });
                if (radios.size() > 0) {
                    radios.get(0).setSelected(true);
                }
                optionBox.getChildren().addAll(radios);
                collectActions.add(() -> {
                    String udata = (String) group.getSelectedToggle().getUserData();
                    attr.setSelectedAttributeValues(
                            getSelectedAttr(attr.getAllAttributeValues(), Lists.newArrayList(udata)));
                });
            }
            // 复选框
            if (!attr.getIsValueRadio()) {
                List<CheckBox> allCbs = new ArrayList<>();
                attr.getAllAttributeValues().forEach(v -> {
                    CheckBox cb = new CheckBox(v.getAttributeValue());
                    cb.setUserData(v.getAttributeValue());
                    allCbs.add(cb);
                });
                optionBox.getChildren().addAll(allCbs);
                collectActions.add(() -> {
                    List<String> selectedVal = collectValue(allCbs);
                    attr.setSelectedAttributeValues(getSelectedAttr(attr.getAllAttributeValues(), selectedVal));
                });
            }
            HBox line = form.newLine(name, optionBox);
            line.setSpacing(15);
            line.setPadding(new Insets(0, 0, 0, 10));
            form.addLine(line);
        }
        // 价格
        List<DishesPrice> priceList = dishesPriceDAO.queryByDishesId(bo.getDishesId());
        if (CommonUtils.isNotEmpty(priceList)) {
            Label name = new Label("价格:");
            name.setPrefWidth(100);
            HBox optionBox = new HBox();
            optionBox.setSpacing(10);

            ToggleGroup group = new ToggleGroup();
            List<RadioButton> radios = new ArrayList<>();
            priceList.forEach(price -> {
                RadioButton radio = new RadioButton(
                        price.getDishesPriceName() + ": " + price.getDishesPrice() + "元/份");
                radio.setToggleGroup(group);
                radio.setUserData(price);
                radios.add(radio);
            });
            radios.get(0).setSelected(true);

            optionBox.getChildren().addAll(radios);
            collectActions.add(() -> {
                DishesPrice selectPrice = (DishesPrice) group.getSelectedToggle().getUserData();
                bo.setDishesPriceId(selectPrice.getDishesPriceId());
                bo.setDishesPrice(selectPrice.getDishesPrice());
            });

            HBox line = form.newLine(name, optionBox);
            line.setSpacing(15);
            line.setPadding(new Insets(15, 0, 0, 10));
            form.addLine(line);
        }
        // 添加数量
        TextField inputNumber = new TextField();
        inputNumber.setText("1");
        if (dishesAttrs.isEmpty()) {
            Label inputNumberLabel = new Label("下单数量:");
            HBox numLine = form.newCenterLine(inputNumberLabel, inputNumber);
            numLine.setPadding(new Insets(20, 0, 0, 0));
            form.addLine(numLine);
        } else {
            Label inputNumberLabel = new Label("下单数量:");
            inputNumberLabel.setPrefWidth(100);
            form.addLine(form.newLine(inputNumberLabel, inputNumber));
        }

        // 按钮
        Button add = new Button("添加购物车");
        add.setOnAction(evt -> {
            try {
                CommonUtils.safeRun(collectActions);
                bo.setNum(CommonUtils.parseInt(inputNumber.getText(), 1));
                addDishesToCart(bo, dishesAttrs);
            } catch (Exception ex) {
                Logger.info(ex.getMessage());
                AlertBuilder.ERROR("添加购物车出现错误了:" + ex.getMessage());
                return;
            }
            stage.close();
        });
        form.addLine(form.newCenterLine(add));
        stage.setScene(new Scene(form));
        stage.showAndWait();
    }

    private List<DishesAttributeValueVO> getSelectedAttr(List<DishesAttributeValueVO> all, List<String> selectedVals) {
        List<DishesAttributeValueVO> selected = new ArrayList<>();
        CommonUtils.forEach(all, a -> {
            if (selectedVals.contains(a.getAttributeValue())) {
                selected.add(CopyUtils.deepClone(a));
            }
        });
        return selected;
    }

    private void addDishesToCart(DishesChoiceItemBO bo, List<DishesAttributeVO> dishesAttrs) {
        Logger.info("添加到购物车," +
                "DishesId=" + bo.getDishesId() + "," +
                "PackageId=" + bo.getDishesPackageId() + "," +
                bo.getDishesName() + ", " +
                (bo.getIfPackage() == 0 ? "普通菜品" : "套餐")
                + CommonUtils.reflectString(param));
        CartItemVO cartItem = new CartItemVO();
        cartItem.setCartDishesId(CommonUtils.randomNumber(0, Integer.MAX_VALUE));
        if (bo.getIfPackage() == 1) {
            cartItem.setDishesId(bo.getDishesPackageId());
        } else {
            cartItem.setDishesId(bo.getDishesId());
        }
        cartItem.setIfDishesPackage(bo.getIfPackage());
        cartItem.setDishesPriceId(bo.getDishesPriceId());
        cartItem.setNums(bo.getNum() != null ? bo.getNum() : 1);
        cartItem.setDishesAttrs(dishesAttrs);
        this.addCartItem(cartItem);
    }

    private void addCartItem(CartItemVO cartItem) {
        try {
            Result<CartVO> addCartRs = cartService.addItem(param.getDeskId(), cartItem);
            Logger.info("购物车信息:" + JSON.toJSONString(addCartRs));
            if (addCartRs.isSuccess()) {
                AlertBuilder.INFO("通知消息", "添加购物车成功");
                cartSize.set(CollectionUtils.size(addCartRs.getData().getContents()));
            } else {
                AlertBuilder.ERROR(addCartRs.getMsg());
            }
        } catch (Exception ex) {
            AlertBuilder.ERROR("报错消息", "添加购物车异常," + ex.getMessage());
        }
    }

    private void createOrderFromCart() {
        try {
            PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
            req.setDeskId(param.getDeskId());
            req.setOrderId(param.getOrderId());
            Result<String> createOrderRs = cartService.createOrder(req);
            if (createOrderRs.isSuccess()) {
                refreshCartSize();
                AlertBuilder.INFO("通知消息", "下单成功");
                this.getScene().getWindow().hide();
            } else {
                AlertBuilder.ERROR(createOrderRs.getMsg());
            }
        } catch (Exception ex) {
            Logger.info("下单失败:" + ex.getMessage());
            AlertBuilder.ERROR("通知消息", "下单失败:" + ex.getMessage());
        }
    }

    private ComboBox<DishesType> dishesTypeIdSelector() {
        DishesTypeService dishesTypeService = GuiceContainer.getInstance(DishesTypeService.class);
        List<DishesType> types = dishesTypeService.loadAllTypesValid();
        DishesType def = new DishesType();
        def.setTypeName("请选择菜品类型");
        types.add(0, def);
        ObservableList<DishesType> options = FXCollections.observableArrayList(types);
        ComboBox<DishesType> selector = new ComboBox<>(options);
        selector.getSelectionModel().selectFirst();
        selector.setConverter(new StringConverter<DishesType>() {
            @Override
            public String toString(DishesType object) {
                return object.getTypeName();
            }

            @Override
            public DishesType fromString(String string) {
                return null;
            }
        });
        selector.valueProperty().addListener((_this, _old, _new) -> {
            DishesQueryCond cond = CopyUtils.cloneObj(qryDishesCond.get());
            if (cond.getDishesTypeId() == null || !cond.getDishesTypeId().equals(_new.getTypeId())) {
                cond.setDishesTypeId(_new.getTypeId());
                cond.setPageNo(1);
                qryDishesCond.set(cond);
            }
        });
        return selector;
    }

    private void refreshCartSize() {
        try {
            cartSize.set(cartService.getCartItems(param.getDeskId()).size());
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
        }
    }

    private ImageView getImageView(String path, double width) {
        try {
            // Logger.info("展示图片: " + path + ", " + width);
            ImageView iv = buildImageView(path);
            assert iv != null;
            iv.setFitWidth(width);
            iv.setFitHeight(width / 3 * 2);
            return iv;
        } catch (Exception ex) {
            Logger.info("展示图片异常: " + ex.getMessage() + "," + path);
            ImageView iv = buildImageView("/img/logo.png");
            assert iv != null;
            iv.setFitWidth(width);
            iv.setFitHeight(width / 3 * 2);
            return iv;
        }
    }

    private List<String> collectValue(List<CheckBox> cbs) {
        return cbs.stream().filter(CheckBox::isSelected)
                .map(Node::getUserData)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("ShowDishesView Stage销毁了。。。。。。。");
    }
}
