package com.xjh.startup.view;

import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.startup.view.base.FxmlView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class LoginView {
    private static final Holder<Scene> holder = new Holder<>();

    public static Scene getLoginView(){
        if(holder.get() != null){
            return holder.get();
        }
        synchronized (LoginView.class){
            if(holder.get() != null){
                return holder.get();
            }
            TimeRecord timeRecord = TimeRecord.start();
            VBox login = FxmlView.load("login");
            assert login != null;
            login.setAlignment(Pos.CENTER);
            login.setPadding(new Insets(0, 400, 0, 400));
            Scene loginScene = new Scene(login);
            loginScene.getStylesheets().add("/css/style.css");
            holder.hold(loginScene);
            Logger.info("加载登录界面, cost " + timeRecord.getCostAndReset());
            return loginScene;
        }
    }
}
