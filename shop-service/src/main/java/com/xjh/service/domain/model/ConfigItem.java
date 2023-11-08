package com.xjh.service.domain.model;

import lombok.Data;

@Data
public class ConfigItem {
    String key;
    String val;
    String sensitive;
    String remark;
}
