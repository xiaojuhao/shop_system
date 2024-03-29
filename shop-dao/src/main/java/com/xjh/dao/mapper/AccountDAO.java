package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AccountDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Account getByUserName(String userName) throws SQLException {
        if (CommonUtils.isBlank(userName)) {
            return null;
        }
        Account cond = new Account();
        cond.setAccountUser(userName);
        List<Account> desks = selectList(cond);
        return desks.stream().findFirst().orElse(null);
    }

    public Account getByAccountId(Integer accountId) {
        if (accountId == null) {
            return null;
        }
        Account cond = new Account();
        cond.setAccountId(accountId);
        List<Account> desks = selectList(cond);
        return desks.stream().findFirst().orElse(null);
    }

    public List<Account> selectList(Account cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Account.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
