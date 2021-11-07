package com.xjh.common.utils.cellvalue;

import java.util.function.Consumer;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class OperationButton {
    RichText title;
    StringProperty titleProperty;
    Runnable action;
    Consumer<Observable> consumer;

    public OperationButton() {
    }

    public OperationButton(String title, Runnable action) {
        this(RichText.create(title), action);
    }

    public OperationButton(RichText title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public OperationButton(String title, Consumer<Observable> consumer) {
        this(RichText.create(title), consumer);
    }

    public OperationButton(RichText title, Consumer<Observable> consumer) {
        this.title = title;
        this.consumer = consumer;
    }
}
