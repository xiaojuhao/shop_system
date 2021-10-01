package com.xjh.common.utils.cellvalue;

import javafx.beans.value.ObservableValue;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class OperationButton {
    String title;
    Runnable action;
    Consumer<ObservableValue<?>> consumer;

    public OperationButton() {
    }

    public OperationButton(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public OperationButton(String title, Consumer<ObservableValue<?>> action) {
        this.title = title;
        this.consumer = action;
    }
}
