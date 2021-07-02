package com.xjh.startup.view;

import com.oracle.tools.packager.Log;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

import java.io.File;

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
        if (CommonUtils.isBlank(get("workDir"))) {
            return false;
        }
        return true;
    }

    private void showPath() throws RocksDBException {
        int row = 0;
        // 工作目录配置
        String workDirPropName = "workDir";
        row++;
        TextField imgPathField = new TextField();
        imgPathField.setText(get(workDirPropName));
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
            set(workDirPropName, data);
        });
    }

    public static String getImageDir() {
        String imageDir = get("workDir") + "/images/";
        LogUtils.info("图片目录:" + imageDir);
        return imageDir;
    }

    private static String get(String key) {
        Runnable close = CommonUtils::emptyAction;
        try {
            TtlDB db = openDB();
            close = () -> CommonUtils.safeRun(db::close);
            byte[] imgPathB = db.get(key.getBytes());
            if (imgPathB != null) {
                return new String(imgPathB);
            }
            return null;
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        } finally {
            close.run();
        }
        return null;
    }

    private static void set(String key, String val) {
        if (val == null) {
            return;
        }
        Runnable close = CommonUtils::emptyAction;
        try {
            TtlDB db = openDB();
            close = () -> CommonUtils.safeRun(db::close);
            db.put(key.getBytes(), val.getBytes());
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        } finally {
            close.run();
        }
    }

    private static TtlDB openDB() throws RocksDBException {
        String userHome = System.getProperty("user.home");
        Log.info("user home = " + userHome);

        File home = new File(userHome + "/ShopSystem/.config");
        LogUtils.info("系统基础信息目录:" + home.getAbsolutePath());
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RocksDBException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        TtlDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);

        return TtlDB.open(options, home.getAbsolutePath());
    }
}
