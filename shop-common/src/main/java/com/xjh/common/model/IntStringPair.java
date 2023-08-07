package com.xjh.common.model;

import com.xjh.common.utils.CommonUtils;
import javafx.scene.control.ComboBox;
import lombok.Data;

@Data
public class IntStringPair {
    Integer key;
    String value;
    Object attachment;

    public IntStringPair(Integer key, String value) {
        this(key, value, null);
    }

    public IntStringPair(Integer key, String value, Object attachment) {
        this.key = key;
        this.value = value;
        this.attachment = attachment;
    }

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
