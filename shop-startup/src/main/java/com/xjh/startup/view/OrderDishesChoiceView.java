package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.enumeration.EnumChoiceAction;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.DishesImg;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPackage;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesPackageDAO;
import com.xjh.dao.reqmodel.PageCond;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.DishesService;
import com.xjh.service.domain.DishesTypeService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.service.domain.model.SendOrderRequest;
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
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.xjh.common.utils.CommonUtils.collect;


public class OrderDishesChoiceView extends VBox {
    DishesService dishesService = GuiceContainer.getInstance(DishesService.class);
    DishesPackageDAO dishesPackageDAO = GuiceContainer.getInstance(DishesPackageDAO.class);
    CartService cartService = GuiceContainer.getInstance(CartService.class);

    private final DeskOrderParam data;
    private final SimpleIntegerProperty cartSize = new SimpleIntegerProperty(0);
    private final ObjectProperty<DishesQueryCond> qryDishesCond = new SimpleObjectProperty<>();

    public OrderDishesChoiceView(DeskOrderParam data, double prefWidth) {
        this.data = data;
        this.getChildren().add(topMenus());
        this.getChildren().add(separator());
        this.getChildren().add(initDishesView(prefWidth));

        refreshCartSize();
    }

    private HBox topMenus() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        // 套餐、普通菜品选择
        hbox.getChildren().add(new Label("类型:"));
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton commonType = new RadioButton("普通菜品");
        commonType.setToggleGroup(toggleGroup);
        commonType.setUserData(0);
        commonType.setSelected(true);
        hbox.getChildren().add(commonType);
        // 送菜不展示套餐
        if (data.getChoiceAction() != EnumChoiceAction.SEND) {
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
        dishesNameField.focusedProperty().addListener((_obs, _old, _new) -> {
            qryDishesCond.get().setDishesName(dishesNameField.getText());
        });
        hbox.getChildren().add(dishesNameField);

        // 菜品类型
        hbox.getChildren().add(dishesTypeIdSelector());

        // 查询按钮
        Button queryBtn = new Button();
        queryBtn.setText("查 询");
        queryBtn.setOnMouseClicked(evt -> {
            qryDishesCond.set(CopyUtils.cloneObj(qryDishesCond.get()));
        });
        hbox.getChildren().add(queryBtn);

        // 分割线
        hbox.getChildren().add(new Separator(Orientation.VERTICAL));

        // 购物车按钮
        Button cartBtn = new Button();
        cartBtn.setText("查看购物车(" + cartSize.get() + ")");
        cartSize.addListener((_this, _old, _new) -> {
            cartBtn.setText("查看购物车(" + _new + ")");
        });
        cartBtn.setOnMouseClicked(evt -> {
            Stage cartStage = new Stage();
            cartStage.initOwner(this.getScene().getWindow());
            cartStage.initModality(Modality.WINDOW_MODAL);
            cartStage.initStyle(StageStyle.DECORATED);
            cartStage.centerOnScreen();
            cartStage.setWidth(this.getScene().getWindow().getWidth() - 10);
            cartStage.setHeight(this.getScene().getWindow().getHeight() - 100);
            cartStage.setTitle("购物车[桌号:" + data.getDeskName() + "]");
            cartStage.setScene(new Scene(new CartView(data)));
            cartStage.showAndWait();
            refreshCartSize();
        });
        hbox.getChildren().add(cartBtn);

        // 下单按钮(赠送时不展示)
        if (data.getChoiceAction() != EnumChoiceAction.SEND) {
            Button placeOrder = new Button("直接下单");
            placeOrder.setOnMouseClicked(evt -> createOrderFromCart());
            hbox.getChildren().add(placeOrder);
        }
        return hbox;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
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
        return dishesService.pageQuery(cond, page);
    }

    private List<DishesPackage> queryPackageList(DishesQueryCond queryCond) {
        PageCond page = new PageCond();
        page.setPageNo(1);
        page.setPageSize(60);
        DishesPackage cond = new DishesPackage();
        cond.setDishesPackageName(queryCond.getDishesName());
        return dishesPackageDAO.pageQuery(cond, page);
    }

    private DishesChoiceItemBO buildBO(Dishes dishes) {
        DishesChoiceItemBO bo = new DishesChoiceItemBO();
        String img = null;
        String base64Imgs = dishes.getDishesImgs();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = Base64.decodeStr(base64Imgs);
            List<DishesImg> arr = JSONArray.parseArray(json, DishesImg.class);
            if (arr != null && arr.size() > 0) {
                img = arr.get(0).getImageSrc();
            }
        }
        bo.setOrderId(data.getOrderId());
        bo.setDeskId(data.getDeskId());
        bo.setDeskName(data.getDeskName());

        bo.setImg(img);
        bo.setDishesId(dishes.getDishesId());
        bo.setDishesName(dishes.getDishesName());
        bo.setDishesPrice(dishes.getDishesPrice());
        bo.setIfPackage(0);
        return bo;
    }

    private DishesChoiceItemBO buildBO(DishesPackage dishes) {
        DishesChoiceItemBO bo = new DishesChoiceItemBO();
        String img = null;
        String base64Imgs = dishes.getDishesPackageImg();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = Base64.decodeStr(base64Imgs);
            List<DishesImg> arr = JSONArray.parseArray(json, DishesImg.class);
            if (arr != null && arr.size() > 0) {
                img = arr.get(0).getImageSrc();
            }
        }
        bo.setOrderId(data.getOrderId());
        bo.setDeskId(data.getDeskId());
        bo.setDeskName(data.getDeskName());
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
        box.setOnMouseClicked(evt -> {
            if (ClickHelper.isDblClick()) {
                // 赠送
                if (data.getChoiceAction() == EnumChoiceAction.SEND) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("送菜");
                    alert.setHeaderText("确定给用户送菜吗?");
                    alert.setContentText(bo.getDishesName());
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        return;
                    }
                    Result<String> sendRs = sendDishes(bo);
                    if (!sendRs.isSuccess()) {
                        AlertBuilder.ERROR(sendRs.getMsg());
                        return;
                    }
                }
                // 打开套餐
                else if (bo.getIfPackage() == 1) {
                    openPackageAddDialog(bo);
                }
                // 加入到购物车
                else {
                    addDishesToCart(bo);
                }
            }
        });
        box.getChildren().add(iv);
        box.getChildren().add(new Label(bo.getDishesName()));
        box.getChildren().add(new Label("单价:" + CommonUtils.formatMoney(bo.getDishesPrice()) + "元"));

        return box;
    }

    private Result<String> sendDishes(DishesChoiceItemBO bo) {
        SendOrderRequest req = new SendOrderRequest();
        CopyUtils.copy(bo, req);
        cartService.createSendOrder(req);
        return Result.success("赠送成功");
    }

    private void openPackageAddDialog(DishesChoiceItemBO bo) {
        Stage stage = new Stage();
        stage.initOwner(this.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(this.getScene().getWindow().getWidth() / 2);
        stage.setHeight(this.getScene().getWindow().getHeight() / 3 * 2);
        stage.setTitle("点菜[桌号:" + data.getDeskName() + "]");
        stage.setScene(new Scene(new PackageDishesChoiceView(bo, this::addCartItem)));
        stage.showAndWait();
    }

    private void addDishesToCart(DishesChoiceItemBO bo) {
        Logger.info("添加到购物车," +
                "DishesId=" + bo.getDishesId() + "," +
                "PackageId=" + bo.getDishesPackageId() + "," +
                bo.getDishesName() + ", " +
                (bo.getIfPackage() == 0 ? "普通菜品" : "套餐")
                + CommonUtils.reflectString(data));
        CartItemVO cartItem = new CartItemVO();
        if (bo.getIfPackage() == 1) {
            cartItem.setDishesId(bo.getDishesPackageId());
        } else {
            cartItem.setDishesId(bo.getDishesId());
        }
        cartItem.setIfDishesPackage(bo.getIfPackage());
        cartItem.setDishesPriceId(0);
        cartItem.setNums(1);

        this.addCartItem(cartItem);
    }

    private void addCartItem(CartItemVO cartItem) {
        try {
            Result<CartVO> addCartRs = cartService.addItem(data.getDeskId(), cartItem);
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
            req.setDeskId(data.getDeskId());
            req.setOrderId(data.getOrderId());
            Result<String> createOrderRs = cartService.createOrder(req);
            if (createOrderRs.isSuccess()) {
                refreshCartSize();
                AlertBuilder.INFO("通知消息", "下单成功");
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
        List<DishesType> types = dishesTypeService.loadAllTypes();
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

    private VBox paintDishesView(Dishes dishes) {
        VBox box = new VBox();
        box.setPrefWidth(200);
        Canvas canvas = new Canvas();
        canvas.setWidth(200);
        canvas.setHeight(210);

        String imgSrc = null;
        String base64Imgs = dishes.getDishesImgs();
        try {
            if (CommonUtils.isNotBlank(base64Imgs)) {
                String json = Base64.decodeStr(base64Imgs);
                List<DishesImg> arr = JSONArray.parseArray(json, DishesImg.class);
                if (CommonUtils.isNotEmpty(arr)) {
                    imgSrc = getImageUrl(arr.get(0).getImageSrc());
                }
            }
            Image img = new Image(imgSrc);
            canvas.getGraphicsContext2D().drawImage(img, 0, 10, 180, 160);
        } catch (Exception e) {
            e.printStackTrace();
        }
        canvas.getGraphicsContext2D().fillText(dishes.getDishesName(), 10, 185);
        canvas.getGraphicsContext2D().fillText("单价:" + dishes.getDishesPrice(), 10, 200);
        canvas.setOnMouseClicked(evt -> {
            if (ClickHelper.isDblClick()) {
                Logger.info("添加到购物车, DishesId=" +
                        dishes.getDishesId() + "," +
                        dishes.getDishesName() + ", "
                        + CommonUtils.reflectString(data));
                CartItemVO cartItem = new CartItemVO();
                cartItem.setDishesId(dishes.getDishesId());
                cartItem.setDishesPriceId(0);
                cartItem.setNums(1);
                cartItem.setIfDishesPackage(0);
                try {
                    Result<CartVO> addCartRs = cartService.addItem(data.getDeskId(), cartItem);
                    Logger.info("购物车信息:" + JSON.toJSONString(addCartRs));
                    if (addCartRs.isSuccess()) {
                        AlertBuilder.INFO("通知消息", "添加购物车成功");
                        cartSize.set(CollectionUtils.size(addCartRs.getData().getContents()));
                    } else {
                        AlertBuilder.ERROR(addCartRs.getMsg());
                    }
                } catch (Exception ex) {
                    AlertBuilder.ERROR("报错消息", "添加购物车异常");
                }
            }
        });
        box.getChildren().add(canvas);
        return box;
    }

    private void refreshCartSize() {
        try {
            cartSize.set(cartService.selectByDeskId(data.getDeskId()).size());
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
        }
    }

    private ImageView getImageView(String path, double width) {
        try {
            ImageView iv = new ImageView(new Image(getImageUrl(path)));
            iv.setFitWidth(width);
            iv.setFitHeight(width / 3 * 2);
            return iv;
        } catch (Exception ex) {
            ImageView iv = new ImageView(getImageUrl("/img/logo.png"));
            iv.setFitWidth(width);
            iv.setFitHeight(width / 3 * 2);
            return iv;
        }
    }

    private String getImageUrl(String url) {
        if (CommonUtils.isBlank(url)) {
            url = "/img/book1.jpg";
        }
        String imageDir = SysConfigView.getImageDir();
        return "file:" + imageDir + url.replaceAll("\\\\", "/");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("ShowDishesView Stage销毁了。。。。。。。");
    }
}
