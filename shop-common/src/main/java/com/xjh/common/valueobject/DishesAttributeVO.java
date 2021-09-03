package com.xjh.common.valueobject;

import java.util.List;

public class DishesAttributeVO {
    Integer dishesAttributeId;

    String dishesAttributeName;

    String dishesAttributeMarkInfo;

    Boolean isValueRadio;

    Boolean isSync;

    List<DishesAttributeValueVO> selectedAttributeValues;

    List<DishesAttributeValueVO> allAttributeValues;

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

    public Boolean getIsValueRadio() {
        return isValueRadio;
    }

    public void setIsValueRadio(Boolean valueRadio) {
        isValueRadio = valueRadio;
    }

    public Boolean getIsSync() {
        return isSync;
    }

    public void setIsSync(Boolean sync) {
        isSync = sync;
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
