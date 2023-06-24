package com.xjh.common.valueobject;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DishesAttributeVO {
    int dishesAttributeId = 0;
    String dishesAttributeName = "";
    String dishesAttributeMarkInfo = "";
    boolean isValueRadio = false;
    boolean isSync = false;
    long createTime = 0L;
    List<DishesAttributeValueVO> selectedAttributeValues = new ArrayList<>();
    List<DishesAttributeValueVO> allAttributeValues = new ArrayList<>();

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

    public boolean getIsValueRadio() {
        return isValueRadio;
    }

    public void setIsValueRadio(boolean valueRadio) {
        isValueRadio = valueRadio;
    }

    public boolean getIsSync() {
        return isSync;
    }

    public void setIsSync(boolean sync) {
        isSync = sync;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<DishesAttributeValueVO> getSelectedAttributeValues() {
        return selectedAttributeValues;
    }

    public void setSelectedAttributeValues(List<DishesAttributeValueVO> selectedAttributeValues) {
        this.selectedAttributeValues = selectedAttributeValues;
    }

    public List<DishesAttributeValueVO> getAllAttributeValues() {
        return allAttributeValues;
    }

    public void setAllAttributeValues(List<DishesAttributeValueVO> allAttributeValues) {
        this.allAttributeValues = allAttributeValues;
    }
}
