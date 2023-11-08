package com.xjh.common.model;

import com.xjh.common.anno.FieldMeta;
import lombok.Data;

@Data
public class OssConfigBO {
    // OSS服务器地址
    String ossEndpoint = "";
    // OSS访问ID
    String ossAccessKeyId = "";
    // OSS访问秘钥
    String ossAccessKeySecret = "";
}
