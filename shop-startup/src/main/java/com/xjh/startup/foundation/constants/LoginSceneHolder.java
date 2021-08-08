package com.xjh.startup.foundation.constants;

import com.xjh.common.utils.Holder;

import javafx.scene.Scene;

public class LoginSceneHolder {
    private static final Holder<Scene> holder = new Holder<>();

    public static void hold(Scene scene) {
        holder.hold(scene);
    }

    public static Scene get() {
        return holder.get();
    }
}
