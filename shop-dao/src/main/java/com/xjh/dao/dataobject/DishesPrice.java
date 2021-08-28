package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("dishes_package_type")
public class DishesPrice {
    @Id
    @Column("dishesPriceId")
    private Integer dishesPriceId;
    @Column("dishesId")
    private Integer dishesId;
    @Column("dishesPriceName")
    private String dishesPriceName;
    @Column("dishesPrice")
    private Double dishesPrice;
    @Column("creatTime")
    private Long creatTime;

    public Integer getDishesPriceId() {
        return dishesPriceId;
    }

    public void setDishesPriceId(Integer dishesPriceId) {
        this.dishesPriceId = dishesPriceId;
    }

    public Integer getDishesId() {
        return dishesId;
    }

    public void setDishesId(Integer dishesId) {
        this.dishesId = dishesId;
    }

    public String getDishesPriceName() {
        return dishesPriceName;
    }

    public void setDishesPriceName(String dishesPriceName) {
        this.dishesPriceName = dishesPriceName;
    }

    public Double getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(Double dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }
}
