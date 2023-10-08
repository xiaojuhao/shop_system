package com.xjh.service.domain;

import com.google.inject.Singleton;
import com.xjh.common.anno.FieldMeta;
import com.xjh.common.kvdb.impl.SysCfgDB;
import com.xjh.common.model.ConfigurationBO;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ReflectionUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xjh.common.utils.CommonUtils.stringify;

@Singleton
public class ConfigService {
    static SysCfgDB sysCfgDB = SysCfgDB.inst();

    public static ConfigurationBO loadConfiguration() {
        Map<String, ReflectionUtils.PropertyDescriptor> pdMap = ReflectionUtils.resolvePD(ConfigurationBO.class);
        ConfigurationBO bo = new ConfigurationBO();
        for (CO co : loadSysCfg()) {
            ReflectionUtils.PropertyDescriptor pd = pdMap.get(co.name);
            if (pd != null) {
                pd.writeValue(bo, co.value);
            }
        }
        return bo;
    }

    public static List<CO> loadSysCfg() {
        List<CO> coList = initCOList();
        for (CO co : coList) {
            String v = sysCfgDB.get(co.name, String.class);
            if (v != null && !v.trim().isEmpty()) {
                co.value = v;
            }
        }
        return coList;
    }

    public void saveCOList(List<CO> list) {
        if (list == null) {
            return;
        }
        for (CO co : list) {
            if (co == null) {
                continue;
            }
            if (CommonUtils.isNotBlank(co.value)  //
                    && co.value.contains("*")  //
                    && co.value.contains("【敏感数据】")) {
                continue;
            }
            sysCfgDB.put(co.name, co.value);
        }
    }

    public static String toProp(List<CO> list) {
        StringBuilder sb = new StringBuilder();
        for (CO co : list) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append("## ").append(co.remark).append("\n");
            if (co.mask && CommonUtils.isNotBlank(co.value)) {
                sb.append(co.name).append("=").append(CommonUtils.maskStr(co.value) + "【敏感数据】");
            }else {
                sb.append(co.name).append("=").append(co.value);
            }
        }
        return sb.toString();
    }

    public static List<CO> toCOList(String text) {
        List<CO> coList = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            if (line.trim().startsWith("#")) {
                continue;
            }
            if (!line.contains("=")) {
                continue;
            }
            int index = line.indexOf("=");
            CO co = new CO();
            co.name = line.substring(0, index);
            co.value = line.substring(index + 1);
            coList.add(co);
        }
        return coList;
    }

    private static List<CO> initCOList() {
        List<CO> coList = new ArrayList<>();
        ConfigurationBO cfginst = new ConfigurationBO();
        for (ReflectionUtils.PropertyDescriptor pd : ReflectionUtils.resolvePDList(ConfigurationBO.class)) {
            CO co = new CO();
            String name = pd.getField().getName();
            String value = stringify(pd.readValue(cfginst));
            co.name = name;
            co.value = value;
            FieldMeta meta = pd.getField().getAnnotation(FieldMeta.class);
            if (meta != null) {
                co.remark = meta.remark();
            }
            if (CommonUtils.isBlank(co.remark)) {
                co.remark = name + "注释";
            }
            if (meta != null && meta.mask() && CommonUtils.isNotBlank(co.value)) {
                co.mask = true;
            }
            coList.add(co);
        }
        return coList;
    }

    @Data
    public static class CO {
        String remark;
        String name;
        String value;

        boolean mask = false;
    }
}
