package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("return_reason")
public class ReturnReasonDO {
    @Id
    Integer id;

    @Column("deskName")
    String deskName;

    @Column("orderId")
    Integer orderId;

    @Column("dishesName")
    String dishesName;

    @Column("returnReason")
    String returnReason;

    @Column("addtime")
    Long addtime;
}
