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
        if (CommonUtils.isBlank(get("imgPath"))) {
            return false;
        }
        return true;
    }

    private void showPath() throws RocksDBException {
        int row = 0;
        String workDirectory = System.getProperty("user.dir");
        Log.info(workDirectory);
        Label workDirectoryPath = new Label(workDirectory);
        workDirectoryPath.setPrefWidth(450);
        this.add(new Label("基础路基："), 0, row);
        this.add(workDirectoryPath, 1, row);

        // 图片地址
        row++;
        TextField imgPathField = new TextField();
        imgPathField.setText(get("imgPath"));
        this.add(new Label("图片目录："), 0, row);
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
            set("imgPath", data);
        });

    }

    public static String get(String key) {
        String workDirectory = System.getProperty("user.dir");
        Log.info(workDirectory);

        Runnable close = CommonUtils::emptyAction;
        try {
            TtlDB db = getDB(workDirectory);
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
            String workDirectory = System.getProperty("user.dir");
            Log.info(workDirectory);
            TtlDB db = getDB(workDirectory);
            close = () -> CommonUtils.safeRun(db::close);
            db.put(key.getBytes(), val.getBytes());
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        } finally {
            close.run();
        }
    }

    public static TtlDB getDB(String dir) throws RocksDBException {
        File home = new File(dir + "/.config");
        LogUtils.info("配置数据库目录:" + home.getAbsolutePath());
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RocksDBException("配置数据库目录失败:" + home.getAbsolutePath());
            }
        }
        TtlDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);

        return TtlDB.open(options, home.getAbsolutePath());
    }
}
