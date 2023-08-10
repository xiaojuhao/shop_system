package com.xjh.service.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.service.vo.ManyCoupon;
import com.xjh.service.vo.PrePaidCard;
import com.xjh.service.vo.SerialNumber;

public class RemoteService {
    static String cardCouponUrl = "http://www.xjhjprl.cn/cardCoupon/index";

    public Result<PrePaidCard> getOnePrePaidCard(int storeId, String code) {
        Logger.info("储值卡查询请求: code=" + code + ", storeId=" + storeId);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "getOnePrePaidCard()");
        jSONObject.put("parameterType", "(String)");
        jSONObject.put("code", code);
        jSONObject.put("storeId", storeId);

        String rs = postJson(cardCouponUrl, jSONObject);
        Logger.info("储值卡查询结果: " + rs);
        JSONObject json = JSONObject.parseObject(rs);
        if (json.containsKey("isNull")) {
            boolean isNull = json.getBoolean("isNull");
            if (isNull) {
                return Result.fail("储值卡号不存在");
            }
        }
        PrePaidCard card = json.getObject("prePaidCard", PrePaidCard.class);
        if (card == null) {
            return Result.fail("查询储值卡失败");
        }
        if (card.getStatus() == 0) {
            return Result.fail("储值卡无效");
        }
        return Result.success(card);
    }

    public Result<String> prePaidCardConsume(int storeId, PrePaidCard prePaidCard, double amount, int orderId) {
        try {
            Logger.info("使用储值卡, 本次消费:" + amount + ", 储值卡信息:" + JSONObject.toJSONString(prePaidCard));
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("API_TYPE", "prePaidCardConsume()");
            jSONObject.put("storeId", storeId);
            jSONObject.put("operator", CurrentAccount.currentAccountCode());
            jSONObject.put("prePaidCard", prePaidCard.toJson());
            jSONObject.put("amount", amount);
            jSONObject.put("orderId", orderId);
            System.out.println("使用储值卡：请求地址:" + cardCouponUrl);
            System.out.println("使用储值卡：请求报文:" + jSONObject);
            String rs = postJson(cardCouponUrl, jSONObject);
            Logger.info("储值卡消费结果: " + rs);
            JSONObject json = JSONObject.parseObject(rs);
            int status = json.getInteger("status");
            if (status == 1) {
                return Result.success("");
            } else {
                return Result.fail("储值卡消费失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("使用储值卡失败:" + ex.getMessage());
        }
    }

    public void updatePrePaidCardBalance(int storeId, PrePaidCard prePaidCard, double newBalance) {
        Logger.info("更新储值卡余额, 余额:" + newBalance + ", 储值卡信息:" + prePaidCard.toString());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "updatePrePaidCardBalance()");
        jSONObject.put("storeId", storeId);
        jSONObject.put("operator", CurrentAccount.currentAccountCode());
        jSONObject.put("prePaidCard", prePaidCard.toJson());
        jSONObject.put("newBalance", newBalance);
        String rs = postJson(cardCouponUrl, jSONObject);
        Logger.info("更新储值卡余额: " + rs);
    }


    /**
     * 查询代金券
     */
    public Result<ManyCoupon> getManyCouponBy(int storeId, String code) {
        Logger.info("查询代金券请求: code=" + code + ", storeId=" + storeId);
        String whereString = " serial_number = '" + code + "'";
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "getManyCouponBy()");
        jSONObject.put("parameterType", "(String)");
        jSONObject.put("whereString", whereString);
        jSONObject.put("storeId", storeId);

        String rs = postJson(cardCouponUrl, jSONObject);
        Logger.info("查询代金券结果: " + rs);
        JSONObject json = JSONObject.parseObject(rs);
        if (json.containsKey("isNull")) {
            boolean isNull = json.getBoolean("isNull");
            if (isNull) {
                return Result.fail("代金券号不存在");
            }
        }
        if (!json.containsKey("manyCoupon")) {
            return Result.fail("代金券不存在");
        }
        ManyCoupon card = json.getObject("manyCoupon", ManyCoupon.class);
        if (card.getStatus() == 0) {
            return Result.fail("代金券已失效");
        }
        return Result.success(card);
    }

    public Result<SerialNumber> getOneSerialNumber(int storeId, String code) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "getOneSerialNumber()");
        jSONObject.put("parameterType", "(String)");
        jSONObject.put("code", code);
        jSONObject.put("storeId", storeId);

        String rs = postJson(cardCouponUrl, jSONObject);
        Logger.info("查询代金券结果: " + rs);
        JSONObject json = JSONObject.parseObject(rs);
        if (json.containsKey("isNull")) {
            boolean isNull = json.getBoolean("isNull");
            if (isNull) {
                return Result.fail("代金券号不存在");
            }
        }
        if (!json.containsKey("serialNumber")) {
            return Result.fail("代金券号不存在");
        }
        SerialNumber sn = json.getObject("serialNumber", SerialNumber.class);

        return Result.success(sn);
    }

    static String postJson(String url, JSONObject body) {
        return HttpUtil.createPost(url).contentType("application/json").body(JSONObject.toJSONString(body)).execute().body();
    }

    public static void main(String[] args) {
        RemoteService service = new RemoteService();
        Result<PrePaidCard> rs = service.getOnePrePaidCard(333, "123321123");
        System.out.println(JSONObject.toJSONString(rs, true));

        Result<String> cs = service.prePaidCardConsume(333, rs.getData(), 1, 1222);
        System.out.println(JSON.toJSON(cs));

        System.exit(0);
    }

}
