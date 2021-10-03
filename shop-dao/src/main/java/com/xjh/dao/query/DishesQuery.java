package com.xjh.dao.query;

import com.xjh.common.utils.CommonUtils;

import lombok.Data;

@Data
public class DishesQuery {
    int pageNo = 1;
    int pageSize = 20;
    String dishesName;
    String status;
    Integer version;

    public DishesQuery newVersion() {
        this.version = CommonUtils.randomNumber(1, 100000);
        return this;
    }

}
