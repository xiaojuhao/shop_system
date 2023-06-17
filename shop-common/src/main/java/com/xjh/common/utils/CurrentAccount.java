package com.xjh.common.utils;

import com.xjh.common.valueobject.AccountVO;

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

}
