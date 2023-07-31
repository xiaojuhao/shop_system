package com.xjh.service.domain.model;

import com.xjh.dao.foundation.Column;
import lombok.Data;

@Data
public class DishesSaleStatModel {
    @Column Integer dishesId;
    @Column String dishesName;
    @Column Integer ifDishesPackage;
    @Column Integer dishesPriceId;
    @Column Integer count;
    @Column Double allPrice;
}
