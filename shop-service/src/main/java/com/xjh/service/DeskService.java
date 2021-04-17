package com.xjh.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Desk;

public class DeskService {
    public List<Desk> getAllDesks() {
        List<Desk> desks = new ArrayList<>();
        try {
            URL url = DeskService.class.getResource("/config/desks.json");
            String data = CommonUtils.readFile(url.getFile());
            JSONArray json = JSON.parseArray(data);
            for (int i = 0; i < json.size(); i++) {
                JSONObject v = json.getJSONObject(i);
                Desk desk = new Desk();
                desk.setId(v.getLong("id"));
                desk.setDeskName(v.getString("name"));
                desks.add(desk);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }

    public List<Desk> getDesksRunningStatus() {
        List<Desk> desks = new ArrayList<>();
        try {
            URL url = DeskService.class.getResource("/config/desks.json");
            String data = CommonUtils.readFile(url.getFile());
            JSONArray json = JSON.parseArray(data);
            for (int i = 0; i < json.size(); i++) {
                JSONObject v = json.getJSONObject(i);
                Desk desk = new Desk();
                desk.setId(v.getLong("id"));
                desk.setDeskName(v.getString("name"));
                desks.add(desk);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desks;
    }
}
