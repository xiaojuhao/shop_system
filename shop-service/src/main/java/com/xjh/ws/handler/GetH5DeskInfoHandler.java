package com.xjh.ws.handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.java_websocket.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.xjh.common.enumeration.EnumDeskPhysicalStatus;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.service.domain.DeskService;
import com.xjh.ws.WsApiType;
import com.xjh.ws.WsHandler;

@Singleton
@WsApiType("h5DeskInfo")
public class GetH5DeskInfoHandler implements WsHandler {
    static int SUCCESS = 0;
    static int FAIL = 1;
    @Inject
    DeskService deskService;

    @Override
    public JSONObject handle(WebSocket ws, JSONObject msg) {
        //这边给出真正的id和桌子状态。
        //之后h5如果桌子是可以的，则getdatapackage,h5 ajax传给服务器保存。
        JSONObject jSONObjectReturn = new JSONObject();
        jSONObjectReturn.put("API_TYPE", "h5DeskInfo_ACK");
        //提供桌名，temKey。服务器进行判断。
        String deskName = msg.getString("deskName");

        try {
            Result<Desk> oneDeskRs = deskService.getByName(deskName);
            if (!oneDeskRs.isSuccess()) {
                jSONObjectReturn.put("status", FAIL);
                jSONObjectReturn.put("msg", oneDeskRs.getMsg());
                return jSONObjectReturn;
            }
            Desk oneDesk = oneDeskRs.getData();
            EnumDeskStatus status = EnumDeskStatus.of(oneDesk.getStatus());
            if (EnumDeskPhysicalStatus.of(oneDesk.getPhysicalStatus()) == EnumDeskPhysicalStatus.DISABLE) {
                jSONObjectReturn.put("status", FAIL);
                jSONObjectReturn.put("msg", "桌子被禁用!");
            } else if (status == EnumDeskStatus.FREE) {
                jSONObjectReturn.put("status", FAIL);
                jSONObjectReturn.put("msg", "还未开台，请联系服务员！");
            } else if (status == EnumDeskStatus.PRESERVED) {
                jSONObjectReturn.put("status", FAIL);
                jSONObjectReturn.put("msg", "桌子预约中...");
            } else {
                jSONObjectReturn.put("status", SUCCESS);
                jSONObjectReturn.put("useStatus", status.status());
                jSONObjectReturn.put("deskIdDb", oneDesk.getDeskId());
            }
        } catch (Exception ex) {
            jSONObjectReturn.put("status", FAIL);
            jSONObjectReturn.put("msg", "未知错误!");
        }
        return jSONObjectReturn;
    }
}
