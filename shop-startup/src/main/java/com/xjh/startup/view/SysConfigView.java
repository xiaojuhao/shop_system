package com.xjh.startup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.xjh.common.store.RtPropNames;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SysConfigView extends GridPane {
    public SysConfigView() {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(20));
        try {
            initView();
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage());
        }
    }

    public static boolean checkConfig() {
        Properties properties = SysConfigUtils.loadRuntimeProperties();

        if (CommonUtils.isBlank(properties.getProperty(RtPropNames.workDirName))) {
            return false;
        }
        if (CommonUtils.isBlank(properties.getProperty(RtPropNames.dbUrlName))) {
            return false;
        }
        if (CommonUtils.isBlank(properties.getProperty(RtPropNames.dbDriverName))) {
            return false;
        }
        if (CommonUtils.isBlank(properties.getProperty(RtPropNames.dbUsernameName))) {
            return false;
        }
        if (CommonUtils.isBlank(properties.getProperty(RtPropNames.dbPasswordName))) {
            return false;
        }
        return true;
    }

    private void initView() {
        Properties runtimeProp = SysConfigUtils.loadRuntimeProperties();
        List<Runnable> collectData = new ArrayList<>();
        int row = 0;
        row++;
        TextField workDir = new TextField();
        workDir.setText(SysConfigUtils.getWorkDir());
        workDir.setPrefWidth(450);
        this.add(new Label("工作目录："), 0, row);
        this.add(workDir, 1, row);
        collectData.add(() -> runtimeProp.setProperty(RtPropNames.workDirName, CommonUtils.trim(workDir.getText())));

        row++;
        TextField dbUrl = new TextField();
        dbUrl.setText(runtimeProp.getProperty(RtPropNames.dbUrlName));
        dbUrl.setPrefWidth(450);
        this.add(new Label("数据库链接："), 0, row);
        this.add(dbUrl, 1, row);
        collectData.add(() -> runtimeProp.setProperty(RtPropNames.dbUrlName, CommonUtils.trim(dbUrl.getText())));

        row++;
        TextField dbDriver = new TextField();
        dbDriver.setText("com.mysql.cj.jdbc.Driver");
        dbDriver.setPrefWidth(450);
        dbDriver.setEditable(false);
        this.add(new Label("驱动："), 0, row);
        this.add(dbDriver, 1, row);
        collectData.add(() -> runtimeProp.setProperty(RtPropNames.dbDriverName, CommonUtils.trim(dbDriver.getText())));

        row++;
        TextField dbUser = new TextField();
        dbUser.setText(runtimeProp.getProperty(RtPropNames.dbUsernameName));
        dbUser.setPrefWidth(450);
        this.add(new Label("DB用户名："), 0, row);
        this.add(dbUser, 1, row);
        collectData.add(() -> runtimeProp.setProperty(RtPropNames.dbUsernameName, CommonUtils.trim(dbUser.getText())));

        row++;
        TextField dbPwd = new TextField();
        dbPwd.setText(runtimeProp.getProperty(RtPropNames.dbPasswordName));
        dbPwd.setPrefWidth(450);
        this.add(new Label("DB密码："), 0, row);
        this.add(dbPwd, 1, row);
        collectData.add(() -> runtimeProp.setProperty(RtPropNames.dbPasswordName, CommonUtils.trim(dbPwd.getText())));
        // save button
        row++;
        VBox saveRow = new VBox();
        Button saveBtn = new Button("保存");
        saveRow.getChildren().add(saveBtn);
        saveRow.setAlignment(Pos.CENTER);
        this.add(saveRow, 0, row, 2, 1);
        saveBtn.setOnMouseClicked(evt -> {
            CommonUtils.safeRun(collectData);
            SysConfigUtils.dumpRuntimeProperties(runtimeProp);
            AlertBuilder.INFO("提示", "保存成功");
        });
    }

    public static String getImageDir() {
        return SysConfigUtils.getWorkDir() + "images/";
    }


}
