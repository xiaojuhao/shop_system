package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;

public class DishesAttributeBO {
    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    RichText isValueRadio;
    RichText createTime;
    Operations operations = new Operations();
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

    public Operations getOperations() {
        return operations;
    }

    public void setOperations(Operations operations) {
        this.operations = operations;
    }
}
