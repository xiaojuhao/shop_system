package com.xjh.startup.view;

import org.rocksdb.RocksDBException;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.SysConfigUtils;

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
            showPath();
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        }
    }

    public static boolean checkConfig() {
        if (CommonUtils.isBlank(SysConfigUtils.getWorkDir())) {
            return false;
        }
        return true;
    }

    private void showPath() throws RocksDBException {
        int row = 0;
        row++;
        TextField imgPathField = new TextField();
        imgPathField.setText(SysConfigUtils.getWorkDir());
        imgPathField.setPrefWidth(450);
        this.add(new Label("工作目录："), 0, row);
        this.add(imgPathField, 1, row);

        // save button
        row++;
        VBox saveRow = new VBox();
        Button saveBtn = new Button("保存");
        saveRow.getChildren().add(saveBtn);
        saveRow.setAlignment(Pos.CENTER);
        this.add(saveRow, 0, row, 2, 1);
        saveBtn.setOnMouseClicked(evt -> {
            String data = imgPathField.getText();
            SysConfigUtils.setWorkDir(data);
        });
    }

    public static String getImageDir() {
        String imageDir = SysConfigUtils.getWorkDir() + "images/";
        // LogUtils.info("图片目录:" + imageDir);
        return imageDir;
    }


}
