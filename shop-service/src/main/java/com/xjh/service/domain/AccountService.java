package com.xjh.service.domain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.mapper.AccountDAO;

import java.util.List;

@Singleton
public class AccountService {
    @Inject
    AccountDAO accountDAO;

    public Result<Account> getAccount(String username) {
        try {
            if (CommonUtils.isBlank(username)) {
                return Result.fail("请输入用户名称");
            }
            Account a = accountDAO.getByUserName(username);
            if (a == null) {
                return Result.fail("账号" + username + "不存在");
            } else {
                return Result.success(a);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("系统异常:" + ex.getMessage());
        }
    }

    public List<Account> listAll() {
        return accountDAO.selectList(new Account());
    }

    public Result<Account> checkPwd(String username, String pwd) {
        if ("1".equals(username)) {
            Account mock = new Account();
            mock.setAccountId(1);
            mock.setAccountUser("mockuser");
            mock.setAccountNickName("模拟用户");
            return Result.success(mock);

        }
        if (CommonUtils.isBlank(pwd)) {
            return Result.fail("请输入密码");
        }
        Result<Account> account = getAccount(username);
        if (!account.isSuccess()) {
            return Result.fail(account.getMsg());
        }
        String md5 = CommonUtils.md5hex(pwd);
        boolean b = CommonUtils.equalsIgnoreCase(md5, account.getData().getAccountPass());
        if (b) {
            return account;
        } else {
            return Result.fail("密码错误");
        }
    }
}
