package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("coupons_list")
public class CouponList {
    @Id
    @Column("couponId")
    Integer couponId;

    @Column("couponName")
    String couponName;

    @Column("couponAmount")
    Double couponAmount;

    @Column("actualAmount")
    Double actualAmount;

    @Column("type")
    Integer type;
}
