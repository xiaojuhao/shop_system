package com.xjh.service.domain.model;

import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.query.PageQueryOrderReq;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

import static com.xjh.common.utils.CopyUtils.deepClone;

@Data
public class DishesSaleStatReq {

    LocalDate startDate;
    LocalDate endDate;

    int pageNo;
    int pageSize;

    int ver;
    public DishesSaleStatReq newVer() {
        this.ver = CommonUtils.randomNumber(1, Integer.MAX_VALUE);
        return deepClone(this);
    }
}
