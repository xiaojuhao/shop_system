package com.xjh.startup.view;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.startup.foundation.guice.GuiceContainer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class ShowDishesView extends VBox {
    private JSONObject data;

    public ShowDishesView(JSONObject data) {
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
        dishesList.forEach(dishes -> {
            Platform.runLater(() -> {
                System.out.println("add dishes >> " + JSONObject.toJSONString(dishes));
                VBox box = new VBox();
                box.setPrefWidth(200);
                box.getChildren().add(new Button(dishes.getDishesName()));
                box.getChildren().add(new Button(dishes.getDishesImgs()));
                box.getChildren().add(new Button(dishes.getDishesDescription()));
                pane.getChildren().add(box);
            });
        });
        return sp;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("ShowDishesView Stage销毁了。。。。。。。");
    }
}
