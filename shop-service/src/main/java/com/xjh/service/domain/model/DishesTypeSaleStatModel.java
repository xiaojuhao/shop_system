package com.xjh.service.domain.model;

import com.xjh.dao.foundation.Column;
import lombok.Data;

@Data
public class DishesTypeSaleStatModel {
    @Column Integer dishesTypeId;
    @Column Integer count;
    @Column Double allPrice;
}
