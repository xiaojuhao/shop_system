package com.xjh.common.utils.cellvalue;

public class OperationButton {
    String title;
    Runnable action;

    public OperationButton() {
    }

    public OperationButton(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}
