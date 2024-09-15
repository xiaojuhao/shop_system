package com.xjh.service.domain.model;

import com.xjh.common.anno.UploadField;
import lombok.Data;

@Data
public class ConfigItem {
    @UploadField("配置名称")
    String key;
    @UploadField("配置值")
    String val;
    @UploadField("是否敏感信息")
    String sensitive;
    @UploadField("配置说明")
    String remark;
}
