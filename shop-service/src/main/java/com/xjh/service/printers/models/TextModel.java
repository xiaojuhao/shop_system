package com.xjh.service.printers.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TextModel {
    String name;
    int comType;
    String sampleContent;
    int size;
    int frontLen;
    int behindLen;
    int frontEnterNum;
    int behindEnterNum;
}
