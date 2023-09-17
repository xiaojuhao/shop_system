package com.xjh.common.valueobject;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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

    public Set<String> roles = new HashSet<>();

    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}
