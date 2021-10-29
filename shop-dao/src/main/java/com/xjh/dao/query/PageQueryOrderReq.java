package com.xjh.dao.query;

import static com.xjh.common.utils.CopyUtils.deepClone;

import java.time.LocalDate;

import com.xjh.common.utils.CommonUtils;

import lombok.Data;

@Data
public class PageQueryOrderReq {
    Integer pageNo = 1;
    Integer pageSize = 20;
    String dishesName;
    Integer orderId;
    Integer status;
    LocalDate startDate;
    LocalDate endDate;
    Integer deskId;
    Integer accountId;
    Integer ver;

    public PageQueryOrderReq newVer() {
        this.ver = CommonUtils.randomNumber(1, Integer.MAX_VALUE);
        return deepClone(this);
    }
}
