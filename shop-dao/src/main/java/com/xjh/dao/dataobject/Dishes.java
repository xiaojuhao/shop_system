package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

@Table("dishes_list")
public class Dishes {
    @Column("dishesId")
    Integer dishesId;

    @Column("dishesTypeId")
    Integer dishesTypeId;

    @Column("dishesName")
    String dishesName;

    @Column("dishesPrice")
    Double dishesPrice;

    @Column("dishesStock")
    Integer dishesStock;

    @Column("dishesDescription")
    String dishesDescription;

    @Column("dishesImgs")
    String dishesImgs;

    @Column("dishesUnitName")
    String dishesUnitName;

    @Column("dishesStatus")
    Integer dishesStatus;

    @Column("dishesPrivateAttribute")
    String dishesPrivateAttribute;

    @Column("dishesPublicAttribute")
    String dishesPublicAttribute;

    @Column("creatTime")
    Long creatTime;

    @Column("ifNeedMergePrint")
    Integer ifNeedMergePrint;

    @Column("ifNeedPrint")
    Integer ifNeedPrint;

    @Column("validTime")
    String validTime;

    @Column("isHidden")
    Integer isHidden;

    @Column("ifdelete")
    Integer ifdelete;

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public Integer getDishesTypeId() {
        return dishesTypeId;
    }

    public void setDishesTypeId(Integer dishesTypeId) {
        this.dishesTypeId = dishesTypeId;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public Double getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(Double dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public Integer getDishesStock() {
        return dishesStock;
    }

    public void setDishesStock(Integer dishesStock) {
        this.dishesStock = dishesStock;
    }

    public String getDishesDescription() {
        return dishesDescription;
    }

    public void setDishesDescription(String dishesDescription) {
        this.dishesDescription = dishesDescription;
    }

    public String getDishesImgs() {
        return dishesImgs;
    }

    public void setDishesImgs(String dishesImgs) {
        this.dishesImgs = dishesImgs;
    }

    public String getDishesUnitName() {
        return dishesUnitName;
    }

    public void setDishesUnitName(String dishesUnitName) {
        this.dishesUnitName = dishesUnitName;
    }

    public Integer getDishesStatus() {
        return dishesStatus;
    }

    public void setDishesStatus(Integer dishesStatus) {
        this.dishesStatus = dishesStatus;
    }

    public String getDishesPrivateAttribute() {
        return dishesPrivateAttribute;
    }

    public void setDishesPrivateAttribute(String dishesPrivateAttribute) {
        this.dishesPrivateAttribute = dishesPrivateAttribute;
    }

    public String getDishesPublicAttribute() {
        return dishesPublicAttribute;
    }

    public void setDishesPublicAttribute(String dishesPublicAttribute) {
        this.dishesPublicAttribute = dishesPublicAttribute;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public Integer getIfNeedMergePrint() {
        return ifNeedMergePrint;
    }

    public void setIfNeedMergePrint(Integer ifNeedMergePrint) {
        this.ifNeedMergePrint = ifNeedMergePrint;
    }

    public Integer getIfNeedPrint() {
        return ifNeedPrint;
    }

    public void setIfNeedPrint(Integer ifNeedPrint) {
        this.ifNeedPrint = ifNeedPrint;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getIfdelete() {
        return ifdelete;
    }

    public void setIfdelete(Integer ifdelete) {
        this.ifdelete = ifdelete;
    }
}
