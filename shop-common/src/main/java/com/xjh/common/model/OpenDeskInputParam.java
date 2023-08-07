package com.xjh.common.model;

import com.xjh.common.enumeration.OpenDeskResult;
import lombok.Data;

@Data
public class OpenDeskInputParam {
    OpenDeskResult result;
    int customerNum;
}
