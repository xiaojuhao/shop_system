package com.xjh.startup.controller;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.DeskView;

import cn.hutool.core.lang.Holder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

public class DeskController {

    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    static Holder<ScrollPane> instance = new Holder<>();

    public ScrollPane view() {
        ScrollPane s = new ScrollPane();
        instance.set(s);
        ObservableList<SimpleObjectProperty<Desk>> tables = FXCollections.observableArrayList();

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(600);
        s.setFitToWidth(true);
        s.setContent(pane);

        ThreadUtils.runInDaemon(() -> {
            // 加载所有的tables
            deskService.getAllDesks().forEach(desk -> tables.add(new SimpleObjectProperty<>(desk)));
            // 渲染tables
            tables.forEach(desk -> Platform.runLater(() -> render(desk, pane)));
            // 监测变化
            while (instance.get() == s) {
                tables.forEach(this::detectChange);
                CommonUtils.sleep(1000);
            }
            System.out.println("******* DeskController 循环退出." + Thread.currentThread().getName());
            System.gc();
        });
        return s;
    }

    void detectChange(SimpleObjectProperty<Desk> desk) {
        Desk dd = deskService.getById(desk.get().getId());
        if (dd != null) {
            Platform.runLater(() -> desk.set(dd));
        }
    }

    void render(SimpleObjectProperty<Desk> desk, FlowPane pane) {
        double prefWidth = Math.max(pane.getWidth() / 6 - 15, 200);
        pane.getChildren().add(new DeskView(desk, prefWidth));
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("DeskController被销毁了。。。。。。。。");
    }
}
