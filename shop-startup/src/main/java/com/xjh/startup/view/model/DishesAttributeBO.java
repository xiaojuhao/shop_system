package com.xjh.startup.view.model;

import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;

public class DishesAttributeBO {
    public DishesAttributeBO(DishesAttributeVO vo, Runnable action) {
        this.dishesAttributeId = vo.getDishesAttributeId();
        this.dishesAttributeName = vo.getDishesAttributeName();
        this.dishesAttributeMarkInfo = vo.getDishesAttributeMarkInfo();
        if (vo.getIsValueRadio() != null && vo.getIsValueRadio()) {
            this.isValueRadio = RichText.create("单选");
        } else {
            this.isValueRadio = RichText.create("多选");
        }
        this.createTime = RichText.create(DateBuilder.base(vo.getCreateTime()).timeStr());
        this.attachment = vo;
        this.operation = new OperationButton();
        this.operation.setTitle("编辑");
        this.operation.setAction(action);
    }

    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    RichText isValueRadio;
    RichText createTime;
    OperationButton operation;
    DishesAttributeVO attachment;

    public Integer getDishesAttributeId() {
        return dishesAttributeId;
    }

    public void setDishesAttributeId(Integer dishesAttributeId) {
        this.dishesAttributeId = dishesAttributeId;
    }

    public String getDishesAttributeName() {
        return dishesAttributeName;
    }

    public void setDishesAttributeName(String dishesAttributeName) {
        this.dishesAttributeName = dishesAttributeName;
    }

    public String getDishesAttributeMarkInfo() {
        return dishesAttributeMarkInfo;
    }

    public void setDishesAttributeMarkInfo(String dishesAttributeMarkInfo) {
        this.dishesAttributeMarkInfo = dishesAttributeMarkInfo;
    }

    public RichText getIsValueRadio() {
        return isValueRadio;
    }

    public void setIsValueRadio(RichText isValueRadio) {
        this.isValueRadio = isValueRadio;
    }

    public RichText getCreateTime() {
        return createTime;
    }

    public void setCreateTime(RichText createTime) {
        this.createTime = createTime;
    }

    public void setAttachment(DishesAttributeVO attachment) {
        this.attachment = attachment;
    }

    public DishesAttributeVO getAttachment() {
        return attachment;
    }

    public OperationButton getOperation() {
        return operation;
    }

    public void setOperation(OperationButton operation) {
        this.operation = operation;
    }
}
