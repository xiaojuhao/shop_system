package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

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

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    public String getAccountNickName() {
        return accountNickName;
    }

    public void setAccountNickName(String accountNickName) {
        this.accountNickName = accountNickName;
    }

    public String getAccountPass() {
        return accountPass;
    }

    public void setAccountPass(String accountPass) {
        this.accountPass = accountPass;
    }

    public String getAccountFather() {
        return accountFather;
    }

    public void setAccountFather(String accountFather) {
        this.accountFather = accountFather;
    }

    public String getAccountRight() {
        return accountRight;
    }

    public void setAccountRight(String accountRight) {
        this.accountRight = accountRight;
    }

    public Double getRemoveLimit() {
        return removeLimit;
    }

    public void setRemoveLimit(Double removeLimit) {
        this.removeLimit = removeLimit;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }
}
