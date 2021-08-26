package com.xjh.startup.view;

import static com.xjh.common.utils.CommonUtils.collect;
import static com.xjh.common.utils.CommonUtils.sleep;

import java.util.List;

import com.xjh.common.utils.Logger;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.dataobject.Desk;
import com.xjh.guice.GuiceContainer;
import com.xjh.service.domain.DeskService;

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
    private final static double padding = 10;
    private final static double gap = 5;

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
        pane.setPrefHeight(height * 0.90);
        s.setContent(pane);

        ThreadUtils.runInNewThread(() -> {
            List<Desk> allDeskList = allDesks();
            int size_per_line;
            if(allDeskList.size() > 60){
                size_per_line = 8;
            } else if(allDeskList.size() > 40) {
                size_per_line = 7;
            } else {
                size_per_line = 6;
            }
            double tableWidth = Math.max(width * 0.92 / size_per_line, 200);
            // 加载所有的tables
            allDesks().forEach(desk -> desks.add(new SimpleObjectProperty<>(desk)));
            List<DeskRectView> views = collect(desks, it -> new DeskRectView(it, tableWidth));
            // 渲染tables;
            Platform.runLater(() -> pane.getChildren().addAll(views));
            // 监测变化
            while (instance.get() == s) {
                TimeRecord cost = TimeRecord.start();
                desks.forEach(it -> Platform.runLater(() -> {
                    // TimeRecord cost2 = TimeRecord.start();
                    Desk dd = deskService.getById(it.get().getDeskId());
                    if (dd != null) {
                        it.set(dd);
                    }
                    // System.out.println("cost22 = " + cost2.getCost());
                }));
                // System.out.println("cost1 = " + cost.getCost());
                sleep(1000 - cost.getCost());
                // System.out.println("cost2 = " + cost.getCost());
            }
            Logger.info("******* DeskListView 循环退出." + Thread.currentThread().getName());
            System.gc();
        });
        return s;
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
