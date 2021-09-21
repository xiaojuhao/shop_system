package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.OperationButton;

public class DishesAttributeValueBO {
    String attributeValue;
    OperationButton action;

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public OperationButton getAction() {
        return action;
    }

    public void setAction(OperationButton action) {
        this.action = action;
    }
}
