package com.xjh.common.model;

import com.xjh.common.anno.FieldMeta;
import lombok.Data;

@Data
public class ConfigurationBO {
    @FieldMeta(remark = "门店ID(只读，勿动)", readonly = true)
    String storeId = "333";
    @FieldMeta(remark = "小票二维码URL地址")
    String tickedUrl = "http://www.xiaojuhao.org/pay/";
    @FieldMeta(remark = "小句号网站地址")
    String publicAddress = "http://www.xiaojuhao.org/pay/";
}
