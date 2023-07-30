package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("accounts")
public class Account {
    @Id
    @Column("accountId")
    Integer accountId;
    @Column("accountUser")
    String accountUser;
    @Column("accountNickName")
    String accountNickName;
    @Column("accountPass")
    String accountPass;
    @Column("accountFather")
    String accountFather;
    @Column("accountRight")
    String accountRight;
    @Column("removeLimit")
    Double removeLimit;
    @Column("isDefault")
    Integer isDefault;
    @Column("creatTime")
    Long creatTime;
}
