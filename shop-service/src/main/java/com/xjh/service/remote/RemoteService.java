package com.xjh.service.remote;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CurrentAccount;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.StoreService;
import com.xjh.service.domain.model.StoreVO;
import com.xjh.service.vo.PrePaidCard;

import javax.inject.Inject;
import java.nio.charset.Charset;

public class RemoteService {
    static String cardCouponUrl = "http://www.xjhjprl.cn/cardCoupon/index";

    public Result<PrePaidCard> getOnePrePaidCard(int storeId, String code) {
        Logger.info("储值卡查询请求: code=" + code + ", storeId=" + storeId);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "getOnePrePaidCard()");
        jSONObject.put("parameterType", "(String)");
        jSONObject.put("code", code);
        jSONObject.put("storeId", storeId);

        String rs = HttpUtil.post(cardCouponUrl, jSONObject.toJSONString());
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
        Logger.info("使用储值卡, 本次消费:" + amount + ", 储值卡信息:" + prePaidCard.toString());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("API_TYPE", "prePaidCardConsume()");
        jSONObject.put("storeId", storeId);
        jSONObject.put("operator", CurrentAccount.currentAccountCode());
        jSONObject.put("prePaidCard", prePaidCard.toJson());
        jSONObject.put("amount", amount);
        jSONObject.put("orderId", orderId);
        String rs = HttpUtil.post(cardCouponUrl, jSONObject.toJSONString());
        Logger.info("储值卡消费结果: " + rs);
        JSONObject json = JSONObject.parseObject(rs);
        int status = json.getInteger("status");
        if (status == 1) {
            return Result.success("");
        } else {
            return Result.fail("储值卡消费失败");
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
        String rs = HttpUtil.post(cardCouponUrl, jSONObject.toJSONString());
        Logger.info("更新储值卡余额: " + rs);
    }

    public static void main(String[] args) {
        RemoteService service = new RemoteService();
        Result<PrePaidCard> rs = service.getOnePrePaidCard(333, "123321123");
        System.out.println(JSONObject.toJSONString(rs, true));

        service.prePaidCardConsume(333, rs.getData(), 1, 123123);
        System.exit(0);
    }

}
