package com.xjh.startup.foundation.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class FxmlUtils {
    public static <T> T load(String fxml) throws IOException {
        return new FXMLLoader(FxmlUtils.class.getResource("/fxml/" + fxml + ".fxml")).load();
    }
}
