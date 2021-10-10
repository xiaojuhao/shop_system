package com.xjh.startup.view.model;

import com.xjh.common.utils.CommonUtils;

import javafx.scene.control.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntStringPair {
    Integer key;
    String value;

    public String toString() {
        return value;
    }

    public static void select(ComboBox<IntStringPair> combo, Integer val, Integer def) {
        Integer s = val != null ? val : def;
        combo.getItems().forEach(item -> {
            if (CommonUtils.eq(item.getKey(), s)) {
                combo.getSelectionModel().select(item);
            }
        });
    }
}
