package com.xjh.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.xjh.dao.dataobject.Account;
import com.xjh.dao.mapper.AccountDAO;
import com.xjh.dao.mapper.StoreDAO;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

@WsApiType("API_TYPE_LOGIN")
@Singleton
public class LoginHandler implements WsHandler {
    public static final String TRANSFER_STATION_IN_USER_NAME = "localServerIn";//这个账号用于h5用户登陆，不能删除
    public static final String LOCAL_SERVER_OUT_OTHER_PLACE = "LOCAL_SERVER_OUT_OTHER_PLACE";//另外一个本地服务器启动了
    public static final String API_ACCOUNT_USERNAME = "API_ACCOUNT_USERNAME";
    public static final String API_ACCOUNT_PASSWORD = "API_ACCOUNT_PASSWORD";
    public static final String API_TYPE = "API_TYPE";
    public static final String API_DEVICE_CODE = "API_DEVICE_CODE";   //设备号
    public static final String API_DEVICE_REMARK = "API_DEVICE_REMARK"; //设备备注信息
    public static final String API_TYPE_LOGIN = "API_TYPE_LOGIN"; //登陆结果
    public static final String API_LOGIN_RESULT = "API_LOGIN_RESULT"; //登陆结果
    public static final int LOGIN_RESULT_LOGIN_FAIL = 1;   //账号密码错误
    public static final int LOGIN_RESULT_DEVICE_DISABLE = 2; //登陆设备被禁用了
    public static final int LOGIN_RESULT_DEVICE_NOACTIVE = 3; //登陆设备未激活
    public static final int LOGIN_RESULT_LOGIN_OTHER_PLACE = 4; //此账号在另一个地方登陆
    public static final int LOGIN_RESULT_LOGIN_INCOMPLETE = 5; //登陆信息不全,json解析报错
    public static final int LOGIN_RESULT_LOGIN_SUCCESS = 6; //登陆成功

    public static final String API_CHANGE_PASS_RESULT = "API_CHANGE_PASS_RESULT";
    public static final int CHANGE_PASS_RESULT_FAIL = 1;   //原密码错误
    public static final int CHANGE_PASS_RESULT_SUCCESS = 2; //成功
    public static final int CHANGE_PASS_RESULT_ERROR = 3; //服务器异常或错误

    public static final String API_CHANGE_NIKE_NAME_RESULT = "API_CHANGE_NIKE_NAME_RESULT";
    public static final int CHANGE_NIKE_NAME_FAIL = 1;   //原密码错误
    public static final int CHANGE_NIKE_NAME_SUCCESS = 2; //成功


    @Inject
    AccountDAO accountDAO;
    @Inject
    StoreDAO storeDAO;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        String userName = msg.getString(API_ACCOUNT_USERNAME);
        String password = msg.getString(API_ACCOUNT_PASSWORD);
        String api_type = msg.getString(API_TYPE);
        String deviceCode = msg.getString(API_DEVICE_CODE);
        try {
//            AccountManager accountManager = (AccountManager) parameterPackage.getObject(AccountManager.class.getName());
            Account account = null;

            if (TRANSFER_STATION_IN_USER_NAME.equals(userName) && TRANSFER_STATION_IN_USER_NAME.equals(password)) {
                //中转账号的话直接通过
                account = login(userName, password, false, true);
            } else {
                try {
                    account = login(userName, password, false, true);
                } catch (Exception e) {
//                result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_LOGIN_FAIL);
//                /*后期再优化*/
//                JSONObject jSONObjectNew = new JSONObject();
//                jSONObjectNew.put(API_TYPE, api_type + "_ACK");
//                jSONObjectNew.put("status", "1");
//                jSONObjectNew.put("contents", "");
//                jSONObjectNew.put("msg", APIKeyword.LOGIN_RESULT_LOGIN_FAIL);
//                WritePaidLog writePaidLog = new WritePaidLog();
//                if (xjhConncter.getWebSocket() == null)
//                {
//                    writePaidLog.writeLogTest("给" + xjhConncter.getInteNetLink().getRemoteAddress().toString() + "答复关闭连接消息2:-->getUpdateDataPackage_ACK,长度" + jSONObjectNew.toString().length());
//                }
//                else
//                {
//                    writePaidLog.writeLogTest("给" + xjhConncter.getWebSocket().getRemoteSocketAddress().toString() + "答复关闭连接消息2:-->getUpdateDataPackage_ACK,长度" + jSONObjectNew.toString().length());
//                }
//                sendAndClose(parameterPackage, xjhConncter, jSONObjectNew.toString());
//                return;
                }

//                if (account == null) {
//                    result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_LOGIN_FAIL);
//                    /*后期再优化*/
//                    JSONObject jSONObjectNew = new JSONObject();
//                    jSONObjectNew.put(API_TYPE, api_type + "_ACK");
//                    jSONObjectNew.put("status", "1");
//                    jSONObjectNew.put("contents", "");
//                    jSONObjectNew.put("msg", APIKeyword.LOGIN_RESULT_LOGIN_FAIL);
//                    WritePaidLog writePaidLog = new WritePaidLog();
//                    if (xjhConncter.getWebSocket() == null) {
//                        writePaidLog.writeLogTest("给" + xjhConncter.getInteNetLink().getRemoteAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                    } else {
//                        writePaidLog.writeLogTest("给" + xjhConncter.getWebSocket().getRemoteSocketAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                    }
//                    sendAndClose(parameterPackage, xjhConncter, jSONObjectNew.toString());
//                    return;
//                }
//
//                DeviceManager deviceManager = (DeviceManager) parameterPackage.getObject(DeviceManager.class.getName());
//                DeviceResult deviceResult = deviceManager.checkDevice(deviceCode);
//                if (!deviceResult.isSuccess()) {
//                    /*后期再优化*/
//                    JSONObject jSONObjectNew = new JSONObject();
//                    jSONObjectNew.put(API_TYPE, api_type + "_ACK");
//                    jSONObjectNew.put("status", "1");
//                    jSONObjectNew.put("contents", "");
//                    MessageLog messageLog;
//                    WritePaidLog writePaidLog = new WritePaidLog();
//                    switch (deviceResult.getResultCode()) {
//
//                        case DeviceResult.DEVICE_DISABLE:
//                            result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_DEVICE_DISABLE);
//                            jSONObjectNew.put("msg", APIKeyword.LOGIN_RESULT_DEVICE_DISABLE);
//                            if (xjhConncter.getWebSocket() == null) {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getInteNetLink().getRemoteAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            } else {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getWebSocket().getRemoteSocketAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            }
//                            sendAndClose(parameterPackage, xjhConncter, jSONObjectNew.toString());
//                            break;
//                        case DeviceResult.DEVICE_NOACTIVE:
//                            result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_DEVICE_NOACTIVE);
//                            jSONObjectNew.put("msg", APIKeyword.LOGIN_RESULT_DEVICE_NOACTIVE);
//                            if (xjhConncter.getWebSocket() == null) {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getInteNetLink().getRemoteAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            } else {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getWebSocket().getRemoteSocketAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            }
//                            sendAndClose(parameterPackage, xjhConncter, jSONObjectNew.toString());
//                            break;
//                        case DeviceResult.NODEVICECODE:
//                            deviceManager.addDevice(userName, deviceCode, "");
//                            result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_DEVICE_NOACTIVE);
//                            jSONObjectNew.put("msg", APIKeyword.LOGIN_RESULT_DEVICE_NOACTIVE);
//                            if (xjhConncter.getWebSocket() == null) {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getInteNetLink().getRemoteAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            } else {
//                                writePaidLog.writeLogTest("给" + xjhConncter.getWebSocket().getRemoteSocketAddress().toString() + "答复关闭连接消息2:-->" + jSONObjectNew.toString());
//                            }
//                            sendAndClose(parameterPackage, xjhConncter, jSONObjectNew.toString());
//                            break;
//                    }
//                    return;
//                }
//                //设备登陆成功了，更新设备信息。
//                deviceManager.updateDevice(userName, deviceCode, "");
//            }

//            List<ParameterPackageSession> parameterPackageSessions = parameterPackage.getAllParameterPackageSessions();

                //如果不是h5用户，要踢人的
//            if (!account.getAccountUser().equals(APIKeyword.TRANSFER_STATION_IN_USER_NAME)) {
//
//                for (int i = 0; i < parameterPackageSessions.size(); i++) {
//                    ParameterPackageSession oneParameterPackageSession = parameterPackageSessions.get(i);
//                    Account accountExixt = oneParameterPackageSession.getAccount();
//                    if (account.getAccountUser().equals(accountExixt.getAccountUser())) {
//                        WritePaidLog writePaidLog = new WritePaidLog();
//                        writePaidLog.writeLogTest(oneParameterPackageSession.getXJHConncter().getInteNetLink().getRemoteAddress().toString() + "因为账号重复被踢下线:" + account.getAccountUser() + "-->OLD::" + accountExixt.getAccountUser());
////                        System.out.println("因为账号重复被踢下线,当前连接:"+xjhConncter.getInteNetLink().hashCode()+","+xjhConncter.getInteNetLink().getRemoteAddress().toString()+",要踢下线连接:" + oneParameterPackageSession.getXJHConncter().getInteNetLink().hashCode() + "," + oneParameterPackageSession.getXJHConncter().getInteNetLink().getRemoteAddress().toString());
//                        rejectAccount(parameterPackage, oneParameterPackageSession);
//                        break;
//                    }
//
//                }
//            }

//            ParameterPackageSession parameterPackageSession = new ParameterPackageSession(parameterPackage, serverN.getXJHConncter(inteNetLink));
//            parameterPackageSession.setAccount(account);
//            parameterPackage.addParameterPackageSession(parameterPackageSession);
//            result.put(APIKeyword.API_LOGIN_RESULT, APIKeyword.LOGIN_RESULT_LOGIN_SUCCESS);
                /*后期再优化*/
                JSONObject jSONObjectNew = new JSONObject();
                jSONObjectNew.put(API_TYPE, api_type + "_ACK");
                jSONObjectNew.put("status", "0");
                jSONObjectNew.put("msg", "");
                // jSONObjectNew.put("storeId", LocalServerConfig.storeId);
                jSONObjectNew.put("storeId", 333);
                // jSONObjectNew.put("uiStyle", LocalServerConfig.uiStyle);
                jSONObjectNew.put("uiStyle", "");
                jSONObjectNew.put("client_ip", ws.getRemoteSocketAddress());

                JSONObject jSONObject1 = new JSONObject();
                jSONObject1.put("keeper_id", account.getAccountId());
                jSONObject1.put("keeper_name", account.getAccountUser());
                jSONObject1.put("realname", account.getAccountNickName());
                jSONObject1.put("type", 0);
                jSONObject1.put("store_name", storeDAO.fetchStoreName());
                jSONObjectNew.put("contents", jSONObject1);

                return jSONObjectNew;
            }
        } catch (Exception ex) {
            JSONObject jSONObjectNew = new JSONObject();
            jSONObjectNew.put(API_TYPE, api_type + "_ACK");
            jSONObjectNew.put("status", "1");
            jSONObjectNew.put("contents", "");
            jSONObjectNew.put("msg", LOGIN_RESULT_LOGIN_FAIL);
            return jSONObjectNew;
        }

        JSONObject jSONObjectNew = new JSONObject();
        jSONObjectNew.put(API_TYPE, api_type + "_ACK");
        jSONObjectNew.put("status", "1");
        jSONObjectNew.put("contents", "");
        jSONObjectNew.put("msg", LOGIN_RESULT_LOGIN_FAIL);
        return jSONObjectNew;
    }

    public Account login(String userName, String password, boolean isSave, boolean isRemote) throws SQLException, AccountNotFoundException, Exception {
        Account acount = new Account();
        acount.setAccountUser(userName);
        acount.setAccountPass(password);
        return login(acount, isSave, isRemote);
    }

    public Account login(String userName, String password, boolean isSave) throws SQLException, AccountNotFoundException, Exception {
        Account acount = new Account();
        acount.setAccountUser(userName);
        acount.setAccountPass(password);
        return login(acount, isSave);
    }


    public Account login(Account account, boolean isSave) throws SQLException, AccountNotFoundException, Exception {
        return login(account, isSave, false);
    }

    public Account login(Account account, boolean isSave, boolean isRemote) throws SQLException, AccountNotFoundException, Exception {

        Account verifyAccount = verifyAccount(account);
        if (verifyAccount != null) {
            if (isRemote == false) {
                if (isSave) {
                    //setAccountDefault(account.getAccountUser());
                } else {
                    //setAccountDefault("");
                }
            }

            return verifyAccount;
        }

        return null;
    }

    public Account verifyAccount(Account account) throws Exception {
        Account accountTrue = getAccount(account.getAccountUser());
        //if (accountTrue.getAccountPass().equals(account.getAccountPass()) || accountTrue.getAccountPass().equals(BenPaoMD5.toMD5(account.getAccountPass()))) {
        return accountTrue;
        //}
        //return null;
    }

    public Account getAccount(String accountUser) throws Exception {
        if (accountUser == null) {
            return null;
        }

        if (accountUser.trim().length() == 0) {
            return null;
        }
        return accountDAO.getByUserName(accountUser);
    }

    public Account getAccount(int accountId) throws Exception {
        if (accountId <= 0) {
            return null;
        }
        return accountDAO.getByAccountId(accountId);
    }
}
