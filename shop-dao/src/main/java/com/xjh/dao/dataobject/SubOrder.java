package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("suborder_list")
public class SubOrder {
    @Id
    Integer subOrderId;
    Integer orderId;
    Integer subOrderStatus;
    Integer accountId;
    Long createtime;
    Integer orderType;
}
