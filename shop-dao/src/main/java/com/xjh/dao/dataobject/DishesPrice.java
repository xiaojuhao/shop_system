package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("dishes_price")
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

}
