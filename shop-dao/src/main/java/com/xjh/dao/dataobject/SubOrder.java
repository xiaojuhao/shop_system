package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("suborder_list")
public class SubOrder {
    @Id
    @Column
    Integer subOrderId;
    @Column
    Integer orderId;
    @Column
    Integer subOrderStatus;
    @Column
    Integer accountId;
    @Column
    Long createtime;
    @Column
    Integer orderType;
}
