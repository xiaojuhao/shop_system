package com.xjh.dao.query;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import com.xjh.common.valueobject.PageCond;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReturnReasonQuery extends PageCond {
    Integer orderId;

    LocalDate startDate;
    LocalDate endDate;

    Integer version;

    public ReturnReasonQuery newVer() {
        version = CommonUtils.randomNumber(1, 100000);
        return CopyUtils.deepClone(this);
    }
}
