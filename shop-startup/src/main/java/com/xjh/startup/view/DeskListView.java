package com.xjh.startup.view;

import static com.xjh.common.utils.CommonUtils.collect;
import static com.xjh.common.utils.CommonUtils.sleep;

import java.util.List;

import com.xjh.common.utils.Logger;
import com.xjh.common.utils.ThreadUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.startup.foundation.ioc.GuiceContainer;

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

public class DeskListView extends ScrollPane{
    private final static double padding = 10;
    private final static double gap = 5;

    DeskService deskService = GuiceContainer.getInstance(DeskService.class);
    static Holder<ScrollPane> instance = new Holder<>();

    public DeskListView() {
        instance.set(this);
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
        this.setContent(pane);
        ThreadUtils.runInNewThread(() -> {
            List<Desk> allDeskList = allDesks();
            int size_per_line = calcLineSize(allDeskList.size());
            double tableWidth = Math.max(width * 0.92 / size_per_line, 200);
            // 加载所有的tables
            allDeskList.forEach(desk -> desks.add(new SimpleObjectProperty<>(desk)));
            List<DeskRectView> views = collect(desks, it -> new DeskRectView(it, tableWidth));
            // 渲染tables;
            Platform.runLater(() -> pane.getChildren().addAll(views));
            // 监测变化
            while (instance.get() == this) {
                TimeRecord cost = TimeRecord.start();
                desks.forEach(it -> Platform.runLater(() -> {
                    Desk dd = deskService.getById(it.get().getDeskId());
                    if (dd != null) {
                        it.set(dd);
                    }
                }));
                sleep(1000 - cost.getCost());
            }
            Logger.info("******* DeskListView 循环退出." + Thread.currentThread().getName());
            System.gc();
        });
    }

    List<Desk> allDesks() {
        return deskService.getAllDesks();
    }

    private int calcLineSize(int size){
        int size_per_line;
        if(size > 60){
            size_per_line = 8;
        } else if(size > 40) {
            size_per_line = 7;
        } else {
            size_per_line = 6;
        }
        return size_per_line;
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.info("DeskListView被销毁了。。。。。。。。");
    }
}
