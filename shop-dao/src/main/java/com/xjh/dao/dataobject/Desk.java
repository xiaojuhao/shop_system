package com.xjh.dao.dataobject;

import java.beans.Transient;
import java.io.Serializable;

import com.xjh.common.anno.NotColumn;
import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("desks")
public class Desk implements Serializable {
    private static final long serialVersionUID = -2784348482896858640L;
    @Id
    @Column("deskId")
    Integer deskId;
    @Column("deskName")
    String deskName;
    @Column("useStatus")
    Integer status;
    @Column("maxPersonNum")
    Integer maxPerson;
    @Column("orderId")
    Integer orderId;
    @Column("belongDeskType")
    Integer belongDeskType;
    @Column("createTime")
    Long orderCreateTime;
    @Column("physicalStatus")
    Integer physicalStatus;

    @NotColumn
    private long version;
    public Desk newVer(){
        this.version = System.currentTimeMillis();
        return this;
    }
}
