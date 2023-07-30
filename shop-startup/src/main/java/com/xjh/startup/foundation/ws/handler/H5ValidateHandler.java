package com.xjh.startup.foundation.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.DeskKey;
import com.xjh.dao.mapper.DeskKeyDAO;
import com.xjh.service.domain.DeskService;
import com.xjh.service.ws.WsApiType;
import com.xjh.startup.foundation.ws.WsHandler;
import org.java_websocket.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@WsApiType("h5Validate")
public class H5ValidateHandler implements WsHandler {
    @Inject
    DeskService deskService;
    @Inject
    DeskKeyDAO deskKeyDAO;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "h5Validate_ACK");
        //提供桌名，temKey。服务器进行判断。
        String openid = msg.getString("openid");
        String deskName = msg.getString("deskName");
        String temKey = msg.getString("temKey");
        if ("".equals(temKey) || temKey == null) {
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", "请扫小票的二维码!");
            return jSONObjectReturn;
        }

        try {
            Desk oneDesk = deskService.getByName(deskName).getData();
            DeskKey deskKey = deskKeyDAO.getByDeskId(oneDesk.getDeskId());
            if (deskKey == null) {
                jSONObjectReturn.put("status", 1);
                jSONObjectReturn.put("msg", "小票已过期!");
            } else if (temKey.equals(deskKey.getDeskKey())) {
                jSONObjectReturn.put("status", 0);
            } else {
                jSONObjectReturn.put("status", 1);
                jSONObjectReturn.put("msg", "h5扫描划菜单点菜失败!");
            }
        } catch (Exception ex) {
            jSONObjectReturn.put("status", 1);
            jSONObjectReturn.put("msg", "未知错误!");
        }
        return jSONObjectReturn;
    }
}
