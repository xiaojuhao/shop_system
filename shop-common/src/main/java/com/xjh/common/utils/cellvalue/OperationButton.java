package com.xjh.common.utils.cellvalue;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class OperationButton {
    String title;
    StringProperty titleProperty;
    Runnable action;
    Consumer<Observable> consumer;

    public OperationButton() {
    }

    public OperationButton(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public OperationButton(String title, Consumer<Observable> action) {
        this.title = title;
        this.consumer = action;
    }
}
