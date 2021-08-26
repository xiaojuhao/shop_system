package com.xjh.startup.foundation.constants;

import com.xjh.common.utils.Holder;
import com.xjh.dao.dataobject.Account;

public class CurrentAccount {
    private static Holder<Account> holder = new Holder<>();

    public static void hold(Account stage) {
        holder.hold(stage);
    }

    public static Account get() {
        return holder.get();
    }

}
