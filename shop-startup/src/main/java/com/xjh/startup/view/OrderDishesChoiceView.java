package com.xjh.startup.view;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.ClickHelper;
import com.xjh.common.utils.CommonUtils;
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
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class OrderDishesChoiceView extends VBox {
    DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);
    CartService cartService = GuiceContainer.getInstance(CartService.class);

    private DeskOrderParam data;
    private SimpleIntegerProperty cartNum = new SimpleIntegerProperty(0);

    public OrderDishesChoiceView(DeskOrderParam data) {
        this.data = data;
        this.getChildren().add(top());
        this.getChildren().add(separator());
        this.getChildren().add(dishesView());
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
        Button cartBtn = new Button();
        cartBtn.setText("查看购物车(" + cartNum.get() + ")");
        cartNum.addListener((cc, _old, _new) -> {
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
            cartStage.show();
        });
        Button placeOrder = new Button("直接下单");
        placeOrder.setOnMouseClicked(evt -> {
            try {
                PlaceOrderFromCartReq req = new PlaceOrderFromCartReq();
                req.setDeskId(data.getDeskId());
                req.setOrderId(data.getOrderId());
                cartService.createOrder(req);
                cartNum.set(0);
                AlertBuilder.INFO("通知消息", "下单成功").showAndWait();
            } catch (Exception ex) {
                AlertBuilder.ERROR("通知消息", "下单失败").showAndWait();
            }
        });
        hbox.getChildren().add(placeOrder);
        hbox.getChildren().add(cartBtn);
        return hbox;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        return s;
    }

    private ScrollPane dishesView() {
        PageCond page = new PageCond();
        Dishes cond = new Dishes();
        List<Dishes> dishesList = dishesDAO.pageQuery(cond, page);
        ScrollPane sp = new ScrollPane();
        FlowPane pane = new FlowPane();
        sp.setContent(pane);
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(1200);
        dishesList.stream().forEach(dishes -> {
            Platform.runLater(() -> {
                VBox box = new VBox();
                box.setPrefWidth(200);
                // Canvas canvas = new Canvas();
                // canvas.setWidth(200);
                // canvas.setHeight(200);
                // try {
                //   javafx.scene.image.Image img = new javafx.scene.image.Image("/img/book1.jpg");
                //   canvas.getGraphicsContext2D().drawImage(img, 0, 15, 180, 160);
                // } catch (Exception e) {
                //   e.printStackTrace();
                // }
                // canvas.getGraphicsContext2D().fillText(dishes.getDishesName(), 10,30);
                // canvas.getGraphicsContext2D().fillText(dishes.getDishesName(), 10,166);
                // box.getChildren().add(canvas);
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
                                AlertBuilder.INFO("通知消息", "添加购物车成功").showAndWait();
                                cartNum.set(CollectionUtils.size(cart.getContents()));
                            } else {
                                AlertBuilder.ERROR("报错消息", "添加购物车失败").showAndWait();
                            }
                        } catch (Exception ex) {
                            AlertBuilder.ERROR("报错消息", "添加购物车异常").showAndWait();
                        }
                    }
                });
                box.getChildren().add(iv);
                box.getChildren().add(new Label(dishes.getDishesName()));
                box.getChildren().add(new Label("单价:" + CommonUtils.formatMoney(dishes.getDishesPrice()) + "元"));

                pane.getChildren().add(box);
            });
        });
        return sp;
    }

    private ImageView getImageView(String path) {
        try {
            String imageDir = SysConfigView.getImageDir();
            if (!imageDir.endsWith("/")) {
                imageDir += "/";
            }
            path = imageDir + path.replaceAll("\\\\", "/");
            ImageView iv = new ImageView(new Image(path));
            iv.setFitWidth(180);
            iv.setFitHeight(100);
            return iv;
        } catch (Exception ex) {
            ImageView iv = new ImageView("/img/book1.jpg");
            iv.setFitWidth(180);
            iv.setFitHeight(100);
            return iv;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("ShowDishesView Stage销毁了。。。。。。。");
    }
}
