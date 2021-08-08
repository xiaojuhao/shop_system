package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import cn.hutool.core.lang.Holder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;

public class DeskListView {

    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    static Holder<ScrollPane> instance = new Holder<>();

    public ScrollPane view() {
        ScrollPane s = new ScrollPane();
        instance.set(s);
        ObservableList<SimpleObjectProperty<Desk>> desks = FXCollections.observableArrayList();
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        // LogUtils.info("screen width:" + width + ",height:" + height);
        //s.setFitToWidth(true);
        s.setContent(pane);

        ThreadUtils.runInNewThread(() -> {
            // 加载所有的tables
            double prefWidth = Math.max(width / 6 - 15, 200);
            allDesks().forEach(desk -> desks.add(new SimpleObjectProperty<>(desk)));
            List<DeskBox> views = new ArrayList<>();
            desks.forEach(d -> views.add(new DeskBox(d, prefWidth)));
            // 渲染tables;
            Platform.runLater(() -> pane.getChildren().addAll(views));
            // 监测变化
            while (instance.get() == s) {
                desks.forEach(this::detectChange);
                CommonUtils.sleep(1000);
            }
            LogUtils.info("******* DeskListView 循环退出." + Thread.currentThread().getName());
            System.gc();
        });
        return s;
    }

    void detectChange(SimpleObjectProperty<Desk> desk) {
        Desk dd = deskService.getById(desk.get().getDeskId());
        if (dd != null) {
            Platform.runLater(() -> desk.set(dd));
        }
    }

    List<Desk> allDesks() {
        return deskService.getAllDesks();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        LogUtils.info("DeskListView被销毁了。。。。。。。。");
    }
}
