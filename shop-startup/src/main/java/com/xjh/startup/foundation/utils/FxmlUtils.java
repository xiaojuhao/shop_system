package com.xjh.startup.foundation.utils;

import javafx.fxml.FXMLLoader;

public class FxmlUtils {
    public static <T> T load(String fxml) {
        try {
            return new FXMLLoader(FxmlUtils.class.getResource("/fxml/" + fxml + ".fxml")).load();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
