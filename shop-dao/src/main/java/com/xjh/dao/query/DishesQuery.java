package com.xjh.dao.query;

import static com.xjh.common.utils.CopyUtils.deepClone;

import com.xjh.common.utils.CommonUtils;

import com.xjh.common.valueobject.PageCond;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishesQuery extends PageCond {
    String dishesName;
    Integer status;
    Integer version;

    public DishesQuery newVersion() {
        this.version = CommonUtils.randomNumber(1, 100000);
        return deepClone(this);
    }

}
