package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("suborder_list")
public class SubOrder {
    @Id
    Integer subOrderId;
    Integer orderId;
    Integer subOrderStatus;
    Integer accountId;
    Long createtime;
    Integer orderType;

    public Integer getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(Integer subOrderId) {
        this.subOrderId = subOrderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getSubOrderStatus() {
        return subOrderStatus;
    }

    public void setSubOrderStatus(Integer subOrderStatus) {
        this.subOrderStatus = subOrderStatus;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
