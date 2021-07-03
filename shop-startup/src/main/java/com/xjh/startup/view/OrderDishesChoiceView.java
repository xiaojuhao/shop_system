package com.xjh.startup.view;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.ClickHelper;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.valueobject.DishesImg;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.reqmodel.PageCond;
import com.xjh.service.domain.CartService;
import com.xjh.service.domain.model.CartItemVO;
import com.xjh.service.domain.model.CartVO;
import com.xjh.service.domain.model.PlaceOrderFromCartReq;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.DeskOrderParam;
import com.xjh.startup.view.model.DishesQueryCond;

import cn.hutool.core.codec.Base64;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OrderDishesChoiceView extends VBox {
    DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);
    CartService cartService = GuiceContainer.getInstance(CartService.class);

    private DeskOrderParam data;
    private SimpleIntegerProperty cartNum = new SimpleIntegerProperty(0);
    private ObjectProperty<DishesQueryCond> dishesCond = new SimpleObjectProperty<>();

    public OrderDishesChoiceView(DeskOrderParam data) {
        this.data = data;
        this.getChildren().add(top());
        this.getChildren().add(separator());
        this.getChildren().add(initDishesView());
    }

    private HBox top() {
        try {
            cartNum.set(cartService.selectByDeskId(data.getDeskId()).size());
        } catch (Exception ex) {
            LogUtils.error(ex.getMessage());
        }
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        Button nextPage = new Button();
        nextPage.setText("下一页");
        nextPage.setOnMouseClicked(evt -> {
            DishesQueryCond _old = dishesCond.get();
            DishesQueryCond _new = CopyUtils.cloneObj(_old);
            _new.setPageNo(_old.getPageNo() + 1);
            dishesCond.set(_new);
        });

        Button prevPage = new Button();
        prevPage.setText("上一页");
        prevPage.setOnMouseClicked(evt -> {
            DishesQueryCond _old = dishesCond.get();
            DishesQueryCond _new = CopyUtils.cloneObj(_old);
            _new.setPageNo(Math.max(1, _old.getPageNo() - 1));
            dishesCond.set(_new);
        });

        Button cartBtn = new Button();
        cartBtn.setText("查看购物车(" + cartNum.get() + ")");
        cartNum.addListener((_this, _old, _new) -> {
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
        });
        Button placeOrder = new Button("直接下单");
        placeOrder.setOnMouseClicked(evt -> {
            try {
                PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
                req.setDeskId(data.getDeskId());
                req.setOrderId(data.getOrderId());
                cartService.createOrder(req);
                cartNum.set(0);
                AlertBuilder.INFO("通知消息", "下单成功");
            } catch (Exception ex) {
                LogUtils.info("下单失败:" + ex.getMessage());
                AlertBuilder.ERROR("通知消息", "下单失败");
            }
        });
        hbox.getChildren().add(prevPage);
        hbox.getChildren().add(nextPage);
        hbox.getChildren().add(placeOrder);
        hbox.getChildren().add(cartBtn);
        return hbox;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
    }

    private ScrollPane initDishesView() {
        ScrollPane sp = new ScrollPane();
        FlowPane pane = new FlowPane();
        sp.setContent(pane);
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(1200);
        dishesCond.addListener((_this, _old, _new) -> {
            List<Dishes> dishesList = queryList(_new);
            List<VBox> list = CommonUtils.map(dishesList, this::buildDishesView);
            Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.getChildren().addAll(list);
            });
        });
        dishesCond.set(new DishesQueryCond());
        return sp;
    }

    private List<Dishes> queryList(DishesQueryCond queryCond) {
        PageCond page = new PageCond();
        page.setPageNo(queryCond.getPageNo());
        page.setPageSize(queryCond.getPageSize());
        Dishes cond = new Dishes();
        return dishesDAO.pageQuery(cond, page);
    }

    private VBox buildDishesView(Dishes dishes) {
        VBox box = new VBox();
        box.setPrefWidth(200);
        String img = null;
        String base64Imgs = dishes.getDishesImgs();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = Base64.decodeStr(base64Imgs);
            List<DishesImg> arr = JSONArray.parseArray(json, DishesImg.class);
            if (arr != null && arr.size() > 0) {
                img = arr.get(0).getImageSrc();
            }
        }
        ImageView iv = getImageView(img);
        iv.setOnMouseClicked(evt -> {
            if (ClickHelper.isDblClick()) {
                LogUtils.info("添加到购物车" +
                        dishes.getDishesId() + "," +
                        dishes.getDishesName() + ", "
                        + data);
                CartItemVO cartItem = new CartItemVO();
                cartItem.setDishesId(dishes.getDishesId());
                cartItem.setDishesPriceId(0);
                cartItem.setNums(1);
                cartItem.setIfDishesPackage(0);
                try {
                    CartVO cart = cartService.addItem(data.getDeskId(), cartItem);
                    if (cart != null) {
                        AlertBuilder.INFO("通知消息", "添加购物车成功");
                        cartNum.set(CollectionUtils.size(cart.getContents()));
                    } else {
                        AlertBuilder.ERROR("报错消息", "添加购物车失败");
                    }
                } catch (Exception ex) {
                    AlertBuilder.ERROR("报错消息", "添加购物车异常");
                }
            }
        });
        box.getChildren().add(iv);
        box.getChildren().add(new Label(dishes.getDishesName()));
        box.getChildren().add(new Label("单价:" + CommonUtils.formatMoney(dishes.getDishesPrice()) + "元"));

        return box;
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
                LogUtils.info("添加到购物车" +
                        dishes.getDishesId() + "," +
                        dishes.getDishesName() + ", "
                        + data);
                CartItemVO cartItem = new CartItemVO();
                cartItem.setDishesId(dishes.getDishesId());
                cartItem.setDishesPriceId(0);
                cartItem.setNums(1);
                cartItem.setIfDishesPackage(0);
                try {
                    CartVO cart = cartService.addItem(data.getDeskId(), cartItem);
                    if (cart != null) {
                        AlertBuilder.INFO("通知消息", "添加购物车成功");
                        cartNum.set(CollectionUtils.size(cart.getContents()));
                    } else {
                        AlertBuilder.ERROR("报错消息", "添加购物车失败");
                    }
                } catch (Exception ex) {
                    AlertBuilder.ERROR("报错消息", "添加购物车异常");
                }
            }
        });
        box.getChildren().add(canvas);
        return box;
    }

    private ImageView getImageView(String path) {
        try {
            ImageView iv = new ImageView(new Image(getImageUrl(path)));
            iv.setFitWidth(180);
            iv.setFitHeight(100);
            return iv;
        } catch (Exception ex) {
            ImageView iv = new ImageView(getImageUrl(""));
            iv.setFitWidth(180);
            iv.setFitHeight(100);
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
