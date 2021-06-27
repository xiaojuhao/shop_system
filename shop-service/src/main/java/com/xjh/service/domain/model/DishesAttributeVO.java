package com.xjh.service.domain.model;

public class DishesAttributeVO {
    private int dishesAttributeId;
    private String dishesAttributeName;
    private String dishesAttributeMarkInfo;
    private boolean isValueRadio;
    private boolean isSync;
    private long createTime;

    public int getDishesAttributeId() {
        return dishesAttributeId;
    }

    public void setDishesAttributeId(int dishesAttributeId) {
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

    public boolean isValueRadio() {
        return isValueRadio;
    }

    public void setValueRadio(boolean valueRadio) {
        isValueRadio = valueRadio;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
