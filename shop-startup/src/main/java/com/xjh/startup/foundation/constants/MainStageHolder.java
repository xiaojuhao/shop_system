package com.xjh.startup.foundation.constants;

import com.xjh.common.utils.Holder;

import javafx.stage.Stage;

public class MainStageHolder {
    private static Holder<Stage> holder = new Holder<>();

    public static void hold(Stage stage) {
        holder.hold(stage);
    }

    public static Stage get() {
        return holder.get();
    }
}
