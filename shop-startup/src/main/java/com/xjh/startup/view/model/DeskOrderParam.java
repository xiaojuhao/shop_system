package com.xjh.startup.view.model;

import com.xjh.common.enumeration.EnumChoiceAction;

public class DeskOrderParam {
    Integer orderId;
    Integer deskId;
    String deskName;
    Runnable callback;
    EnumChoiceAction choiceAction;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public String getDeskName() {
        return deskName;
    }

    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public EnumChoiceAction getChoiceAction() {
        return choiceAction;
    }

    public void setChoiceAction(EnumChoiceAction choiceAction) {
        this.choiceAction = choiceAction;
    }
}
