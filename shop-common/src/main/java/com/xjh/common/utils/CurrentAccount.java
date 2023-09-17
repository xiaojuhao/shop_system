package com.xjh.common.utils;

import com.xjh.common.valueobject.AccountVO;

import java.util.Set;

public class CurrentAccount {
    private static Holder<AccountVO> holder = new Holder<>();

    public static void hold(AccountVO stage) {
        holder.hold(stage);
    }

    public static AccountVO get() {
        return holder.get();
    }

    public static int currentAccountId() {
        return get() != null ? get().getAccountId() : 1;
    }

    public static String currentAccountCode() {
        return get() != null ? get().getAccountUser() : "root";
    }

    public static boolean hasRoles(Set<String> roles) {
        if (roles == null || roles.size() == 0) {
            return true;
        }
        if (get() == null) {
            return false;
        }
        Set<String> accountRoles = get().getRoles();
        if (accountRoles == null || accountRoles.size() == 0) {
            return false;
        }
        for (String role : roles) {
            if (accountRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

}
