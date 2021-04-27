package com.xjh.startup.view;

import javafx.fxml.FXMLLoader;

public class FxmlView {
    public static <T> T load(String fxml) {
        try {
            return new FXMLLoader(FxmlView.class.getResource("/fxml/" + fxml + ".fxml")).load();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
