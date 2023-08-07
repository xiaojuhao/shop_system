package com.xjh.common.model;

import com.xjh.common.utils.CommonUtils;
import lombok.Data;

@Data
public class DishesQueryCond {
    int pageNo = 1;
    int maxPageNo = Integer.MAX_VALUE;
    int pageSize = 18;
    String dishesName;
    Integer dishesTypeId;
    Integer ifPackage;
    Integer version;

    public DishesQueryCond newVersion() {
        this.version = CommonUtils.randomNumber(1, 1000000);
        return this;
    }

    public boolean arrivedMaxPageNo() {
        return pageNo >= maxPageNo;
    }
}
