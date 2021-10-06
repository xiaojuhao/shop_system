package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("discount_list")
public class DiscountDO {
    @Id
    @Column("id")
    Integer id;

    @Column("discount_name")
    String discountName;

    @Column("rate")
    Double rate;

    @Column("reason")
    String reason;
}
