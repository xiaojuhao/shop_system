package com.xjh.dao.query;

import lombok.Data;

import static com.xjh.common.utils.CopyUtils.deepClone;

@Data
public class DishesPackageQuery {
    int pageNo = 1;
    int pageSize = 20;
    String name;
    String status;
    Integer packageId;
    Integer version = 0;

    public DishesPackageQuery newVersion() {
        this.version = this.version + 1;
        return deepClone(this);
    }
}
