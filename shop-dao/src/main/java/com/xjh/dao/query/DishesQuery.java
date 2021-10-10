package com.xjh.dao.query;

import com.xjh.common.utils.CommonUtils;

import com.xjh.common.utils.CopyUtils;
import lombok.Data;

import static com.xjh.common.utils.CopyUtils.deepClone;

@Data
public class DishesQuery {
    int pageNo = 1;
    int pageSize = 20;
    String dishesName;
    Integer status;
    Integer version;

    public DishesQuery newVersion() {
        this.version = CommonUtils.randomNumber(1, 100000);
        return deepClone(this);
    }

}
