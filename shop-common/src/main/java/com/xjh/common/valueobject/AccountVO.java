package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class AccountVO {
    Integer accountId;
    String accountUser;
    String accountNickName;
    String accountPass;
    String accountFather;
    String accountRight;
    Double removeLimit;
    Integer isDefault;
    Long creatTime;
}
