package com.xjh.startup.view;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.valueobject.DishesImg;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.startup.foundation.guice.GuiceContainer;

import cn.hutool.core.codec.Base64;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class OrderDishesChoiceView extends VBox {
    private JSONObject data;

    public OrderDishesChoiceView(JSONObject data) {
        this.data = data;
        this.getChildren().add(dishesView());
    }

    private ScrollPane dishesView() {
        DishesDAO dishesDAO = GuiceContainer.getInstance(DishesDAO.class);
        List<Dishes> dishesList = dishesDAO.selectList(new Dishes());
        ScrollPane sp = new ScrollPane();
        FlowPane pane = new FlowPane();
        sp.setContent(pane);
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(1200);
        dishesList.stream().limit(60).forEach(dishes -> {
            Platform.runLater(() -> {
                System.out.println("add dishes >> " + JSONObject.toJSONString(dishes));
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
                box.getChildren().add(getImageView(img));
                box.getChildren().add(new Label(dishes.getDishesName()));
                box.getChildren().add(new Label("单价:" + CommonUtils.formatMoney(dishes.getDishesPrice()) + "元"));

                pane.getChildren().add(box);
            });
        });
        return sp;
    }

    private ImageView getImageView(String path) {
        try {
            path = "/" + path.replaceAll("\\\\", "/");
            ImageView iv = new ImageView(path);
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
