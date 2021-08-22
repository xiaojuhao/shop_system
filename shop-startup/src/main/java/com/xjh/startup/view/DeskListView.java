package com.xjh.startup.view;

import cn.hutool.core.lang.Holder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.dao.dataobject.Desk;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.DeskService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class DeskListView {
    private final static double padding = 10;
    private final static double gap = 5;
    private final static int size_per_line = 6;

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
        pane.setPadding(new Insets(padding));
        pane.setHgap(gap);
        pane.setVgap(gap);
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        s.setContent(pane);

        ThreadUtils.runInNewThread(() -> {
            double tableWidth = Math.max(width * 0.92 / size_per_line, 200) ;
            // 加载所有的tables
            allDesks().forEach(desk -> desks.add(new SimpleObjectProperty<>(desk)));
            List<DeskRectView> views = new ArrayList<>();
            desks.forEach(d -> views.add(new DeskRectView(d, tableWidth)));
            // 渲染tables;
            Platform.runLater(() -> pane.getChildren().addAll(views));
            // 监测变化
            while (instance.get() == s) {
                desks.forEach(this::detectChange);
                CommonUtils.sleep(1000);
            }
            Logger.info("******* DeskListView 循环退出." + Thread.currentThread().getName());
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
        Logger.info("DeskListView被销毁了。。。。。。。。");
    }
}
